package persistent.collections.Transactions;

import persistent.collections.TransactionPersistentArray;

public class AllocateOperation implements Operation
{

	private long ref;
    private long nextRef = -1;
    private TransactionPersistentArray pa;
    
    public AllocateOperation(TransactionPersistentArray pa, long ref, long nextRe){
        this.ref = ref;
        this.pa = pa;
    }
    
    public void execute() { }
    
    
    public void undo() {
        pa.transactionDelete(ref);
    }
    
    public void setNext(long nextRef){
        this.nextRef = nextRef;
    }
        
    long getNext(){
        return this.nextRef;
    }

}
