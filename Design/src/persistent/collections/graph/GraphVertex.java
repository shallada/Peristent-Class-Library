package persistent.collctions.graph;

import java.nio.ByteBuffer;

public class GraphVertex<E> {
	
	private long firstEdge;
	private E value;
	private long index;
	private PersistentFactory<E> factory;
	
	public static final int SIZE = Long.BYTES * 2;
	
	public GraphVertex(PersistentFactory<E> factory) {
		this.factory = factory;
	}
	
	public E getValue() {
		return value;
	}
	public void setValue(E value) {
		this.value = value;
	}
	
	public long getIndex() {
		return index;
	}
	
	public void setIndex(long index) {
		this.index = index;
	}
	public long getFirstEdge() {
		return firstEdge;
	}
	public void setFirstEdge(long firstEdge) {
		this.firstEdge = firstEdge;
	}
	
	public void serialize(ByteBuffer buffer) {

		//put the first edge and index in the buffer 
		buffer.putLong(firstEdge);
		buffer.putLong(index);
		
		//make the value serialize itself
		
		factory.serialize(buffer, value);
		
	}

	public void load(ByteBuffer buffer) {
		buffer.flip();
		//load the first edge and index
		firstEdge = buffer.getLong();
		index = buffer.getLong();
		
		//use the factory to deserialize the value
		value = factory.deserialize(buffer);
	}
	
	public int getSize() {
		return SIZE + factory.getSize();
	}
	
}
