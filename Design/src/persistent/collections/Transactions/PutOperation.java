package persistent.collections.Transactions;

import persistent.collections.TransactionPersistentArray;

public class PutOperation implements Operation {

	private long nextRef = -1;
	private long ref;
	private ByteArray data;
	private ByteArray oldData;
	private TransactionPersistentArray pa;

	public PutOperation(TransactionPersistentArray pa, long ref, ByteArray data) {
		this.ref = ref;
		this.pa = pa;
		this.data = data;
	}

	@Override
	public void execute() {
		oldData = pa.get(ref);
		pa.put(ref, data);
	}

	@Override
	public void undo() {
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
