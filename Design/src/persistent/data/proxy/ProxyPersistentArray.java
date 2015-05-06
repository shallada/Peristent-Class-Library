package Design.src.persistent.data.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;

import persistent.array.PersistentArrayInterface;
import persistent.collections.PersistentArray;

public class ProxyPersistentArray implements PersistentArray
{
	private ProxyNetwork proxyNetwork;
	
	public ProxyPersistentArray(PersistentArray persistentArray, String ipAddress, int port)
	{
		proxyNetwork = new ProxyNetwork(persistentArray, ipAddress, port);
	}
	
	@Override
	public long allocate()
	{
		// call allocate on proxyNetwork
		// return Long recieved
	}

	@Override
	public ByteBuffer get(long index)
	{
		// call get on proxyNetowrk passing index
		// save the returned bytebuffer as bb
		// return bb
	}

	@Override
	public void put(long index, ByteBuffer bb)
	{
		// call put on proxyNetwork passing index and bb
	}

	@Override
	public void delete(long index)
	{
		// call delete on proxyNetwork passing index
	}

	@Override
	public ByteBuffer getMetadata()
	{
		// call getMetaData on proxyNetwork
		// Recieve ByteBufffer and save as bb
		// return bb
	}

	@Override
	public long getRecordCount()
	{
		// call getRecordCount on proxyNetwork
	}

	@Override
	public void close() throws IOException
	{
		// call close on the proxyNetwork
	}

	@Override
	public void persistMetadata() throws IOException
	{
		// call persistMetaData on the proxyNetwork		
	}

}
