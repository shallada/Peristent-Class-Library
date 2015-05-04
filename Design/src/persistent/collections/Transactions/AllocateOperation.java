package persistent.collections.Transactions;

import persistent.collections.TransactionPersistentArray;

public class AllocateOperation implements Operation
{

	private long ref;
    private long nextRef = -1;
    private 	TransactionPersistentArray pa;
    
    public AllocateOperation(TransactionPersistentArray pa, long ref, long nextRe){
        this.ref = ref;
        this.pa = pa;
    }
    
    public void execute() { }
    
    @Override
    public void undo() {
        pa.transactionDelete(ref);
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
