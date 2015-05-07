package dictionary;

import java.nio.ByteBuffer;

public class PersistentKVP<K, V> {
	public static final long EMPTY = -2;
	public static final long END_OF_LIST = -1;
	
	private K key = null;
	private V value = null;
	private long next = EMPTY;
	
	private PersistentFactory<K> keyFactory;
	private PersistentFactory<V> valueFactory;
	
	public PersistentKVP(K key, V value, PersistentFactory<K> keyFactory, PersistentFactory<V> valueFactory) {
		this.keyFactory = keyFactory;
		this.valueFactory = valueFactory;
		this.key = key;
		this.value = value;
		if(key != null)
			next = END_OF_LIST;
	}
	
	public PersistentKVP(ByteBuffer data, PersistentFactory<K> keyFactory, PersistentFactory<V> valueFactory) {
		data.position(0);
		this.keyFactory = keyFactory;
		this.valueFactory = valueFactory;
		key = keyFactory.fromBuffer(data);
		value = valueFactory.fromBuffer(data);
		next = data.getLong();
	}
	
	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	public long getNext() {
		return next;
	}
	
	public void setValue(V value) {
		this.value = value;
	}
	
	public ByteBuffer toBytes() {
		ByteBuffer buffer = null;
		int size = 8 + keyFactory.sizeInBytes() + valueFactory.sizeInBytes();
		buffer = ByteBuffer.allocate(size);
		if( next != EMPTY) {
			keyFactory.toBuffer(buffer, key);
			valueFactory.toBuffer(buffer, value);
		}
		else {
			buffer.position(keyFactory.sizeInBytes() + valueFactory.sizeInBytes());
		}
		buffer.putLong(next);
		buffer.flip();
		return buffer;
	}

	public void setNext(long next) {
		this.next = next;
	}
	
	public boolean isEmpty() {
		return next == EMPTY;
	}
	
	public boolean isEndOfList() {
		return next <= END_OF_LIST;
	}
	
	public static int additionalSizeInBytes() {
		return 8;
	}
	
}