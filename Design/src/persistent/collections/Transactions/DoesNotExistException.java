package persistent.collections.Transactions;

public class DoesNotExistException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DoesNotExistException()
	{
		super();
	}

	public DoesNotExistException(String message)
	{
		super(message);
	}

	public DoesNotExistException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DoesNotExistException(Throwable cause)
	{
		super(cause);
	}

}
