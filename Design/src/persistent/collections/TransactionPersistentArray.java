package persistent.collections;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import persistent.collections.Transactions.AllocateOperation;
import persistent.collections.Transactions.DeleteOperation;
import persistent.collections.Transactions.Operation;
import persistent.collections.Transactions.PutOperation;
import persistent.collections.Transactions.Transaction;

public class TransactionPersistentArray implements PersistentArray {

	private Transaction transaction;
	private PersistentArray lowerPersistentArray;
	private long recordSize;
	private UUID id;

	protected static int ourMetadataSize = Long.BYTES;

	public TransactionPersistentArray(PersistentArray lowerPersistentArray,
			long recordSize) {
		this.lowerPersistentArray = lowerPersistentArray;
		this.recordSize = recordSize;
		id = UUID.randomUUID();
	}

	@Override
	public long allocate() throws IOException {
		long ref;
		ref = lowerPersistentArray.allocate();
		if (transaction != null) {
			Operation allocateOp = new AllocateOperation(id, recordSize, ref);
			transaction.addOperation(allocateOp);
		}
		return ref;
	}

	@Override
	public void delete(long index) throws IOException {
		if (transaction != null) {
			Operation allocateOp = new DeleteOperation(id, index, recordSize);
			transaction.addOperation(allocateOp);
			transaction.txnStateDel(index);
		} else {
			lowerPersistentArray.delete(index);
		}

	}

	@Override
	public ByteBuffer get(long index) throws IOException {
		ByteBuffer data = transaction.get(index);
		if (data == null) {
			lowerPersistentArray.get(index);
		}
		return data;
	}

	@Override
	public void put(long index, ByteBuffer buffer) throws IOException {
		if (transaction != null) {
			Operation allocateOp = new PutOperation(id, recordSize, index, buffer);
			transaction.addOperation(allocateOp);
			transaction.txnStatePut(index, buffer);
		} else {
			lowerPersistentArray.put(index, buffer);
		}
	}

	@Override
	public void close() throws IOException {
		lowerPersistentArray.close();

	}

	@Override
	public long getRecordCount() {
		return lowerPersistentArray.getRecordCount();
	}

	@Override
	public ByteBuffer getMetadata() throws IOException {
		ByteBuffer bb = lowerPersistentArray.getMetadata();
		this.recordSize = bb.getLong();
		return bb.slice();
	}

	@Override
	public void persistMetadata() throws IOException {
		ByteBuffer bb = lowerPersistentArray.getMetadata();
		bb.putLong(recordSize);
		lowerPersistentArray.persistMetadata();
	}

	public long transactionAllocate() throws IOException {
		return lowerPersistentArray.allocate();
	}

	public void transactionPut(long index, ByteBuffer data) throws IOException {
		lowerPersistentArray.put(index, data);
	}

	public void transactionDelete(long index) throws IOException {
		lowerPersistentArray.delete(index);
	}

	public ByteBuffer transactionGet(long index) throws IOException {

		return lowerPersistentArray.get(index);
	}

	public void setTransaction(Transaction t) {
		this.transaction = t;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public long getRecordSize() {
		return this.recordSize;
	}

	public UUID getId() {
		return id;
	}
}
