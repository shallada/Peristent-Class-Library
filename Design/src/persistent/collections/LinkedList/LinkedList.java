package persistent.collections.LinkedList;

import persistent.Persistable;
import persistent.collections.PersistentArray;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.sql.rowset.Predicate;

import persistent.Persistable;
import persistent.collections.PersistentArray;

public class LinkedList<T extends Persistable> implements Iterable<T> {
	private PersistentArray pa;
	private long head;
	private long count;
	private ByteBuffer metadata;
	private Class<T> valueClass;
	
	/**
	 * Creates an instance of this LinkedList using the passed in PersistentArray as its storage source and the class of the generic type used in this list for deserialization
	 * @param pa The PersistentArray to e used with this LinkeList
	 * @param payloadClass The class of the generic type being stored in this LinkedList
	 */
	public LinkedList(PersistentArray pa, Class<T> payloadClass) {
		this.pa = pa;
		this.valueClass = payloadClass;
		
		try {
			this.metadata = pa.getMetadata();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.head = metadata.getLong();
		this.count = metadata.getLong();
		
		if (count <= 0) {
			initializePA();
		}
	}
	
	/**
	 * Initializes the Persistant Array with a single node should none exist
	 */
	private void initializePA() {
		try {
			long index = pa.allocate();
			T val = null;
			LinkedListNode<T> node = new LinkedListNode<T>(val);
			node.previous = index;
			node.next = index;
			ByteBuffer buffer = node.allocate();
			node.serialize(buffer);
			pa.put(index, buffer);
			head = index;
			putMetadata();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the size of the metadata used by this LinkedList for its operations
	 * @return The size, in bytes, of the metadata for this LinkedList
	 */
	public static int getMDSize() {
		return Long.BYTES * 2;
	}
	
	/**
	 * Returns the size of the records used by this LinkedList
	 * @param payload An instance of the type stored in this LinkedList
	 * @return The total size, in bytes, of the record to be stored in the PersistentArray
	 */
	public static <T extends Persistable> int getRecSize(T payload) {
		return (Long.BYTES * 2) + payload.getSize();
	}
	
	/**
	 * Creates an instance of the generic type stored within this LinkedList
	 * @return an instance of T.
	 */
	private T createInstance() {
		T val = null;
		try {
			val = valueClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new IllegalStateException("Failed to instantiate generic type");
		}
		
		return val;
	}
	
	/**
	 * Writes the given node's data to the given index in the Persistant array
	 * @param index Location to write the node to
	 * @param node The node to write to the Persistant Array
	 * @throws IOException
	 */
	private void persistNode(long index, LinkedListNode<T> node) throws IOException {
		ByteBuffer buffer = node.allocate();
		node.serialize(buffer);
		pa.put(index, buffer);
	}
	
	/**
	 * Writes the metadata associated with this object to the metadata buffer and sends it down the chain to be serialized
	 */
	private void putMetadata() {
		this.metadata.position(0);
		this.metadata.putLong(head);
		this.metadata.putLong(count);
		
		try {
			pa.persistMetadata();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the node located at the given index in the Persistant Array by creating a new node and populating it with the data located at the given index
	 * @param index The location of the node to load
	 * @return  The node at the given index
	 * @throws IOException
	 */
	private LinkedListNode<T> getNode(long index) throws IOException {
		ByteBuffer nodeData = null;
		nodeData = pa.get(index);
		
		T val = createInstance();
		LinkedListNode<T> deserializedNode = new LinkedListNode<T>(val);
		deserializedNode.load(nodeData);
		return deserializedNode;
	}
	
	/**
	 * Adds the given value to the position before the given node reference in the LinkedList and adds it to the PersistentArray
	 * @param nodeRef The index the node that is being added before is located at
	 * @param val the value to be added to the LinkedList
	 * @return The index that the newly added node
	 * @throws IOException
	 */
	public long addBefore(long nodeRef, T val) throws IOException {
		LinkedListNode<T> newNode = new LinkedListNode<T>(val);
		
		long newIndex = -1;
		newIndex = pa.allocate();
		
		LinkedListNode<T> addingBefore = getNode(nodeRef);
		LinkedListNode<T> addingAfter = getNode(addingBefore.previous);
		addingAfter.next = newIndex;
		newNode.previous = addingBefore.previous;
		addingBefore.previous = newIndex;
		newNode.next = nodeRef;
		persistNode(newIndex, newNode);
		count++;
		putMetadata();
		persistNode(nodeRef, addingBefore);
		persistNode(newNode.previous, addingAfter);
		return newIndex;
	}
	
	/**
	 * Adds the given value to the position after the given node reference in the LinkedList and adds it to the PersistentArray
	 * @param nodeRef The index of the of the node that is being added after
	 * @param val The value to be added to the LinkedList
	 * @return The index of the newly added node
	 * @throws IOException
	 */
	public long addAfter(long nodeRef, T val) throws IOException {
		LinkedListNode<T> newNode = new LinkedListNode<T>(val);
		long newIndex = -1;
		newIndex = pa.allocate();
		
		LinkedListNode<T> addingAfter = getNode(nodeRef);
		LinkedListNode<T> addingBefore = getNode(addingAfter.next);
		addingBefore.previous = newIndex;
		newNode.next = addingAfter.next;
		addingAfter.next = newIndex;
		newNode.previous = nodeRef;
		persistNode(newIndex, newNode);
		count++;
		putMetadata();
		persistNode(nodeRef, addingAfter);
		persistNode(newNode.next, addingBefore);
		return newIndex;
	}
	
	/**
	 * Adds the given to front of the LinkedList
	 * @param val The value to be added to the LinkedList
	 * @return The index of the newly added node
	 * @throws IOException
	 */
	public long addToFront(T val) throws IOException {
		return addAfter(head, val);
	}
	
	/**
	 * Adds the given to end of the LinkedList
	 * @param val The value to be added to the LinkedList
	 * @return The index of the newly added node
	 * @throws IOException
	 */
	public long addToEnd(T val) throws IOException {
		return addBefore(head, val);
	}
	
	/**
	 * Removes the first occurance of the given value from the LinkedList if it is in the list. If the value if not present in the list an exception is thrown.
	 * @param val The value to remove
	 * @throws IOException
	 */
	public void remove(T val) throws IOException {
		if (val == null) {
			throw new NullPointerException("Provided value is null");
		}
		
		long foundIndex = find(val);
		if (foundIndex > 0) {
			remove(foundIndex);
		} else {
			throw new IllegalArgumentException("The specified value is not in the list");
		}
	}
	
	/**
	 * Removes the node at the given index from the PersistentArray
	 * @param nodeRef The location of the node to remove
	 * @throws IOException
	 */
	public void remove(long nodeRef) throws IOException {
		if (nodeRef == head) {
			throw new IllegalStateException("Cannot delete the head node. At least one node must be present in the list");
		}
		
		LinkedListNode<T> toDelete = getNode(nodeRef);
		LinkedListNode<T> nextNode = getNode(toDelete.next);
		LinkedListNode<T> previousNode = getNode(toDelete.previous);
		previousNode.next = toDelete.next;
		nextNode.previous = toDelete.previous;
		count--;
		
		pa.delete(nodeRef);
		
		persistNode(nextNode.previous, previousNode);
		persistNode(previousNode.next, nextNode);
		putMetadata();
	}
	
	/**
	 * Gets the value of the node located at the given index from the Persistant Array
	 * @param nodeRef The index of the node to get the value of
	 * @return The value at the given index
	 * @throws IOException
	 */
	public T get(long nodeRef) throws IOException {
		LinkedListNode<T> containingNode = getNode(nodeRef);
		return containingNode.value;
	}
	
	/**
	 * Finds and returns the index of the first node containing the given value. If the value cannot be found -1 is returned
	 * @param val The value to search for
	 * @return The index of the node containing the given value or -1 if it could not be found
	 * @throws IOException
	 */
	public long find(T val) throws IOException {
		LinkedListNode<T> currentNode = getNode(head);
		long currentRef = head;
		long nextRef = currentNode.next;
		while (currentNode.value.equals(val) && nextRef != head) {
			currentRef = currentNode.next;
			currentNode = getNode(currentNode.next);
			nextRef = currentNode.next;
		}
		
		if (nextRef == head && !currentNode.value.equals(val)) {
			return -1;
		}
		
		return currentRef;
	}
	
	/**
	 * Returns an iterator that returns values based on the given predicate
	 * @param predicate The condition to return values on
	 * @return An iterator using the given predicate
	 */
	public Iterator<T> findAll(Predicate<T> predicate) {
		return new LinkedListIterator(predicate);
	}
	
	/**
	 * Returns the number of items on the LinkedList
	 * @return Number of items in the LinkedList
	 */
	public long getListSize() {
		return count;
	}
	
	/**
	 * Returns whether or not the given value is in the LinkedList
	 * @param val The value to search for
	 * @return True is the value is in the list. False otherwise
	 * @throws IOException
	 */
	public boolean contains(T val) throws IOException {
		boolean containsVal = true;
		long foundIndex = find(val);
		if (foundIndex < 0) {
			containsVal = false;
		}
		
		return containsVal;
	}
	
	/**
	 * Returns an iterator to iterate over all the elements in the LinkedList
	 */
	public Iterator<T> iterator() {
		return new LinkedListIterator(t -> true);
	}
	
	/**
	 * An iterator that takes a predicate into it's constructor. Using the predicate, this iterator determines whether 
	 * or not there are any elements that satisfy the predicate condition
	 * and returns those values
	 */
	private class LinkedListIterator implements Iterator<T> {
		private Predicate<T> predicate;
		private LinkedListNode<T> currentNode;

		public LinkedListIterator(Predicate<T> predicate) {
			try {
				currentNode = getNode(head);
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalStateException("Could not load head node from PersistentArray");
			}
			this.predicate = predicate;
		}
		
		/**
		 * Using the predicate, returns whether or not there are any remaining elements that satisfy the predicate's condition
		 */
		@Override
		public boolean hasNext() {
			long nextRef = currentNode.next;
			while(!predicate.test(currentNode.value) && nextRef != head) {
				try {
					currentNode = getNode(nextRef);
				} catch (IOException e) {
					e.printStackTrace();
					throw new IllegalStateException("Could not load the node located at index " + nextRef + ". This indicates a break in the list and that something may be wrong with the implementation");
				}
				nextRef = currentNode.next;
			}
			
			return predicate.test(currentNode.value) || nextRef != head;
		}
		
		/**
		 * Returns the next element or throws an exception if there are no remaining elements
		 */
		@Override
		public T next() {
			if (!hasNext()) {
				throw new NoSuchElementException("There are no elements remaining");
			}
			
			T value = currentNode.value;
			try {
				currentNode = getNode(currentNode.next);
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalStateException("Could not load node located at index " + currentNode.next);
			}
			return value;
		}
	}
}
