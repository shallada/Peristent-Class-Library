package persistent.collections.Transactions;

public class RollbackInterruptedException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RollbackInterruptedException()
	{
		super();
	}

	public RollbackInterruptedException(String message)
	{
		super(message);
	}

	public RollbackInterruptedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public RollbackInterruptedException(Throwable cause)
	{
		super(cause);
	}

}
