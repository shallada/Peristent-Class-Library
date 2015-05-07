package persistent.data.proxy;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;

import persistent.collections.PersistentArray;

public class ProxyPersistentArray implements PersistentArray
{
	private ProxyNetwork proxyNetwork;
	
	public ProxyPersistentArray(String ipAddress, int port)
	{
		this.proxyNetwork = new ProxyNetwork(ipAddress, port);
	}
	
	@Override
	public long allocate() throws IOException
	{
		long allocatedSpace = proxyNetwork.allocate();
		return allocatedSpace;
	}

	@Override
	public ByteBuffer get(long index) throws IOException
	{
		ByteBuffer bb = this.proxyNetwork.get(index);
		return bb;
	}

	@Override
	public void put(long index, ByteBuffer bb) throws IOException
	{
		this.proxyNetwork.put(index, bb);
	}

	@Override
	public void delete(long index) throws IOException
	{
		this.proxyNetwork.delete(index);
	}

	@Override
	public ByteBuffer getMetadata() throws IOException
	{
		ByteBuffer bb = this.proxyNetwork.getMetadata();
		return bb;
	}

	@Override
	public long getRecordCount() throws UncheckedIOException
	{
		long recordCount = this.proxyNetwork.getRecordCount();
		return recordCount;
	}

	@Override
	public void close() throws IOException
	{
		this.proxyNetwork.close();
	}

	@Override
	public void persistMetadata() throws IOException
	{
		this.proxyNetwork.persistMetadata();	
	}
}
