package persistent.collections.Transactions;

import java.io.IOException;
import java.util.UUID;

import persistent.collections.TransactionPersistentArray;

public class AllocateOperation extends Operation {

	public AllocateOperation(UUID paId, long ref) {
		this.setRef(ref);
		this.setTransactionPersistentArrayId(paId);
	}

	public void execute(TransactionPersistentArray txnpa) {
	}

	@Override
	public void undo(TransactionPersistentArray txnpa) throws IOException {
		txnpa.transactionDelete(this.getRef());
	}
}
