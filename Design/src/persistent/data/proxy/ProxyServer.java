package persistent.data.proxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import persistent.collections.PersistentArray;
import persistent.data.proxy.commands.Command;

public class ProxyServer
{
	private boolean done;
	private int port;
	private PersistentArray persistentArray;
	
	public ProxyServer(PersistentArray persistentArray, int port)
	{
		this.persistentArray = persistentArray;
		this.done = false;
		this.port = port;
	}
	
	public void start()
	{
		ServerSocket socket = null;
		try
		{
			socket = new ServerSocket(this.port);
		} 
		catch (IOException e)
		{
			System.out.println("error setting up server socket. " + e.getMessage());
			e.printStackTrace();
		}
		
		while(!done)
		{
			try
			{
				Socket newSocket = socket.accept();
				new Thread(() -> this.getCall(newSocket)).start();
			}
			catch (IOException e)
			{
				System.out.println("error connecting with socket. " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public void close()
	{
		this.done = true;
	}
	
	private void getCall(Socket socket)
	{
		try
		{
			ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
			Command received = (Command)inputStream.readObject();	
			received.execute(persistentArray);
			if(received.isReturnable())
			{
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				outputStream.writeObject(received);
				outputStream.flush();
			}
		}
		catch(IOException e)
		{
			System.out.println("error with the stream. " + e.getMessage());
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e)
		{
			System.out.println("don't have the command class to cast from stream. " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			try
			{
				socket.close();
			} 
			catch (IOException e)
			{
				System.out.println("error closing the socket. " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
