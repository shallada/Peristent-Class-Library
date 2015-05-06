package Design.src.persistent.data.proxy;

import java.net.Socket;

import persistent.collections.PersistentArray;

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
		// create a socket
		// while done is false
			// listen on socket with port for a connection
			// once connection is established pass stream to getCall on new thread
	}
	
	public void close()
	{
		// call persistentArray.close()
		this.done = true;
	}
	
	private void getCall(Socket socket)
	{
		// create an objectinput stream
		// pull down a command from the stream
		// run the command passing in the persistentArray
		// if command isReturnable
			// create an objectoutput stream
			// send the completed command back over the stream
		// close stream
	}
}
