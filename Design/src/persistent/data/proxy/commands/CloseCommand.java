package Design.src.persistent.data.proxy.commands;

import java.io.IOException;

import persistent.collections.PersistentArray;

public class CloseCommand implements Command
{
	private boolean returnable = false;

	@Override
	public void execute(PersistentArray persistentArray) throws IOException
	{
		persistentArray.close();
	}

	@Override
	public boolean isReturnable()
	{
		return returnable;
	}
}