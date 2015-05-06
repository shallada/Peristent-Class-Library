package Design.src.persistent.data.proxy.commands;

import java.io.IOException;

import persistent.collections.PersistentArray;

public class AllocateCommand implements Command
{
	private long result;
	private boolean returnable = true;
	
	@Override
	public void execute(PersistentArray persistentArray) throws IOException
	{
		long result = persistentArray.allocate();
		this.result = result;
	}
	
	public long getResult()
	{
		return this.result;
	}

	@Override
	public boolean isReturnable()
	{
		return returnable;
	}
}
