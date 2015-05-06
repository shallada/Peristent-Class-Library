package persistent.collections.Transactions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.UUID;

import persistent.collections.BasePersistentArray;
import persistent.collections.LinkedList.LinkedList;
import persistent.collections.dictionary.PersistentDictionary;
import persistent.collections.dictionary.PersistentFactory;

public class TransactionManager {
	private LinkedList<Operation> operationsLog;
	private PersistentDictionary<UUID, TransactionMetaData> transactionLog;
	private String transactionMetaDataPath = "/transactions/";
	private int metaDataSize = 0;

	public TransactionManager(LinkedList<Operation> operationsLog) throws IOException {
		TransactionMetaDataFactory tmdfactory = new TransactionMetaDataFactory();
		BasePersistentArray.create(transactionMetaDataPath, metaDataSize, tmdfactory.sizeInBytes());
		BasePersistentArray bpa = BasePersistentArray.open(transactionMetaDataPath);
		transactionLog = new PersistentDictionary<UUID, TransactionMetaData>(
				bpa, new UUIDFactory(), tmdfactory);
		this.operationsLog = operationsLog;
	}

	public Transaction getTransaction() throws IOException {
		return new Transaction(this);
	}

	public void commitPhaseOne(UUID transactionId, Iterator<Operation> itr,
			int operationCount) throws IOException {
		long firstOp = writeOperations(itr);
		transactionLog.put(transactionId, new TransactionMetaData(firstOp,
				operationCount));
		transactionLog.get(transactionId).setPhaseOneCommitted(true);
	}

	private long writeOperations(Iterator<Operation> ops) throws IOException {
		long firstRef = 0;
		synchronized (operationsLog) {
			while (ops.hasNext()) {
				Operation next = ops.next();
				operationsLog.addToEnd(next);
			}
		}
		return operationsLog.getListSize() - 1;
	}

	public void commitPhaseTwo(UUID transactionId) throws IOException {
		transactionLog.get(transactionId).setPhaseTwoCommitted(true);
		deleteTransaction(transactionId);

	}

	private void deleteTransaction(UUID transactionId) throws IOException {
		long currentRef = transactionLog.get(transactionId).getFirstRef();
		for (; currentRef < currentRef
				+ transactionLog.get(transactionId).getOperationCount(); currentRef++) {
			operationsLog.remove(currentRef);
		}
	}
	
	private class UUIDFactory implements PersistentFactory<UUID> {
		private static final int UUID_SIZE_IN_BYTES = 36;
		@Override
		public UUID fromBuffer(ByteBuffer data) {
			byte[] uuid = new byte[UUID_SIZE_IN_BYTES];
			data.get(uuid, 0, uuid.length);
			UUID id = null;
			try {
				id = UUID.fromString(new String(uuid, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return id;
		}

		@Override
		public void toBuffer(ByteBuffer buffer, UUID obj) {
			buffer.put(obj.toString().getBytes());
		}

		@Override
		public int sizeInBytes() {
			return UUID_SIZE_IN_BYTES;
		}
	}

	private class TransactionMetaDataFactory implements
			PersistentFactory<TransactionMetaData> {
		int booleanSize = 1;
		@Override
		public TransactionMetaData fromBuffer(ByteBuffer data) {
			long firstRef = data.getLong();
			int operationCount = data.getInt();
			boolean phaseOneCommitted = (data.get() == 1);
			boolean phaseTwoCommitted = (data.get() == 1);
			TransactionMetaData tmd = new TransactionMetaData(firstRef, operationCount);
			tmd.setPhaseOneCommitted(phaseOneCommitted);
			tmd.setPhaseTwoCommitted(phaseTwoCommitted);
			return tmd;
		}

		@Override
		public void toBuffer(ByteBuffer buffer, TransactionMetaData obj) {
			buffer.putLong(obj.getFirstRef());
			buffer.putInt(obj.getOperationCount());
			buffer.put((byte)(obj.getPhaseOneCommitted() ? 1 : 0));
			buffer.put((byte)(obj.getPhaseTwoCommitted() ? 1 : 0));
		}

		@Override
		public int sizeInBytes() {
			return Long.BYTES + Integer.BYTES + booleanSize + booleanSize;
		}
	}
}
