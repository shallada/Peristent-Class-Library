package persistent.collections;

public class Node<E extends Persistable> implements Persistable {
	private Pair<E>[] pairs;
	private long index;
	private boolean isLeaf;

	public Node() {

	}

	public Node(long index, int width, boolean isLeaf) {
		//set values
		//instatiate pairs to an array of size width
	}

	public void put(int index, Pair p) {
		//Put p in pairs at index
		//If p has a reference instead of a value, set isLeaf to false
	}
	/*
	Getters and setters
	*/

	public int getSize() {
		//Long + Boolean + (size of pair * num of pairs)
	}

	public ByteBuffer allocate {
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
}
