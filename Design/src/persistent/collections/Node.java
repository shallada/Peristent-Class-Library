package persistent.collections;

public class Node implements PersistSerializable {
	private Pair[] pairs;
	private long index;
	private boolean isLeaf;

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
}
