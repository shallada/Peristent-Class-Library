package persistent.collections.Transactions.tests;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;

import persistent.collections.Transactions.AllocateOperation;
import persistent.collections.Transactions.Operation;

public class OperationTest {

	private long testRecordSize = 100;
	private long testRef = 1;
	private long testRefChanged = 2;
	@Test
	public void testLoad() {
		Operation op = new AllocateOperation(UUID.randomUUID(), testRecordSize, testRef);
		ByteBuffer testOutput = ByteBuffer.allocate(op.getSize());
		
		testOutput.putLong(op.getRecordSize());
		testOutput.putLong(testRefChanged);
		testOutput.put(op.getTransactionPersistentArrayId().toString().getBytes());
		testOutput.put(op.getOldData());
		testOutput.put(op.getData());
		testOutput.flip();
		
		op.load(testOutput);
		
		Assert.assertEquals(testRefChanged, op.getRef());
		
		
	}

	@Test
	public void testSerialize() {
		final int UUID_SIZE_IN_BYTES = 36;
		
		UUID testUUID = UUID.randomUUID();
		Operation op = new AllocateOperation(testUUID, testRecordSize, testRef);
		ByteBuffer testOutput = ByteBuffer.allocate(op.getSize());
		op.serialize(testOutput);
		
		testOutput.flip();
		
		byte[] uuid = new byte[UUID_SIZE_IN_BYTES];
		
		long recordSize = testOutput.getLong();
		long ref = testOutput.getLong();
		testOutput.get(uuid, 0, uuid.length);
		UUID resultUUID = null;
		try {
			resultUUID = UUID.fromString(new String(uuid, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Assert.assertEquals(testRecordSize, recordSize);
		Assert.assertEquals(testRef, ref);
		Assert.assertEquals(testUUID, resultUUID);
	}

}
