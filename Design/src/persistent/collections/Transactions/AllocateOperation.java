package persistent.collections.Transactions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import persistent.collections.TransactionPersistentArray;

public class AllocateOperation extends Operation {

	public AllocateOperation(UUID paId, long recordSize, long ref) {
		super(ref, recordSize, paId);
		this.setData(ByteBuffer.allocate((int)recordSize));
		this.setOldData(ByteBuffer.allocate((int)recordSize));
	}

	public void execute(TransactionPersistentArray txnpa) {
		
	}

	@Override
	public void undo(TransactionPersistentArray txnpa) throws IOException {
		txnpa.transactionDelete(this.getRef());
	}
}
