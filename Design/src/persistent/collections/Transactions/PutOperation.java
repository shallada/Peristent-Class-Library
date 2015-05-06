package persistent.collections.Transactions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import persistent.collections.TransactionPersistentArray;

public class PutOperation extends Operation {

	public PutOperation(UUID paId, long ref, ByteBuffer data) {
		this.setRef(ref);
		this.setTransactionPersistentArrayId(paId);
		this.setData(data);
	}

	@Override
	public void execute(TransactionPersistentArray txnpa) throws IOException {
		this.setOldData(txnpa.get(this.getRef()));
		txnpa.put(this.getRef(), this.getData());
	}

	@Override
	public void undo(TransactionPersistentArray txnpa) throws IOException {
		txnpa.put(this.getRef(), this.getOldData());
	}
}
