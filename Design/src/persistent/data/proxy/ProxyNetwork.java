package Design.src.persistent.data.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.stream.Stream;

import persistent.collections.PersistentArray;

public class ProxyNetwork implements PersistentArray
{
	private String ipAddress;
	private int port;
	private PersistentArray pa;
	
	public ProxyNetwork(PersistentArray pa, String ipAddress, int port)
	{
		this.pa = pa;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	@Override
	public long allocate()
	{
		// connect to server
		// create new allocateCommand with this pa 
		// send allocateCommand over stream
		// recieve back a new allocateCommand from stream
		// close stream
		// return the result from allocateCommand.getResult()
	}

	@Override
	public ByteBuffer get(long index)
	{
		// connect to server
		// create a new GetCommand give it the this PersistentArray and index in constructor
		// send GetCommand over the stream
		// recive back a new GetCommand from the stream
		// close stream
		// return the result from GetCommand.getResult()
	}

	@Override
	public void put(long index, ByteBuffer bb)
	{
		// connect to server
		// create a new PutCommand and pass in this.PersistentArray, index, and bb to the constructor
		// send PutCommand over the stream
		// close stream
	}

	@Override
	public void delete(long index)
	{
		// connect to server
		// create a new DeleteCommand with this.persistentArray and the index
		// send the DeleteCommand over the stream
		// close stream
	}

	@Override
	public ByteBuffer getMetadata()
	{
		// connect to server
		// create a new GetMetadataCommand with this.persistentArray
		// send the command over the stream
		// recieve the finished command from stream
		// close stream
		// return the command result
	}

	@Override
	public long getRecordCount()
	{
		// connect to server
		// create a new GetRecordCountCommand with this.persistentArray
		// send the command over the stream
		// recieve the finished command from stream
		// close stream
		// return the command result
	}
	
	private Stream connect()
	{
		// create a socket connection
		// open a stream to the IPAddress and port
		// return the stream
	}

	@Override
	public void close() throws IOException
	{
		// connect to server
		// create a new CloseCommand with this.persistentArray
		// send the command over the stream
		// close the stream
	}

	@Override
	public void persistMetadata() throws IOException
	{
		// connect to server
		// create a new PersistMetadataCommand with this.persistentArray
		// send the command over the stream
		// close the stream
	}
}
