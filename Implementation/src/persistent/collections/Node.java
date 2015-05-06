package persistent.collections;

import java.nio.ByteBuffer;

public class Node<E extends Persistable> implements Persistable {
	private Pair<E extends Persistable>[] pairs;
	private long index;
	private boolean isLeaf;

	public Node() {

	}

	public Node(long index, int width, boolean isLeaf) {
		this.index = index;
	//	pairs = new Pair<E extends Persistable>[width];
		this.isLeaf = isLeaf;
	}

	public void put(int index, Pair p) {
		pairs[index]=p;
//		if(p.isHasNextIndex()){
//		   isLeaf = false;
//		}
	}

	public int getSize() {
		//Long + Boolean + (size of pair * num of pairs)
		return 0;
	}

	public ByteBuffer allocate(){
		return ByteBuffer.allocate(getSize());
	}

	public void Serialize(ByteBuffer buffer) {
		//serialize index
		//serialize isLeaf
		//for each pair
			//if pair is null
				//Write 0s for the length of pair.getSize()
			//else
				//call serialize on the pair and pass in buffer
	}

	public void Load(ByteBuffer buffer) {
		//index = first 8 bytes
		//isLeaf = next byte
		//for each pair
			//call load on the pair and pass in buffer
	}

	public Pair<E>[] getPairs() {
		return pairs;
	}

	public void setPairs(Pair<E>[] pairs) {
		this.pairs = pairs;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public ByteBuffer getAllocate() {
		return allocate;
	}

	public void setAllocate(ByteBuffer allocate) {
		this.allocate = allocate;
	}
}
