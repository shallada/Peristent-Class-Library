package persistent.data.proxy.commands;

import java.io.IOException;
import java.nio.ByteBuffer;

import persistent.collections.PersistentArray;

@SuppressWarnings("serial")
public class GetCommand implements Command
{
	private long index;
	private ByteBuffer bbResult;
	private boolean returnable = true;
	
	public GetCommand(long index)
	{
		this.index = index;
	}
	
	@Override
	public void execute(PersistentArray persistentArray) throws IOException
	{
		ByteBuffer results = persistentArray.get(this.index);
		this.bbResult = results;
	}
	
	public ByteBuffer getResult()
	{
		return this.bbResult;
	}

	@Override
	public boolean isReturnable()
	{
		return returnable;
	}
}
