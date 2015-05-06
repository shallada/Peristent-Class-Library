package persistent.collections.Transactions;

import java.io.IOException;
import java.nio.ByteBuffer;
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
