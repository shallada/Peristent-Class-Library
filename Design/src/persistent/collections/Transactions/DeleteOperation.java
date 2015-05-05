package persistent.collections.Transactions;

import java.io.IOException;
import java.nio.ByteBuffer;

import persistent.collections.TransactionPersistentArray;

public class DeleteOperation implements Operation {

	private long nextRef = -1;
	private long ref;
	private TransactionPersistentArray pa;
	private ByteBuffer oldData;

	public DeleteOperation(TransactionPersistentArray pa, long ref) {
		this.ref = ref;
		this.pa = pa;
	}

	@Override
	public void execute() throws IOException {
		oldData = pa.get(ref);
		pa.delete(ref);
	}

	@Override
	public void undo() throws RollbackInterruptedException, IOException {
		long testRef = pa.allocate();
		if (testRef != ref) {
			throw new RollbackInterruptedException(
					"Someone external allocated during your transaction.");
		}
		pa.put(ref, oldData);
	}

	@Override
	public void setNext(long nextRef) {
		this.nextRef = nextRef;
	}

	@Override
	public long getNext() {
		return this.nextRef;
	}

}
