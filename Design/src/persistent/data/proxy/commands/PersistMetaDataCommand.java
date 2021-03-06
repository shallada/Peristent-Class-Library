package persistent.data.proxy.commands;

import java.io.IOException;

import persistent.collections.PersistentArray;

@SuppressWarnings("serial")
public class PersistMetaDataCommand implements Command
{
	private boolean returnable = false;
	
	@Override
	public void execute(PersistentArray persistentArray) throws IOException
	{
		persistentArray.persistMetadata();
	}

	@Override
	public boolean isReturnable()
	{
		return this.returnable;
	}

}
