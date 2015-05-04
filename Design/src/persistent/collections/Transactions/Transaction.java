package persistent.collections.Transactions;

class public Transaction {
    int operationCount = 0;
    TransactionManager txnManager;
    UUID transactionID;
    PersistentArray<Operation> operations = new PersistentArray<Operation>();
    List<TransactionalPersistentArray> registeredArrays = new List<TransactionalPersistentArray>();
    PersistentHashTable<long, TransactionStateData<ByteArray>> transactionState = new PersistentHashTable<long, TransactionStateData<ByteArray>>);
    
    public Transaction(TransactionManager manager) {
        this.txnManager = manager
        transactionID = new UUID();
    }
    
    void registerArrays(list<TransactionPersistentArray> arrays){
        for(TransactionalPersistentArray xpa : arrays) {
            registeredArrays.add(xpa);
        }
    }
    
    void registerArray(TransactionalPersistentArray array) {
        registeredArrays.add(array);
    }
    
    void start(){
        for(TransactionalPeristentArray xpa : registeredArrays) {
            if(xpa.getTransaction != null){
                throw somethingExceptionallyHeavy();
            }
            p.setTransaction(this);
        }
    }
      
    void commit() {
        txnManager.commitPhaseOne(transactionID, operations.getIterator(), new int operationCount);
        writeToDisk();
        txnManager.commitPhaseTwo(transactionID);
        for(TransactionalPeristentArray xpa : registeredArrays) {
            p.setTransaction(null);
        }
    }
    
    void writeToDisk() {
        for(Operation op : operations){
            try{
                op.do();
            } catch(Exception e){
                rollback(op);
                throw new TransactionFailedException();
            }
        }
    }
    
    void rollback(Operation operation){
        for(Operation op : operations){
            while(!operation.equals(op)){
                op.undo()
            }
        }
    }
    
    void addOperation(Operation op) {
        operationCount++;
        operations.put(op);
        //cache changes
    }
    
    void txnStatePut(long index, ByteArray data) {
        transactionStateData put = new transactionStateData(data, false);
        transactionState.put(index, put);
    }
    
    void txnStateDel(long index) { 
        transactionStateData del = new transactionStateData(null, true);
        transactionState.put(index, del);
    }
    
    E get(long reference) {
        transactionStateData data = transactionState.get(reference)
        if(data.isDeleted){
            throw new deletedException();
        }
        return data.getData();
    } 
}
