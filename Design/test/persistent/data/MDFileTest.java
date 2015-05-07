package persistent.data;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

//Notes:
//testWithNoSizeByteBuffer
//testWriteWithEmptyBuffer
public class MDFileTest {
	
	private static final String testPath = "file.txt";
	private static final String testMDPath = "testMD.txt";
	
	private MDFile getTestMDFile() {
		MDFile testFile = null;
		try {
			MDFile.create(testPath, 0);
			testFile = MDFile.open(testPath);
		} catch (IOException e) {
			Assert.fail();
		}
		return testFile;		
	}
	
	private void closeMDFile(MDFile file) {
		if(file != null) {
			try {
				file.close();
			} catch (IOException e) {
				Assert.fail();
			}
		}
	}
	
	private void writeValue(MDFile file, long value) {
		ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
		bb.putLong(value);
		try {
			file.write(bb);
		} catch (IOException e) {
			Assert.fail();
		}
	}
	
	@After
	public void deleteCreatedFiles() {
		
		try {
			Files.delete(Paths.get(testPath));
		} catch (IOException e) {
			
		}
		
		try {
			Files.delete(Paths.get(testMDPath));
		} catch (IOException e) {
			
		}
	}
	
	//Test does exist
	@Test
	public void testDoesExist() {
		Assert.assertFalse(MDFile.doesExist(testPath));
		try {
			MDFile.create(testPath, 0);
		} catch (IOException e) {
			Assert.fail();
		}
		Assert.assertTrue(MDFile.doesExist(testPath));
	}
	
	//Test create when file does not exist
	@Test
	public void testCreateWhenFileDoesNotExist() {
		try {
			MDFile.create(testPath, 0);
		} catch (IOException e) {
			Assert.fail();
		}
		
		MDFile.doesExist(testPath);
	}
	
	//Test Create when file exists
	@Test
	public void testCreateWhenFileExists() {
		try {
			MDFile.create(testPath, 0);
			Assert.assertTrue(MDFile.doesExist(testPath));
			MDFile.create(testPath, 0);
			Assert.fail();
		} catch (IOException e) {
			
		}
	}
	
	//Test Open when file exists
	@Test
	public void testOpenWhenFileExists() {
		MDFile testFile = null;
		try {
			MDFile.create(testPath, 0);
			testFile = MDFile.open(testPath);
			Assert.assertNotNull(testFile);
			testFile.close();
		} catch (IOException e) {
			closeMDFile(testFile);
			Assert.fail();
		}
	}
	
	//Test Open when file does not exist
	@Test
	public void testOpenWhenFileDoesNotExist() {
		try {
			MDFile tryOpen = MDFile.open(testPath);
			closeMDFile(tryOpen); // In case it is somehow opened
			Assert.fail();
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			Assert.fail();
		}
	}
	
	//Test seek with legal value
	@Test
	public void testSeekWithLegalValue() {
		long writeValue = 4;
		MDFile testFile = getTestMDFile();
		writeValue(testFile, writeValue);
		try {
			testFile.seek(0);
			ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
			testFile.read(bb);
			long readValue = bb.getLong();
			Assert.assertEquals(writeValue, readValue);
		} catch (IOException e) {
			closeMDFile(testFile);
			Assert.fail();
		}
		closeMDFile(testFile);
	}
	
	//Test seek with negative value
	@Test
	public void testSeekWithNegativeValue() {
		MDFile testFile = getTestMDFile();
		try {
			testFile.seek(-1);
			closeMDFile(testFile); // In case exception not thrown
			Assert.fail();
		} catch(IllegalArgumentException ex) {
			
		} catch (IOException e) {
			closeMDFile(testFile);
			Assert.fail();
		}
		closeMDFile(testFile);
	}
	
	//Test seek with value greater than file size
	@Test
	public void testSeekWithValueGreaterThanFileSize() {
		
		long writeValue = 4;
		MDFile testFile = getTestMDFile();
		try {
			testFile.seek(100);

			writeValue(testFile, writeValue);
			
			ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
			testFile.seek(100);
			testFile.read(bb);
			long readValue = bb.getLong();
			
			Assert.assertEquals(writeValue, readValue);
		} catch (IOException e) {
			closeMDFile(testFile);
			Assert.fail();
		}
		closeMDFile(testFile);
	}
	
	@Test
	public void testSeekWithLargeMetadataSize() {
		MDFile testFile = null;
		try {
			MDFile.create(testPath, 1000);
			testFile = MDFile.open(testPath);
			

			long writeValue = 4;
			writeValue(testFile, writeValue);
			try {
				testFile.seek(0);
				ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
				testFile.read(bb);
				long readValue = bb.getLong();
				Assert.assertEquals(writeValue, readValue);
			} catch (IOException e) {
				closeMDFile(testFile);
				Assert.fail();
			}
			closeMDFile(testFile);
		} catch (IOException e1) {
			closeMDFile(testFile);
			Assert.fail();
		}
	}
	
	//Test read with no size buffer
	@Test
	public void testReadWithNoSizeBuffer() {
		long writeValue = 4;
		MDFile testFile = getTestMDFile();
		writeValue(testFile, writeValue);
		try {
			ByteBuffer bb = ByteBuffer.allocate(0);
			int numBytesRead = testFile.read(bb);
			Assert.assertEquals(0, numBytesRead);
		} catch (IOException e) {
			closeMDFile(testFile);
			Assert.fail();
		}
		closeMDFile(testFile);
	}
	
