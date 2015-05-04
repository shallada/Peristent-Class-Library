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

    @Override
    public void execute(){
        oldData = pa.get(ref);
        pa.del(ref);
    }

    @Override
    public void undo(){ 
        long testRef = pa.allocate();
        if (testRef != ref){
            throw new rollbackInterruptedException();
        }
        pa.put(ref, oldData);
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
