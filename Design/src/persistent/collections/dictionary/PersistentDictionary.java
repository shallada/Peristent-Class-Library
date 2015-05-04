package dictionary;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PersistentDictionary<K, V> implements Map<K, V>, Iterable<PersistentKVP<K, V>>{
	private PersistentArray persistentArray;
	private PersistentDictionaryMetadata metadata;
	private PersistentFactory<K> keyFactory;
	private PersistentFactory<V> valueFactory;
	
	/**
	 * Creates a Persistent dictionary with buckets number of buckets
	 * @param pa
	 * @param keyFactory
	 * @param valueFactory
	 * @param buckets
	 * @throws IOException
	 */
	public PersistentDictionary(PersistentArray pa, PersistentFactory<K> keyFactory, PersistentFactory<V> valueFactory, long buckets) throws IOException {
		persistentArray = pa;
		initialize(buckets);
	}
	
	/**
	 * creates a Persistent dictionary with 10,000 buckets
	 * @param pa
	 * @param keyFactory
	 * @param valueFactory
	 * @throws IOException
	 */
	public PersistentDictionary(PersistentArray pa, PersistentFactory<K> keyFactory, PersistentFactory<V> valueFactory) throws IOException {
		persistentArray = pa;
		if(pa.getRecordCount() == 0) {
			initialize(10000);
		}
	}
	
	/**
	 * create buckets
	 * @param buckets
	 * @throws IOException
	 */
	private void initialize(long buckets) throws IOException {
		assert(persistentArray.getRecordCount() == 0);

		metadata = new PersistentDictionaryMetadata(persistentArray.getMetadata());
		metadata.buckets = buckets;
		PersistentKVP<K, V> empty = new PersistentKVP<K, V>(null, null, null, null);
		for(int i = 0; i < buckets; i++) {
			persistentArray.allocate();
			persistentArray.put(i, empty.toBytes());
		}
	}

	/**
	 * find value stored at key
	 * @return value, or null if key is not found
	 */
	@Override
	public V get(Object key) {
		try{
			V value = null;
			long index = getIndex(key);
			ByteBuffer data = persistentArray.get(index);
			PersistentKVP<K, V> bucket = new PersistentKVP<K, V>(data, keyFactory, valueFactory);
			
			if(!bucket.isEmpty()) {
				// find bucket with matching key
				bucket = findBucketContainingKey(bucket, key);
				
				if(bucket.getKey().equals(key)) {
					value = bucket.getValue();
				}
			}
			return value;
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * puts value at key and returns value
	 */
	@Override
	public V put(K key, V value) {
		
		if(key == null || value == null) {
			throw new NullPointerException();
		}
		
		try{
			long index = getIndex(key);
			PersistentKVP<K, V> kvpToAdd = new PersistentKVP<K, V>(key, value, keyFactory, valueFactory);
			ByteBuffer data = persistentArray.get(index);
			PersistentKVP<K, V> bucket = new PersistentKVP<K, V>(data, keyFactory, valueFactory);
			long next = bucket.getNext();
			
			if(bucket.isEmpty()) {
				persistentArray.put(index, kvpToAdd.toBytes());
			}
			else {
				boolean inserted = false;
				// find bucket with matching key. While keeping track of the index
				while(!bucket.isEndOfList() && !inserted) {
					index = next;
					data = persistentArray.get(next);
					bucket = new PersistentKVP<K, V>(data, keyFactory, valueFactory);
					next = bucket.getNext();
					if(key.equals(bucket.getKey())) {
						bucket.setValue(value);
						persistentArray.put(index, bucket.toBytes());
						inserted = true;
					}
				}
				// if bucket wasn't found, make one at index
				if(!inserted) {
					long insertIndex = persistentArray.allocate();
					bucket.setNext(insertIndex);
					persistentArray.put(index, bucket.toBytes());
					persistentArray.put(insertIndex, kvpToAdd.toBytes());
				}
			}
			metadata.incrementEntries();
			metadata.persist();
			persistentArray.persistMetadata();
			return value;
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * remove key and value, return value
	 * returns null if key doesn't exist
	 */
	@Override
	// removes the key and returns its value
	// will return null if the key doesn't exist
	public V remove(Object key) {
		try{
			V value = null;
			long prevIndex = -1;
			long index = getIndex(key);
			ByteBuffer data = persistentArray.get(index);
			PersistentKVP<K, V> bucket = new PersistentKVP<K, V>(data, keyFactory, valueFactory);
			PersistentKVP<K, V> prevBucket = null;
			
			if(!bucket.isEmpty()) {
				// find bucket with matching key. While keeping track of the index and previous bucket
				while(!bucket.getKey().equals(key) && !bucket.isEndOfList()) {
					prevIndex = index;
					prevBucket = bucket;
					index = bucket.getNext();
					data = persistentArray.get(index);
					bucket = new PersistentKVP<K, V>(data, keyFactory, valueFactory);
				}
				
				// delete bucket
				if(bucket.getKey().equals(key)) {
					value = remove(bucket, prevBucket, index, prevIndex);
				}
			}
			return value;
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private V remove(PersistentKVP<K, V> bucket, PersistentKVP<K, V> prevBucket, long index, long prevIndex) throws IOException {
		V value = bucket.getValue();
		persistentArray.delete(index);
		
		// connect previous bucket to next bucket
		if(!bucket.isEndOfList()) {
			prevBucket.setNext(bucket.getNext());
			persistentArray.put(prevIndex, prevBucket.toBytes());
			
			metadata.decrementEntries();
			metadata.persist();
			persistentArray.persistMetadata();
		}
		else {
			prevBucket.setNext(PersistentKVP.END_OF_LIST);
		}
		return value;
	}

	/**
	 * returns true if key exists
	 */
	@Override
	public boolean containsKey(Object key) {
		try{
			long index = getIndex(key);
			ByteBuffer data = persistentArray.get(index);
			PersistentKVP<K, V> bucket = new PersistentKVP<K, V>(data, keyFactory, valueFactory);
			
			boolean exists = false;
			if(!bucket.isEmpty()) {
				// find bucket with matching key
				bucket = findBucketContainingKey(bucket, key);
				exists = bucket.getKey().equals(key);
			}
	
			return exists;
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * returns number of key value pairs stored
	 */
	@Override
	public int size() {
		return (int)metadata.getEntries();
	}

	/** 
	 * returns true if no key value pairs are stored
	 */
	@Override
	public boolean isEmpty() {
		return metadata.getBuckets() == 0;
	}
	
	private long getIndex(Object o) {
		int hash = o.hashCode();
		return hash % metadata.getBuckets();
	}
	
	private PersistentKVP<K, V> findBucketContainingKey(PersistentKVP<K, V> bucket, Object key) throws IOException {
		while(!bucket.getKey().equals(key) && !bucket.isEndOfList()) {
			long index = bucket.getNext();
			ByteBuffer data = persistentArray.get(index);
			bucket = new PersistentKVP<K, V>(data, keyFactory, valueFactory);
		}
		return bucket;
	}
	
	public static int additionalSizeInBytes() {
		return PersistentKVP.additionalSizeInBytes();
	}
	
	public static int metadataSizeInBytes() {
		return 16;//PersistentDictionaryMetadata.sizeInBytes();
	}
	
	@Override
	public Iterator<PersistentKVP<K, V>> iterator() {
		try {
			return new PersistentKVPIterator();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private class PersistentKVPIterator implements Iterator<PersistentKVP<K, V>> {
		private long bucket = 0;
		private PersistentKVP<K, V> next;
		private long nextIndex = -1;
		
		
		PersistentKVPIterator() throws IOException {
			ByteBuffer data = persistentArray.get(bucket);
			next = new PersistentKVP<K, V>(data, keyFactory, valueFactory);
			next();
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public PersistentKVP<K, V> next() {
			try {
				
				PersistentKVP<K, V> current = next;
				setNext();
				return current;
				
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		private void setNext() throws IOException {
			do {
				if(next.isEndOfList()) {
					if(bucket == metadata.buckets - 1) {
						next = null;
					}
					else {
						nextIndex = bucket;
					}
				}
				else {
					nextIndex = next.getNext();
				}
				
				if(hasNext()) {
					ByteBuffer data = persistentArray.get(nextIndex);
					next = new PersistentKVP<K, V>(data, keyFactory, valueFactory);
				}
			} 
			while(next.isEmpty() && bucket < metadata.buckets - 1);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	private class PersistentDictionaryMetadata {
		private ByteBuffer buffer;
		private long buckets = 0;
		private long entries = 0;
		private int bytes = 8 + 8;
		
		public PersistentDictionaryMetadata(ByteBuffer buffer) {
			this.buffer = buffer;
			buckets = buffer.getLong();
			entries = buffer.getLong();
		}
		
		public long getBuckets() {
			return buckets;
		}
		
		public long getEntries() {
			return entries;
		}
		
		public void incrementEntries() {
			entries++;
		}
		
		public void decrementEntries() {
			entries--;
		}
		
		public void persist() {
			buffer.position(0);
			buffer.putLong(buckets);
			buffer.putLong(entries);
		}
		
		public int sizeInBytes() {
			return bytes;
		}
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}
	
}
