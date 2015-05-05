package persistent.collections.Transactions;

public class rollbackInterruptedException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public rollbackInterruptedException()
	{
		super();
	}

	public rollbackInterruptedException(String message)
	{
		super(message);
	}

	public rollbackInterruptedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public rollbackInterruptedException(Throwable cause)
	{
		super(cause);
	}

}
