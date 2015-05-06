package persistent.collections.Transactions;

public class TransactionMetaData
{
	private long firstRef;
	private int operationCount;
	private boolean phaseOneCommitted = false;
	private boolean phaseTwoCommitted = false;

	public TransactionMetaData(long firstRef, int opCount)
	{
		this.firstRef = firstRef;
		this.operationCount = opCount;
	}

	public boolean getPhaseOneCommitted()
	{
		return this.phaseOneCommitted;
	}

	public void setPhaseOneCommitted(boolean committed)
	{
		this.phaseOneCommitted = committed;
	}

	public boolean getPhaseTwoCommitted()
	{
		return this.phaseTwoCommitted;
	}

	public void setPhaseTwoCommitted(boolean committed)
	{
		this.phaseTwoCommitted = committed;
	}

	public int getOperationCount()
	{
		return this.operationCount;
	}

	public long getFirstRef()
	{
		return firstRef;
	}

	public void setFirstRef(long firstRef)
	{
		this.firstRef = firstRef;
	}

	public void setOperationCount(int operationCount)
	{
		this.operationCount = operationCount;
	}
}
