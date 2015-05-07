package persistent.collections.Transactions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import persistent.collections.TransactionPersistentArray;

public class PutOperation extends Operation {

	public PutOperation(UUID paId, long ref, long recordSize, ByteBuffer data) {
		super(ref, recordSize, paId);
		this.setData(data);
		this.setOldData(ByteBuffer.allocate((int)recordSize));
	}

	@Override
	public void execute(TransactionPersistentArray txnpa) throws IOException {
		this.setOldData(txnpa.transactionGet(this.getRef()));
		txnpa.transactionPut(this.getRef(), this.getData());
	}

	@Override
	public void undo(TransactionPersistentArray txnpa) throws IOException {
		txnpa.transactionPut(this.getRef(), this.getOldData());
	}
}
