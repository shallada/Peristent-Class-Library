package persistent.data.proxy.commands;

import java.io.IOException;
import java.nio.ByteBuffer;

import persistent.collections.PersistentArray;

@SuppressWarnings("serial")
public class GetMetadataCommand implements Command
{
	private boolean returnable = true;
	private ByteBuffer metadata;
	
	@Override
	public void execute(PersistentArray persistentArray) throws IOException
	{
		ByteBuffer bb = persistentArray.getMetadata();
		this.metadata = bb;
	}

	@Override
	public boolean isReturnable()
	{
		return returnable;
	}
	
	public ByteBuffer getResult()
	{
		return this.metadata;
	}
}