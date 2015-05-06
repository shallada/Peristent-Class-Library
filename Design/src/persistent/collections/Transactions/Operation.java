package persistent.collections.Transactions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.UUID;

import persistent.Persistable;
import persistent.collections.TransactionPersistentArray;

public abstract class Operation implements Persistable
{
	private long ref;
	private ByteBuffer data;
	private ByteBuffer oldData;
	private long recordSize;
	private UUID transactionPersistentArrayId;
	
	private static final int UUID_SIZE_IN_BYTES = 36;
	
    public abstract void execute(TransactionPersistentArray txnpa) throws IOException;
    public abstract void undo(TransactionPersistentArray txnpa) throws IOException, RollbackInterruptedException;
	
    public Operation(long ref, long recordSize, UUID id){
    	this.ref = ref;
    	this.recordSize = recordSize;
    	this.transactionPersistentArrayId = id;
    }
    
    public long getRecordSize() {
    	return this.recordSize;
    }
    
	public long getRef()
	{
		return ref;
	}
	
	public void setRef(long ref)
	{
		this.ref = ref;
	}
	
	public ByteBuffer getData()
	{
		return data;
	}
	
	public void setData(ByteBuffer data)
	{
		this.data = data;
	}
	
	public ByteBuffer getOldData()
	{
		return oldData;
	}
	
	public void setOldData(ByteBuffer oldData)
	{
		this.oldData = oldData;
	}
	
	public UUID getTransactionPersistentArrayId()
	{
		return transactionPersistentArrayId;
	}
	
	public void setTransactionPersistentArrayId(UUID transactionPersistentArrayId)
	{
		this.transactionPersistentArrayId = transactionPersistentArrayId;
	}
	
	@Override
	public ByteBuffer allocate() {
		return ByteBuffer.allocate(this.getSize());
	}

	@Override
	public int getSize() {
		int size = Long.BYTES + (int)this.recordSize + (int)this.recordSize + Long.BYTES + UUID_SIZE_IN_BYTES;
		return size;
	}

	@Override
	public void load(ByteBuffer buffer) {
		byte[] uuid = new byte[UUID_SIZE_IN_BYTES];
		
		this.recordSize = buffer.getLong();
		this.ref = buffer.getLong();
		buffer.get(uuid, 0, uuid.length);
		try {
			this.transactionPersistentArrayId = UUID.fromString(new String(uuid, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		byte[] oldDataBytes = new byte[(int)recordSize];
		buffer.get(oldDataBytes);
		this.oldData = ByteBuffer.wrap(oldDataBytes);
		
		byte[] newDataBytes = new byte[(int)recordSize];
		buffer.get(newDataBytes);
		this.data = ByteBuffer.wrap(newDataBytes);
	}

	@Override
	public void serialize(ByteBuffer buffer) {
		buffer.putLong(recordSize);
		buffer.putLong(ref);
		buffer.put(transactionPersistentArrayId.toString().getBytes());
		buffer.put(this.oldData);
		buffer.put(data);
	}
}
