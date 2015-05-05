package persistent.collections.Transactions;

public class TransactionManager
{

	PersistentArray operationsLog;
    PersistentHashmap<UUID, TransactionMetaData> transactionLog = new PersistentHashmap<UUID, Transaction>();
    
    public TransactionManager(PersistenArray operationsLog){
        this.operationsLog = operationsLog;
    }
    
    public Transaction getTransaction(){
        return new Transaction(this);
    }

    public void commitPhaseOne(UUID transactionId, Iterator<Operation> itr, int operationCount){
        long firstOp = writeOperations(Iterator<Operation> itr) // potential issue, storing metadata after operations written to log
        transactionLog.put(transactionID, new TransactionMetaData(firstOp, operationCount));
        transactionLog.get(transactionID).setPhaseOneCommitted(true);    
       }

    public long writeOperations (Iterator<Operation> ops){

        synchronized(operationsLog) {
            long firstRef = operationsLog.allocate();
            operationsLog.put(firstRef, ops.next());
            long prev = firstRef;
            while(ops.hasNext()) {
                long ref = operationsLog.allocate();
                operationsLog.put(ref, ops.next());
                prev.setNext(prev);
                prev = ref;
            }
        }
        return firstRef;
    }

    public void commitPhaseTwo(UUID transactionId){
        transactionLog.get(transactionId).setPhaseTwoCommitted(true);
        deleteTransaction(transactionId);
        
    }
    
    private void deleteTransaction(UUID transactionId){
        long currentRef = transactionLog.get(transactionID).getFirstRef();
        while(transactionLog.get(transactionID).getFirstRef() != -1){
            transactionLog.get(transactionID).setFirstRef(operationsLog.get(currentRef).getNext());
            operationsLog.delete(currentRef);
            currentRef = transactionLog.get(transactionID).getFirstRef();
        }
    }
    
    
    // Periodically compact operations log by removing transactions that are phase two completed.

}
