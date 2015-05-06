package Design.src.persistent.data.proxy.commands;

import java.io.IOException;
import java.nio.ByteBuffer;

import persistent.collections.PersistentArray;

public class PutCommand implements Command
{
	private long index;
	private ByteBuffer bb;
	private boolean returnable = false;
	
	public PutCommand(long index, ByteBuffer bb)
	{
		this.index = index;
		this.bb = bb;
	}
	
	@Override
	public void execute(PersistentArray persistentArray) throws IOException
	{
		persistentArray.put(index, bb);
	}

	@Override
	public boolean isReturnable()
	{
		return returnable;
	}
}
