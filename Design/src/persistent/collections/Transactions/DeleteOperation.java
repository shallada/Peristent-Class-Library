package persistent.collections.Transactions;

import persistent.collections.TransactionPersistentArray;

public class DeleteOperation implements Operation
{

    private long nextRef = -1;
    private long ref;
    private TransactionPersistentArray pa;
    private ByteArray oldData;
    
    public DeleteOperation(TransactionPersistentArray pa, long ref){
        this.ref = ref;
        this.pa = pa;
    }

    public void execute(){
        oldData = pa.get(ref);
        pa.del(ref);
    }

    public void undo(){ 
        long testRef = pa.allocate();
        if (testRef != ref){
            throw new rollbackInterruptedException();
        }
        pa.put(ref, oldData);
    }
    
    public void setNext(long nextRef){
        this.nextRef = nextRef;
    }
    
    public long getNext(){
        return this.nextRef;
    }

}
