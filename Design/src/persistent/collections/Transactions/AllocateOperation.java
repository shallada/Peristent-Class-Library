package persistent.collections.Transactions;

import java.io.IOException;

import persistent.collections.TransactionPersistentArray;

public class AllocateOperation implements Operation
{

	private long ref;
    private long nextRef = -1;
    private TransactionPersistentArray pa;
    
    public AllocateOperation(TransactionPersistentArray pa, long ref){
        this.ref = ref;
        this.pa = pa;
    }
    
    public void execute() { }
    
    @Override
    public void undo() {
        try {
			pa.transactionDelete(ref);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    @Override
    public void setNext(long nextRef){
        this.nextRef = nextRef;
    }
    @Override
    public long getNext(){
        return this.nextRef;
    }

}
