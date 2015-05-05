package persistent.collections.Transactions;

import java.io.IOException;
import java.nio.ByteBuffer;

import persistent.collections.TransactionPersistentArray;

public class DeleteOperation implements Operation
{

    private long nextRef = -1;
    private long ref;
    private TransactionPersistentArray pa;
    private ByteBuffer oldData;
    
    public DeleteOperation(TransactionPersistentArray pa, long ref){
        this.ref = ref;
        this.pa = pa;
    }

    @Override
    public void execute(){
        try {
			oldData = pa.get(ref);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			pa.delete(ref);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void undo() throws rollbackInterruptedException{ 
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
