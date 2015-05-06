package persistent.collections.Transactions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import persistent.Persistable;
import persistent.collections.TransactionPersistentArray;

public abstract class Operation implements Persistable
{
	private long nextRef = -1;
	private long ref;
	private ByteBuffer data;
	private ByteBuffer oldData;
	private UUID transactionPersistentArrayId;
	
    public abstract void execute(TransactionPersistentArray txnpa) throws IOException;
    public abstract void undo(TransactionPersistentArray txnpa) throws IOException, RollbackInterruptedException;
    
	public long getNextRef()
	{
		return nextRef;
	}
	
	public void setNextRef(long nextRef)
	{
		this.nextRef = nextRef;
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
}
