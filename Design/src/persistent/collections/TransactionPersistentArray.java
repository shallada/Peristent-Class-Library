package persistent.collections;

import java.io.IOException;
import java.nio.ByteBuffer;

import persistent.collections.Transactions.AllocateOperation;
import persistent.collections.Transactions.DeleteOperation;
import persistent.collections.Transactions.Operation;
import persistent.collections.Transactions.PutOperation;
import persistent.collections.Transactions.Transaction;

public class TransactionPersistentArray implements PersistentArray {

    private Transaction transaction;
    private PersistentArray lowerPersistantArray;
    
    @Override
	public long allocate() throws IOException
	{
		long ref;
		ref = lowerPersistantArray.allocate();
		if(transaction != null)
		{
			Operation allocateOp = new AllocateOperation(this, ref);
			transaction.addOperation(allocateOp);
		}
		return ref;
	}
	
	@Override
	public void delete(long index) throws IOException
	{
		if(transaction != null)
		{
			Operation allocateOp = new DeleteOperation(this, index);
			transaction.addOperation(allocateOp);
			transaction.txnStateDel(index);
		}
		else
		{
			lowerPersistantArray.delete(index);
		}
		
	}
	
	@Override
	public ByteBuffer get(long index) throws IOException
	{
		ByteBuffer data = transaction.get(index);
		if(data == null)
		{
			lowerPersistantArray.get(index);
		}
		return data;
	}
	
	@Override
	public void put(long index, ByteBuffer buffer) throws IOException
	{
		if(transaction != null)
		{
			Operation allocateOp = new PutOperation(this, index, buffer);
			transaction.addOperation(allocateOp);
			transaction.txnStatePut(index, buffer);
		}		
		else
		{
			lowerPersistantArray.put(index, buffer);
		}
	}
	
	@Override
	public void close() throws IOException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public long getRecordCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public ByteBuffer getMetadata() throws IOException
	{
		return lowerPersistantArray.getMetadata();
	}
	
	@Override
	public void persistMetadata() throws IOException
	{
		lowerPersistantArray.persistMetadata();
	}
	
	public long transactionAllocate() throws IOException {
        return lowerPersistantArray.allocate();
    }

    public void transactionPut(long index, ByteBuffer data) throws IOException{
    	lowerPersistantArray.put(index, data);
    }

    public void transactionDelete(long index) throws IOException{
    	lowerPersistantArray.delete(index);
    }
    
    public void setTransaction(Transaction t){
        this.transaction = t;
    }
    
	public Transaction getTransaction()
	{
		return transaction;
	}
    
}
