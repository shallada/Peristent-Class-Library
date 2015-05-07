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

	public ByteBuffer getData() throws IOException
	{
		if (deleted)
		{
			throw new IOException();
		}
		return data;
	}

	public void setData(ByteBuffer data)
	{
		this.data = data;
		this.deleted = false;
	}

	public boolean isDeleted()
	{
		return deleted;
	}

	public void setDeleted(boolean deleted)
	{
		this.deleted = deleted;
	}
}
