package persistent.collections.Transactions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import persistent.collections.TransactionPersistentArray;

public class DeleteOperation extends Operation {

	public DeleteOperation(UUID paId, long ref) {
		this.setRef(ref);
		this.setTransactionPersistentArrayId(paId);
	}

	@Override
	public void execute(TransactionPersistentArray txnpa) throws IOException {
		setOldData(txnpa.get(getRef()));
		txnpa.delete(getRef());
	}

	@Override
	public void undo(TransactionPersistentArray txnpa) throws RollbackInterruptedException, IOException {
		long testRef = txnpa.allocate();
		if (testRef != getRef()) {
			throw new RollbackInterruptedException(
					"Someone external allocated during your transaction.");
		}
		txnpa.put(getRef(), getOldData());
	}

	@Override
	public ByteBuffer allocate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void load(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serialize(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		
	}
}
