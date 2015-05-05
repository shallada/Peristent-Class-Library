package Design.src.persistent.data.proxy.commands;

import java.io.IOException;

import persistent.collections.PersistentArray;

public class GetRecordCountCommand implements Command
{
	private boolean returnable = true;
	private long recordCount;

	@Override
	public void execute(PersistentArray persistentArray) throws IOException
	{
		long recordCount = persistentArray.getRecordCount();
		this.recordCount = recordCount;
	}

	@Override
	public boolean isReturnable()
	{
		return returnable;
	}
	
	public long getResult()
	{
		return this.recordCount;
	}
}