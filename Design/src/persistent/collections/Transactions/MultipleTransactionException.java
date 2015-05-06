package persistent.collections.Transactions;

public class MultipleTransactionException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MultipleTransactionException()
	{
		super();
	}

	public MultipleTransactionException(String message)
	{
		super(message);
	}

	public MultipleTransactionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public MultipleTransactionException(Throwable cause)
	{
		super(cause);
	}
}
