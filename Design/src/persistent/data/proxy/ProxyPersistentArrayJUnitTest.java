package persistent.data.proxy;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class ProxyPersistentArrayJUnitTest
{
	@Test
	public void testSendAndRecieveOnNetwork()
	{
		// the inmemorypersistentarray is stricktly for testing.
		InMemoryPersistentArray impa = new InMemoryPersistentArray("", 10, 10);
		
		Thread serverThread = new Thread(() -> {
			ProxyServer server = new ProxyServer(impa, 55444);
			server.start();
		});
		serverThread.start();
		
		// used 2 proxies to make sure server doesn't care about which proxy is calling.
		ProxyPersistentArray ppa = new ProxyPersistentArray("localhost", 55444);
		ProxyPersistentArray ppa2 = new ProxyPersistentArray("localhost", 55444);
		try
		{
			assertEquals(ppa.allocate(), 0);
			assertEquals(ppa.allocate(), 1);
			assertEquals(ppa2.allocate(), 2);
			assertEquals(ppa.allocate(), 3);
			assertEquals(ppa2.allocate(), 4);
		} 
		catch (IOException e)
		{
			fail();
		}
	}
}
