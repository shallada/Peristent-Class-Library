package src.persistent.collections.Transactions;

import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

import persistent.collections.PersistentArray;
import persistent.collections.Transactions.Operation;
import persistent.collections.Transactions.Transaction;
import persistent.collections.Transactions.TransactionMetaData;
import persistent.collections.dictionary.PersistentDictionary;

public class TransactionManager
{
	PersistentArray operationsLog;
	PersistentDictionary<UUID, TransactionMetaData> transactionLog;// = new PersistentDictionary<UUID, TransactionMetaData>(operationsLog, null, null);

	public TransactionManager(PersistentArray operationsLog)
	{
		this.operationsLog = operationsLog;
	}

	public Transaction getTransaction() throws IOException
	{
		return new Transaction(this);
	}

	public void commitPhaseOne(UUID transactionId, Iterator<Operation> itr, int operationCount)
	{
		long firstOp = writeOperations(itr); // potential
																	// issue,
																	// storing
																	// metadata
																	// after
																	// operations
																	// written
																	// to log
		transactionLog.put(transactionId, new TransactionMetaData(firstOp, operationCount));
		transactionLog.get(transactionId).setPhaseOneCommitted(true);
	}

	public long writeOperations(Iterator<Operation> ops)
	{
		long firstRef = 0;
		synchronized (operationsLog)
		{
			firstRef = operationsLog.allocate();
			operationsLog.put(firstRef, ops.next());
			long prev = firstRef;
			while (ops.hasNext())
			{
				long ref = operationsLog.allocate();
				operationsLog.put(ref, ops.next());
				operationsLog.get(prev).setNext(ref);
				prev = ref;
			}
		}
		return firstRef;
	}

	public void commitPhaseTwo(UUID transactionId)
	{
		transactionLog.get(transactionId).setPhaseTwoCommitted(true);
		deleteTransaction(transactionId);

	}

	private void deleteTransaction(UUID transactionId)
	{
		long currentRef = transactionLog.get(transactionId).getFirstRef();
		while (transactionLog.get(transactionId).getFirstRef() != -1)
		{
			transactionLog.get(transactionId).setFirstRef(operationsLog.get(currentRef).getNext());
			operationsLog.delete(currentRef);
			currentRef = transactionLog.get(transactionId).getFirstRef();
		}
	}

	// Periodically compact operations log by removing transactions that are
	// phase two completed.

}
