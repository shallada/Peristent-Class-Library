package persistent.collections.Transactions.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteBuffer;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import persistent.collections.Transactions.TransactionStateData;

public class TransactionStateDataTest {
	private int byteBufferTestAllocate;
	public ExpectedException thrown = ExpectedException.none();

	@Test(expected = IOException.class)
	public void testGetData() throws IOException {
		ByteBuffer testBuffer = ByteBuffer.allocate(byteBufferTestAllocate);
		TransactionStateData data = new TransactionStateData(testBuffer, false);
		ByteBuffer testData = null;
		testData = data.getData();

		Assert.assertEquals(testData, testBuffer);

		data.setDeleted(true);
		testData = data.getData();

		assertThat(testData, is(not(testBuffer)));
	}

	@Test
	public void testSetData() {
		ByteBuffer testBuffer = ByteBuffer.allocate(byteBufferTestAllocate);
		TransactionStateData data = new TransactionStateData(null, true);
		
		Assert.assertTrue(data.isDeleted());
		
		data.setData(testBuffer);
		
		Assert.assertTrue(!data.isDeleted());
	}
}
