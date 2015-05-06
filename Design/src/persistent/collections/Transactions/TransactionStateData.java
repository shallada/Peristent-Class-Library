package persistent.collections.Transactions;

import java.io.IOException;
import java.nio.ByteBuffer;

public class TransactionStateData
{
	private boolean deleted;
	private ByteBuffer data;

	public TransactionStateData(ByteBuffer data, boolean deleted)
	{
		setData(data);
		setDeleted(deleted);
	}

	ByteBuffer getData() throws IOException
	{
		if (deleted)
		{
			throw new IOException();
		}
		return data;
	}

	void setData(ByteBuffer data)
	{
		this.data = data;
	}

	boolean isDeleted()
	{
		return deleted;
	}

	void setDeleted(boolean deleted)
	{
		this.deleted = deleted;
	}
}
