package persistent.collections;

import java.io.IOException;
import java.nio.ByteBuffer;

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
			//add allocate command
			//add to cache
		}
		return ref;
	}
	
	@Override
	public void delete(long index) throws IOException
	{
		if(transaction != null)
		{
			//add delete command
			//add or update cache
			//transaction.txnStateDelete(index);
		}
		else
		{
			lowerPersistantArray.delete(index);
		}
		
	}
	
	@Override
	public ByteBuffer get(long index) throws IOException
	{
		ByteBuffer data = null;// = transaction.get(index);
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
			//add put command
			//add or update cache
			//transaction.txnStatePut(ref, data);
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
    
}
