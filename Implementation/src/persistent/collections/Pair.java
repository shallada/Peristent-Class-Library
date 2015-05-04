package persistent.collections;

public class Pair<E extends Persistable> implements Persistable{
	private long key;
	private E value;
	private long nextIndex;
	private boolean hasNextIndex; //Needed so that when we load the data, if nextIndex = 0 
				      //we know whether or not 0 represents a lack of data or a reference

	/*
	Constructors (Parameterless and one that sets key, value, nextIndex)
	*/

	/*
	Getters and setters
	*/

	public int getSize() {
		//long + E.getSize() + long + boolean
	}

	public ByteBuffer allocate() {
		//ByteBuffer.allocate(getSize())
	}

	public void serialize(ByteBuffer buffer) {
		//write key
		//write nextIndex
		//write hasNextIndex
		//call serialize on the value with this buffer
	}

	public void load(ByteBuffer buffer) {
		//first 8 bytes = key
		//next 8 bytes = nextIndex
		//next 8 bytes = hasNextIndex
		//if value is null
			//write 0's for the length of value's size
		//else
			//call load on value
	}
}
