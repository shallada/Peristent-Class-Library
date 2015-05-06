package persistent.collections.Transactions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

import persistent.collections.BasePersistentArray;
import persistent.collections.TransactionPersistentArray;
import persistent.collections.LinkedList.LinkedList;
import persistent.collections.Transactions.MultipleTransactionException;
import persistent.collections.Transactions.Operation;
import persistent.collections.Transactions.TransactionManager;
import persistent.collections.Transactions.TransactionStateData;
import persistent.collections.dictionary.PersistentDictionary;
import persistent.collections.dictionary.PersistentFactory;

public class Transaction {
	private int operationCount = 0;
	private String transactionStatePath = "/transactions/";
	private TransactionManager transactionManager;
	private UUID transactionID;
	private LinkedList<Operation> operations;
	private ArrayList<TransactionPersistentArray> registeredArrays = new ArrayList<TransactionPersistentArray>();

	PersistentDictionary<Long, TransactionStateData> transactionState;

	public Transaction(TransactionManager manager) throws IOException {
		this.transactionManager = manager;
		TransactionStateDataFactory tsdfactory = new TransactionStateDataFactory();
		BasePersistentArray.create(transactionStatePath, 0, tsdfactory.sizeInBytes());
		BasePersistentArray bpa = BasePersistentArray.open(transactionStatePath);
		transactionState = new PersistentDictionary<Long, TransactionStateData>(
				bpa, new LongFactory(), tsdfactory);
		transactionID = UUID.randomUUID();
	}

	public void registerArrays(ArrayList<TransactionPersistentArray> arrays) {
		for (TransactionPersistentArray xpa : arrays) {
			registeredArrays.add(xpa);
		}
	}

	public void registerArray(TransactionPersistentArray array) {
		registeredArrays.add(array);
	}
	
	public void start() throws MultipleTransactionException {
		for (TransactionPersistentArray xpa : registeredArrays) {
			if (xpa.getTransaction() != null) {
				throw new MultipleTransactionException("A TransactionalPersistentArray cannot belong to more than one transaction.");
			}
			xpa.setTransaction(this);
		}
	}

	public void commit() {
		transactionManager.commitPhaseOne(transactionID,
				operations.iterator(), operationCount); //// not sure how to do this?
		writeToDisk();
		transactionManager.commitPhaseTwo(transactionID);
		for (TransactionPersistentArray xpa : registeredArrays) {
			xpa.setTransaction(null);
		}
	}

	public void writeToDisk(){
		for (Operation op : operations) {
			try {
				op.execute(findTransactionPersistentArrayById(op.getTransactionPersistentArrayId()));
			} catch (IOException e) {
				rollback(op);
				throw new IOException();
			}
		}
	}

	public void rollback(Operation operation) {
		for (Operation op : operations) {
			while (!operation.equals(op)) {
				op.undo(findTransactionPersistentArrayById(op.getTransactionPersistentArrayId()));
			}
		}
	}
	
	public void addOperation(Operation op) throws IOException {
		operationCount++;
		long index = operations.allocate();
		OperationFactory factory = new OperationFactory();
		ByteBuffer bb = ByteBuffer.allocate(factory.sizeInBytes());
		factory.toBuffer(bb, op);
		operations.put(index, bb);
	}

	public void txnStatePut(long index, ByteBuffer data) {
		TransactionStateData put = new TransactionStateData(data, false);
		transactionState.put(index, put);
	}

	public void txnStateDel(long index) {
		TransactionStateData del = new TransactionStateData(null, true);
		transactionState.put(index, del);
	}

	public ByteBuffer get(long reference) throws IOException {
        TransactionStateData data = transactionState.get(reference);
        if(data.isDeleted()){
            throw new IOException();
        }
        return data.getData();
    }
	
	private TransactionPersistentArray findTransactionPersistentArrayById(UUID id)
	{
		for(int i = 0; i < registeredArrays.size(); i++)
		{
			if(registeredArrays.get(i).getId() == id)
			{
				return registeredArrays.get(i);
			}
		}
		return null;
	}
	
	//implement these. -------------------------------------------
	
	private class OperationFactory implements PersistentFactory<Operation>{

		@Override
		public Operation fromBuffer(ByteBuffer data) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void toBuffer(ByteBuffer buffer, Operation obj) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int sizeInBytes() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}

	private class LongFactory implements PersistentFactory<Long> {

		@Override
		public Long fromBuffer(ByteBuffer data) {
			return data.getLong();
		}

		@Override
		public void toBuffer(ByteBuffer buffer, Long obj) {
			buffer.putLong(obj);
		}

		@Override
		public int sizeInBytes() {
			return Long.BYTES;
		}
	}

	private class TransactionStateDataFactory implements
			PersistentFactory<TransactionStateData> {

		@Override
		public TransactionStateData fromBuffer(ByteBuffer data) {
			boolean deleted = (data.get() == 1);
			return new TransactionStateData(data.slice(), deleted);
		}

		@Override
		public void toBuffer(ByteBuffer buffer, TransactionStateData obj) {
			buffer.put((byte)(obj.isDeleted() ? 1 : 0));
			try
			{
				buffer.put(obj.getData());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}

		@Override
		public int sizeInBytes() {
			return 1;
		}

	}
}
