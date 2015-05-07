package persistent.collections.Transactions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import persistent.collections.TransactionPersistentArray;

public class DeleteOperation extends Operation {

	public DeleteOperation(UUID paId, long ref, long recordSize) {
		super(ref, recordSize, paId);
		this.setData(ByteBuffer.allocate((int)recordSize));
		this.setOldData(ByteBuffer.allocate((int)recordSize));
	}

	@Override
	public void execute(TransactionPersistentArray txnpa) throws IOException {
		setOldData(txnpa.transactionGet(getRef()));
		txnpa.transactionDelete(getRef());
	}

	@Override
	public void undo(TransactionPersistentArray txnpa) throws RollbackInterruptedException, IOException {
		long testRef = txnpa.allocate();
		if (testRef != getRef()) {
			throw new RollbackInterruptedException(
					"Someone external allocated during your transaction.");
		}
		txnpa.transactionPut(getRef(), getOldData());
	}
}
