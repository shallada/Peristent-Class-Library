/**
 * Author: Neumont University CSC360 - Section 1: Spring 2015
 */
package persistent.collections;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

public class BinaryTree<E extends Persistable & Comparable> implements Iterable {
	
	private PersistentArray backingStructure;
	private long root;
	private long length;
	private Persistable generic;
	
	/**
	 * Constructor
	 * @param _backingStructure: The backing persistent data structure
	 */
	public BinaryTree(PersistentArray _backingStructure, Persistable genericInstance){
		this.backingStructure = _backingStructure;
		length = 0;
		this.generic = genericInstance;
	}
	
	/**
	 * Insert a value into the tree.
	 * @param value: value to be inserted
	 * @return: reference to the node where the value was stored
	 * @throws Exception if value already exists in the tree
	 */
	public BinaryTreeNode insert(E value) throws Exception{
		return insert(value, root);
	}
	private BinaryTreeNode insert(E value, long nodeIndex) throws Exception{
		BinaryTreeNode node = new BinaryTreeNode();
		node.Load(backingStructure.get(nodeIndex));
		if(node.value == value)
			throw new Exception("value already exists in tree");
		if(value.compareTo(node.value) < 0){
			if(getNode(node.left).value.compareTo(value) < 0){
				BinaryTreeNode n = new BinaryTreeNode(value);
				n.left = node.left;
				node.left = putNode(n);
				return n;
			} else {
				return insert(value, node.left);
			}
		} else {
			if(getNode(node.right).value.compareTo(value) < 0){
				return insert(value, node.right);
			} else {
				BinaryTreeNode n = new BinaryTreeNode(value);
				n.right = node.right;
				node.right = putNode(n);
				return n;
			}
		}
	}
	private BinaryTreeNode getNode(long index){
		BinaryTreeNode node = new BinaryTreeNode();
		try { node.Load(backingStructure.get(index)); } 
		catch (IOException e) { node = null; }
		return node;
	}
	private long putNode(BinaryTreeNode node) throws IOException{
		ByteBuffer buffer = ByteBuffer.allocate(node.GetSize());
		node.Serialize(buffer);
		long index = backingStructure.allocate();
		backingStructure.put(index, buffer);
		return index;
	}
	
	/**
	 * Remove a value from the tree.
	 * @param value: value to be removed from the tree
	 * @throws Exception: value is not found in the tree
	 */
	public void delete(E value) throws Exception { 
		deleteNode(find(value));
	}
	private void deleteNode(long index) throws IOException{
		if(getNode(index).left == -1){
			if(getNode(index).right == -1){
				backingStructure.delete(index);
			} else {
				index = getNode(index).right;
				backingStructure.delete(index);
			}
		} else if(getNode(index).right == -1){
			index = getNode(index).left;
			backingStructure.delete(index);
		} else {
			backingStructure.delete(getNode(index).right);
		}
	}
	
	/**
	 * Find a value in the tree.
	 * @param value: value to be found in the tree
	 * @return: reference to the node where that value was found
	 * @throws Exception: value not found in the tree
	 */
	public long find(E value) throws Exception {
		long found = find(value, root);
		if (getNode(found) == null) throw new Exception("Item Not Found");
		else return found;
	}
	private long find(E value, long index){
		BinaryTreeNode node = getNode(index);
		if(node == null || node.value == value)
			return index;
		else if(value.compareTo(node.value) < 0)
			return find(value, node.left);
		else
			return find(value, node.right);
	}
	
	public class BinaryTreeNode implements Persistable {
		
		private E value;
		private long right = -1; 
		private long left = -1;  
		
		public BinaryTreeNode(E _value){
			value = _value;
		}
		public BinaryTreeNode(){
			
		}
		
		public E getValue(){
			return value;
		}
		public long getRight(){
			return right;
		}
		public long getLeft(){
			return left;
		}
		@Override
		public int GetSize(){
			return generic.GetSize() + (Long.BYTES * 2);
		}
		@Override 
		public void Serialize(ByteBuffer buffer){
			buffer.putLong(left);
			buffer.putLong(right);
			value.Serialize(buffer);
			buffer.flip();
		}
		@Override 
	    public void Load(ByteBuffer buffer){
			left = buffer.getLong();
			right = buffer.getLong();
			value.Load(buffer);
		}
	}

	@Override
	public Iterator<BinaryTreeNode> iterator() {
		Iterator<BinaryTreeNode> it = null;
		try { it = new BinaryTreeIterator(); } 
		catch (IOException e) { }
		return it;
	}
	
	class BinaryTreeIterator implements Iterator<BinaryTreeNode> {

		private long next = 0;
		private long total = backingStructure.getRecordCount();
		private long current = 0;
		
		public BinaryTreeIterator() throws IOException{
			BinaryTreeNode rootNode = new BinaryTreeNode();
			rootNode.Load(backingStructure.get(next++));
		}
		
		@Override
		public boolean hasNext() {
			return current < total;
		}

		@Override
		public BinaryTreeNode next() {
			BinaryTreeNode node = new BinaryTreeNode();
			try { 
				node.Load(backingStructure.get(next++));
				current++;
			} catch (IOException e) { e.printStackTrace(); }
			return node;
		}
	}
}
