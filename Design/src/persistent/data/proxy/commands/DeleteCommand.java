package persistent.data.proxy.commands;

import java.io.IOException;

import persistent.collections.PersistentArray;

@SuppressWarnings("serial")
public class DeleteCommand implements Command
{
	private long index;
	private boolean returnable = false;
	
	public DeleteCommand(long index)
	{
		this.index = index;
	}
	
	@Override
	public void execute(PersistentArray persistentArray) throws IOException
	{
		persistentArray.delete(index);
	}

	@Override
	public boolean isReturnable()
	{
		return returnable;
	}
}