	//Test read with normal size buffer
	@Test
	public void testReadWithNormalSizeBuffer() {
		long writeValue = 4;
		MDFile testFile = getTestMDFile();
		writeValue(testFile, writeValue);
		try {
			testFile.seek(0);
			ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
			testFile.read(bb);
			long readValue = bb.getLong();
			Assert.assertEquals(writeValue, readValue);
		} catch (IOException e) {
			closeMDFile(testFile);
			Assert.fail();
		}
		closeMDFile(testFile);
	}
	
	//Test read with greater than file size buffer
	@Test
	public void testReadWithGreaterThanFileSizeBuffer() {
		long writeValue = 4;
		MDFile testFile = getTestMDFile();
		writeValue(testFile, writeValue);
		try {
			testFile.seek(0);
			ByteBuffer bb = ByteBuffer.allocate(100);
			int numBytesRead = testFile.read(bb);
			Assert.assertEquals(Long.BYTES, numBytesRead);
			Assert.assertEquals(0, bb.position());
			long readValue = bb.getLong();
			Assert.assertEquals(writeValue, readValue);
		} catch (IOException e) {
			closeMDFile(testFile);
			Assert.fail();
		}
		closeMDFile(testFile);
	}
	
	//Test read from empty file
	@Test
	public void testReadFromEmptyFile() {
		MDFile testFile = getTestMDFile();
		try {
			testFile.seek(0);
			ByteBuffer bb = ByteBuffer.allocate(100);
			int numBytesRead = testFile.read(bb);
			Assert.assertEquals(-1, numBytesRead);
			Assert.assertEquals(0, bb.position());
		} catch (IOException e) {
			closeMDFile(testFile);
			Assert.fail();
		}
		closeMDFile(testFile);
	}
	
	//Test write with no size buffer
	@Test
	public void testWriteWithNoSizeBuffer() {
		MDFile testFile = getTestMDFile();
		try {
			ByteBuffer noSizeBB = ByteBuffer.allocate(0);
			testFile.write(noSizeBB);
		} catch(IOException e) {
			closeMDFile(testFile);
			Assert.fail();
		}
		closeMDFile(testFile);
	}
	
	//Test write with normal size buffer
	@Test
	public void testWriteWithNormalBuffer() {
		MDFile testFile = getTestMDFile();
		try {
			long writeValue = 4;
			ByteBuffer writeBB = ByteBuffer.allocate(Long.BYTES);
			writeBB.putLong(writeValue);
			testFile.write(writeBB);
			
			ByteBuffer readBB = ByteBuffer.allocate(Long.BYTES);
			testFile.seek(0);
			testFile.read(readBB);
			long readValue = readBB.getLong();
			Assert.assertEquals(writeValue, readValue);
		} catch(IOException ex) {
			closeMDFile(testFile);
			Assert.fail();
		}
		closeMDFile(testFile);
	}
	
	//Test getMetaData is ready to read
	@Test
	public void testGetMetaData() {
		MDFile testFile = null;
		//Make File
		try {
			int testSize = 100;
			MDFile.create(testPath, testSize);
			testFile = MDFile.open(testPath);
			//Get MetaData
			ByteBuffer metaData = testFile.getMetadata();
			//Check MetaData is property size and empty
			closeMDFile(testFile);
			Assert.assertEquals(testSize, testFile.getMDSize());
			Assert.assertEquals(testSize, metaData.capacity());
			for(Byte b : metaData.array()) {
				if(b != 0) {
					closeMDFile(testFile);
					Assert.fail();
				}
			}			
		} catch (IOException e) {
			closeMDFile(testFile);
			Assert.fail();
		}
		
	}
	
	//Test persistMetadata changes metadata
	@Test
	public void testPersistMetaData() {
		MDFile testFile = null;
		try {
			int testSize = 100;
			//Make File
			MDFile.create(testMDPath, testSize);
			testFile = MDFile.open(testMDPath);
			//Get MetaData
			ByteBuffer metaData = testFile.getMetadata();
			//Write test data to MetaData
			long longMD = 5;
			byte byteMD = 2;
			char charMD = 'a';
			metaData.putLong(longMD);
			metaData.put(byteMD);
			metaData.putChar(charMD);
			//Persist MetaData
			testFile.persistMetadata();
			//Get MetaData Again
			ByteBuffer afterPersistMD = testFile.getMetadata();
			//Ensure Bytes from written metaData and new GetMetaData again are same
			closeMDFile(testFile);
			Assert.assertEquals(metaData, afterPersistMD);
			for(int i = 0; i < metaData.limit(); i++) {
				Assert.assertEquals(metaData.get(i), afterPersistMD.get(i));
			}
			
		} catch (IOException e) {
			closeMDFile(testFile);
			Assert.fail();
		}
	}
		
	
	//Test persistMetadata changes metadata on file
	@Test
	public void testPersistMetadataChangesOnFile() {
		MDFile testFile = null;
		try {
			int testSize = 100;
			//Make File
			MDFile.create(testMDPath, testSize);
			testFile = MDFile.open(testMDPath);
			//Get MetaData
			ByteBuffer metaData = testFile.getMetadata();
			//Write test data to MetaData
			long longMD = 5;
			byte byteMD = 2;
			char charMD = 'a';
			metaData.putLong(longMD);
			metaData.put(byteMD);
			metaData.putChar(charMD);
			//Persist MetaData
			testFile.persistMetadata();
			//Close file
			closeMDFile(testFile);
			//Open file again
			testFile = MDFile.open(testMDPath);
			//Get MetaData
			ByteBuffer afterPersistMD = testFile.getMetadata();
			//Ensure writen metadata is GetMetaData
			closeMDFile(testFile);
			for(int i = 0; i < metaData.limit(); i++) {
				Assert.assertEquals(metaData.get(i), afterPersistMD.get(i));
			}
			
		} catch (IOException e) {
			closeMDFile(testFile);
			Assert.fail();
		}
	}
}
