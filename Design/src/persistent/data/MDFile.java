package persistent.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MDFile implements AutoCloseable {
	
	private RandomAccessFile raf;
	private FileChannel channel;
	private int MDSize;
	private ByteBuffer AllMetadata;
	
	private MDFile(RandomAccessFile NewRaf) throws IOException{
		raf = NewRaf;
		this.channel = raf.getChannel();

		ByteBuffer bufferForSize = ByteBuffer.allocate(Integer.BYTES);
		channel.read(bufferForSize);
		bufferForSize.rewind();
		MDSize = bufferForSize.getInt();
		
		AllMetadata = ByteBuffer.allocate(MDSize);
		channel.read(AllMetadata);
		this.seek(0);
	}
	
	/**
	 *	Creates a file at the path location, and instantiates a MDFile with a channel to the file
	 *	Throws a FileExistsException if the file at the path has already been created
	 * @throws FileAlreadyExistsException 
	*/
	public static void create(String path, int MDSize) throws FileAlreadyExistsException, IOException{
		if(doesExist(path)) {
			throw new FileAlreadyExistsException(path);
		}
		Files.createFile(Paths.get(path));
		try(FileChannel channel = FileChannel.open(Paths.get(path), StandardOpenOption.WRITE)) {
			ByteBuffer MetaDataSize = ByteBuffer.allocate(Integer.BYTES);
			//Write MDSize to file;
			MetaDataSize.putInt(MDSize);
			MetaDataSize.flip();
			//Write AllMetaData to file
			channel.write(MetaDataSize);
		}
	}
	
	/**
	 *	Instantiates a MDFile object with a channel to the file in the path.
	 *	Throws a FileNotFoundException if there isn't a file at the path
	 * @throws IOException, FileNotFoundException 
	*/
	public static MDFile open(String path) throws IOException, FileNotFoundException{
		if (!doesExist(path)) {
			throw new FileNotFoundException();
		}
		RandomAccessFile raf = new RandomAccessFile(path, "rw");
		MDFile NewMDFile =  new MDFile(raf);
		return NewMDFile;
	}
	
	/**
	 *	Checks a path to see if it exists and that it is points to a file.
	*/
	public static boolean doesExist(String path) {
		return Files.exists(Paths.get(path)) && Files.isRegularFile(Paths.get(path));
	}
	
	/**
	*	Closes resources the class uses
	 * @throws IOException 
	*/
	@Override
	public void close() throws IOException{
		raf.close();
	}
	
	/**
	*	Reads bytes into the buffer and return the number of bytes that were read.
	 * @throws IOException 
	*/
	public int read(ByteBuffer buffer) throws IOException{
		int bytesRead = channel.read(buffer);
		buffer.flip();
		return bytesRead;
	}
	
	/**
	*	Changes the position of the channel to the position given.
	 * @throws IOException 
	*/
	public void seek(long position) throws IOException{
		channel.position(offsetMD(position));
	}
	
	/**
	*	Write the contents of the ByteBuffer to the channel
	 * @throws IOException 
	*/
	public void write(ByteBuffer buffer) throws IOException{
		buffer.rewind();
		channel.write(buffer);
	}
		
	//Return position
	
	/**
	*	Gets the offset of the metadata, to ensure that classes using the MDFile cannot access the metadata.
	*/
	private long offsetMD(long position) {
		if(position < 0) {
			throw new IllegalArgumentException("Position cannot be negative");
		}
		return position + MDSize + Integer.BYTES;
	}
	
	/**
	*	Returns the ByteBuffer obtained in creation or opening, with the position at the beginning of the buffer.
	*/
	public ByteBuffer getMetadata(){
		AllMetadata.rewind();
		return AllMetadata;
	}
	
	/**
	*	Takes in a buffer with the meta data and writes it to the channel, offset to avoid MDSize
	 * @throws IOException 
	*/
	public void persistMetadata() throws IOException{
		AllMetadata.rewind();
		channel.write(AllMetadata, Integer.BYTES);
//		AllMetadata.flip();
	}
	
	public int getMDSize() { return MDSize; }
}


