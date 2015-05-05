package persistent.collections.Transactions;

import java.io.IOException;
import java.nio.ByteBuffer;

import persistent.collections.TransactionPersistentArray;

public class PutOperation implements Operation {

	private long nextRef = -1;
	private long ref;
	private ByteBuffer data;
	private ByteBuffer oldData;
	private TransactionPersistentArray pa;

	public PutOperation(TransactionPersistentArray pa, long ref, ByteBuffer data) {
		this.ref = ref;
		this.pa = pa;
		this.data = data;
	}

	@Override
	public void execute() {
		try {
			oldData = pa.get(ref);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			pa.put(ref, data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void undo() {
		try {
			pa.put(ref, oldData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
