package persistent.collections.Transactions;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

import persistent.collections.PersistentArray;
import persistent.collections.TransactionPersistentArray;

public class Transaction
{
	int operationCount = 0;
	TransactionManager transactionManager;
	UUID transactionID;
	PersistentArray operations;// = new PersistentArray();
	ArrayList<TransactionPersistentArray> registeredArrays = new ArrayList<TransactionPersistentArray>();

	// PersistentHashTable<Long, TransactionStateData<ByteBuffer>>
	// transactionState;// = new PersistentHashTable<Long,
	// TransactionStateData<ByteArray>>);

	public Transaction(TransactionManager manager)
	{
		this.transactionManager = manager;
		// transactionID = new UUID();
	}

	void registerArrays(ArrayList<TransactionPersistentArray> arrays)
	{
		for (TransactionPersistentArray xpa : arrays)
		{
			registeredArrays.add(xpa);
		}
	}

	void registerArray(TransactionPersistentArray array)
	{
		registeredArrays.add(array);
	}

	void start()
	{
		for (TransactionPersistentArray xpa : registeredArrays)
		{
			if (xpa.getTransaction() != null)
			{
				// throw exception about xpa already having a transaction
			}
			xpa.setTransaction(this);
		}
	}

	void commit() {
        txnManager.commitPhaseOne(transactionID, operations.getIterator(), new int operationCount);
        writeToDisk();
        txnManager.commitPhaseTwo(transactionID);
        for(TransactionalPeristentArray xpa : registeredArrays) {
            p.setTransaction(null);
        }
    }

	void writeToDisk() {
        for(Operation op : operations){
            try{
                op.do();
            } catch(Exception e){
                rollback(op);
                throw new TransactionFailedException();
            }
        }
    }

	void rollback(Operation operation){
        for(Operation op : operations){
            while(!operation.equals(op)){
                op.undo()
            }
        }
    }

	void addOperation(Operation op)
	{
		operationCount++;
		operations.put(op);
		// cache changes
	}

	void txnStatePut(long index, ByteArray data)
	{
		transactionStateData put = new transactionStateData(data, false);
		transactionState.put(index, put);
	}

	void txnStateDel(long index)
	{
		transactionStateData del = new transactionStateData(null, true);
		transactionState.put(index, del);
	}

	E get(long reference) {
        transactionStateData data = transactionState.get(reference)
        if(data.isDeleted){
            throw new deletedException();
        }
        return data.getData();
    }}
