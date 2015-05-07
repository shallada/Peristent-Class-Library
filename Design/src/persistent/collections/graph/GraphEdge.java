package persistent.collctions.graph;

import java.nio.ByteBuffer;

public class GraphEdge {
	
	private long source;
	private long destination;
	private long nextEdge;
	private long index;
	
	public static final int SIZE = Long.BYTES * 4;
	
	public GraphEdge(long index, long source, long destination) {
		this.destination = destination;
		nextEdge = -1;
	}
	
	public GraphEdge() {
		nextEdge = -1;
	}
	
	public long getDestination() {
		return destination;
	}
	public void setDestination(long destination) {
		this.destination = destination;
	}
	public long getNextEdge() {
		return nextEdge;
	}
	public void setNextEdge(long nextEdge) {
		this.nextEdge = nextEdge;
	}

	/**
	 * the edge's index; a value of -1 indicates that this edge has been deleted
	 * @return
	 */
	public long getIndex() {
		return index;
	}


	/**
	 * Set the edge's index; a value of -1 indicates that this edge has been deleted
	 * @return
	 */
	public void setIndex(long index) {
		this.index = index;
	}

	public long getSource() {
		return source;
	}

	public void setSource(long source) {
		this.source = source;
	}

	public void serialize(ByteBuffer buffer) {

		//put all of the values in the buffer in the order source, destination, nextEdge, index
		buffer.putLong(source);
		buffer.putLong(destination);
		buffer.putLong(nextEdge);
		buffer.putLong(index);
		
	}

	public void load(ByteBuffer buffer) {
		buffer.flip();
		//load all of the values in the buffer in the order source, destination, nextEdge, index
		source = buffer.getLong();
		destination = buffer.getLong();
		nextEdge = buffer.getLong();
		index = buffer.getLong();
	}

	public int getSize() {
		return SIZE;
	}
	
}
