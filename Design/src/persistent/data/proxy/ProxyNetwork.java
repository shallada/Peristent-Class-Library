package persistent.data.proxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import persistent.data.proxy.commands.*;

class ProxyNetwork
{
	private String ipAddress;
	private int port;
	
	protected ProxyNetwork(String ipAddress, int port)
	{
		this.ipAddress = ipAddress;
		this.port = port;
	}

	protected long allocate() throws IOException
	{
		Socket socket = this.connect();
		AllocateCommand ac = new AllocateCommand();
		this.sendCommand(socket, ac);
		AllocateCommand returned = (AllocateCommand)this.recieveCommand(socket);
		socket.close();
		return returned.getResult();
	}

	protected ByteBuffer get(long index) throws IOException
	{
		Socket socket = this.connect();
		GetCommand getCommand = new GetCommand(index);
		this.sendCommand(socket, getCommand);
		GetCommand returned = (GetCommand)this.recieveCommand(socket);
		socket.close();
		return returned.getResult();
	}

	protected void put(long index, ByteBuffer bb) throws IOException
	{
		Socket socket = this.connect();
		PutCommand putCommand = new PutCommand(index, bb);
		this.sendCommand(socket, putCommand);
		socket.close();
	}

	protected void delete(long index) throws IOException
	{
		Socket socket = this.connect();
		DeleteCommand deleteCommand = new DeleteCommand(index);
		this.sendCommand(socket, deleteCommand);
		socket.close();
	}

	protected ByteBuffer getMetadata() throws IOException
	{
		Socket socket = this.connect();
		GetMetadataCommand getCommand = new GetMetadataCommand();
		this.sendCommand(socket, getCommand);
		GetMetadataCommand returned = (GetMetadataCommand)this.recieveCommand(socket);
		socket.close();
		return returned.getResult();
	}

	protected long getRecordCount() throws UncheckedIOException
	{
		long result = 0;
		try
		{
			Socket socket = this.connect();
			GetRecordCountCommand getCommand = new GetRecordCountCommand();
			this.sendCommand(socket, getCommand);
			GetRecordCountCommand returned = (GetRecordCountCommand)this.recieveCommand(socket);
			socket.close();
			result = returned.getResult();
		}
		catch(IOException ex)
		{
			throw new UncheckedIOException(ex);
		}
		return result;
	}
	
	protected Socket connect() throws UnknownHostException, IOException
	{
		Socket client = null;
		client = new Socket(this.ipAddress, this.port);
		return client;
	}
	
	protected void sendCommand(Socket socket, Command command) throws IOException
	{
		ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
		outputStream.writeObject(command);
		outputStream.flush();
	}
	
	protected Command recieveCommand(Socket socket) throws IOException
	{
		Command returned = null;
	
		try
		{
			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
			returned = (Command)inputStream.readObject();
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("error casting object from stream. " + e.getMessage());
			e.printStackTrace();
		}
		return returned;
	}

	protected void close() throws IOException
	{
		Socket socket = this.connect();
		CloseCommand closeCommand = new CloseCommand();
		this.sendCommand(socket, closeCommand);
		socket.close();
	}

	protected void persistMetadata() throws IOException
	{
		Socket socket = this.connect();
		PersistMetaDataCommand pmdCommand = new PersistMetaDataCommand();
		this.sendCommand(socket, pmdCommand);
		socket.close();
	}
}
