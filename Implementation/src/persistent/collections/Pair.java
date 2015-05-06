package persistent.collections;

public class Pair<E extends Persistable> implements Persistable{
	private long key;
	private E value;
	private long nextIndex;
	private boolean hasNextIndex; //Needed so that when we load the data, if nextIndex = 0 
				      //we know whether or not 0 represents a lack of data or a reference

	public Pair(){

	}

	public Pair(long key, E value){
		this.key = key;
		this.value = value;
	}

	/*
	Getters and setters
	*/

	public int getSize() {
		//long + E.getSize() + long + boolean
		return 0;
	}

	public ByteBuffer allocate() {
		//ByteBuffer.allocate(getSize())
		return null;
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

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public E getValue() {
		return value;
	}

	public void setValue(E value) {
		this.value = value;
	}

	public long getNextIndex() {
		return nextIndex;
	}

	public void setNextIndex(long nextIndex) {
		this.nextIndex = nextIndex;
	}

	public boolean isHasNextIndex() {
		return hasNextIndex;
	}

	public void setHasNextIndex(boolean hasNextIndex) {
		this.hasNextIndex = hasNextIndex;
	}
}
