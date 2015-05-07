package persistent.collections.LinkedList;

import persistent.Persistable;

import java.nio.ByteBuffer;

public class LinkedListNode<T extends Persistable> implements Persistable {
	protected long previous;
	protected long next;
	protected T value;
	
	public LinkedListNode(T val) {
		this.value = val;
		this.previous = -1;
		this.next = -1;
	}
	
	@Override
	public int getSize() {
		return (Long.BYTES * 2) + value.getSize();
	}
	
	@Override
	public void serialize(ByteBuffer buffer) {
		buffer.putLong(previous);
		buffer.putLong(next);
		value.serialize(buffer);
	}
	
	@Override
	public void load(ByteBuffer buffer) {
		this.previous = buffer.getLong();
		this.next = buffer.getLong();
		this.value.load(buffer);
	}
}
