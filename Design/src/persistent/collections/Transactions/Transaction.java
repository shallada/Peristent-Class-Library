package persistent.collections.Transactions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

import persistent.collections.BasePersistentArray;
import persistent.collections.PersistentArray;
import persistent.collections.TransactionPersistentArray;
import persistent.collections.dictionary.PersistentDictionary;
import persistent.collections.dictionary.PersistentFactory;

public class Transaction {
	int operationCount = 0;
	TransactionManager transactionManager;
	UUID transactionID;
	PersistentArray operations;
	ArrayList<TransactionPersistentArray> registeredArrays = new ArrayList<TransactionPersistentArray>();

	PersistentDictionary<Long, TransactionStateData> transactionState = 
			new PersistentDictionary<Long, TransactionStateData>(
					new BasePersistentArray(), new PersistentFactory<Long>(),
					new PersistentFactory<TransactionStateData>());


	public Transaction(TransactionManager manager) {
		this.transactionManager = manager;
		// transactionID = new UUID();
	}

	public void registerArrays(ArrayList<TransactionPersistentArray> arrays) {
		for (TransactionPersistentArray xpa : arrays) {
			registeredArrays.add(xpa);
		}
	}

	public void registerArray(TransactionPersistentArray array) {
		registeredArrays.add(array);
	}

	public void start() {
		for (TransactionPersistentArray xpa : registeredArrays) {
			if (xpa.getTransaction() != null) {
				// throw exception about xpa already having a transaction
			}
			xpa.setTransaction(this);
		}
	}

	public void commit() {
		transactionManager.commitPhaseOne(transactionID,
				operations.getIterator(), operationCount);
		writeToDisk();
		transactionManager.commitPhaseTwo(transactionID);
		for (TransactionPersistentArray xpa : registeredArrays) {
			xpa.setTransaction(null);
		}
	}

	public void writeToDisk() {
		for (Operation op : operations) {
			try {
				op.execute();
			} catch (Exception e) {
				rollback(op);
				throw new TransactionFailedException();
			}
		}
	}

	public void rollback(Operation operation) {
		for (Operation op : operations) {
			while (!operation.equals(op)) {
				op.undo();
			}
		}
	}

	public void addOperation(Operation op) {
		operationCount++;
		operations.put(op);
	}

	public void txnStatePut(long index, ByteBuffer data) {
		TransactionStateData put = new TransactionStateData(data, false);
		transactionState.put(index, put);
	}

	public void txnStateDel(long index) {
		TransactionStateData del = new TransactionStateData(null, true);
		transactionState.put(index, del);
	}

	public ByteBuffer get(long reference) {
        TransactionStateData data = transactionState.get(reference)
        if(data.isDeleted()){
            throw new IOException();
        }
        return data.getData();
    }
	
	private class LongFactory implements PersistentFactory<Long> {

		@Override
		public Long fromBuffer(ByteBuffer data) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void toBuffer(ByteBuffer buffer, Long obj) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int sizeInBytes() {
			// TODO Auto-generated method stub
			return 0;
		}
	}
}
