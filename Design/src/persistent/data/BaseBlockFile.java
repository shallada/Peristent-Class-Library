package persistent.data;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BaseBlockFile implements BlockFile
{
    protected static int ourMetadataSize =
            Long.BYTES // nextIndex
                    + Integer.BYTES // recordSize
                    + Long.BYTES; // recordCount
    protected long nextIndex;
    protected int recordSize;
    protected long recordCount;
    protected ByteBuffer metadata;
    protected MdFileTemp data;

    /**
     * Returns the number of bytes the implementation will need to pass to the factory
     *   to ensure enough metadata space
     * @return the number of bytes the implementation will need to pass to the factory
     *   to ensure enough metadata space
     */
    public static int getMdSize()
    {
        return ourMetadataSize;
    }

    /**
     * Returns an index that can be used for the next row
     * @return the index of the allocated space
     * @throws IOException if there is a failure in the IO system
     */
    @Override
    public long allocate() throws IOException
    {
        long tempIndex = nextIndex++;
        persistMetadata();
        return tempIndex;
    }

    /**
     * Gets the bytes at the index
     * @param index the index of the bytes
     * @return bytes that are stored at the index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws IOException if there is a failure in the IO system
     */
    @Override
    public ByteBuffer get(long index) throws IOException
    {
        if (index < 0 || index >= nextIndex)
            throw new IndexOutOfBoundsException();

        ByteBuffer buffer = ByteBuffer.allocate(recordSize);

        long readIndex = (index * recordSize);
        data.seek(readIndex);
        int bytesRead = data.read(buffer);

        // make sure we read the number of bytes that we should have
        assert(bytesRead == recordSize);

        return buffer;
    }

    /**
     * Puts the bytes at the index
     * @param index the index of the bytes
     * @param buffer a buffer of bytes to store
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws IllegalArgumentException if the buffer is not the correct length
     * @throws IOException if there is a failure in the IO system
     */
    @Override
    public void put(long index, ByteBuffer buffer) throws IOException
    {
        if (index < 0 || index >= nextIndex)
            throw new IndexOutOfBoundsException();

        if (buffer.capacity() != recordSize)
            throw new IllegalArgumentException("buffer is not the correct size");

        long writeIndex = (index * recordSize);

        data.seek(writeIndex);
        data.write(buffer);
    }

    /**
     * Gets the number of persisted items
     * @return the number of persisted items
     */
    @Override
    public long getRecordCount()
    {
        return recordCount;
    }

    /**
     * Creates a BaseBlockFile instance
     * @param path the location for create the file
     * @param mdSize the size to allocate for super class metadata
     * @param recordSize the size of a record
     * @throws IOException if there is a failure in the IO system
     */
    public static void create(String path, int mdSize, int recordSize) throws IOException
    {
        if (MdFileTemp.exists(path))
            throw new IOException();

        BaseBlockFile bbFile = new BaseBlockFile();
        MdFileTemp.create(path, mdSize + ourMetadataSize);
        bbFile.data = MdFileTemp.open(path);
        bbFile.nextIndex = 0;
        bbFile.recordSize = recordSize;
        bbFile.metadata = bbFile.data.getMetadata();

        bbFile.data.persistMetadata();
        bbFile.data.close();
        bbFile.close();
    }

    /**
     * Returns if a file exists at the path
     * @param path the path to check
     * @return true if a file exists at the path, otherwise false
     */
    public static boolean exists(String path) {
        return MdFileTemp.exists(path);
    }

    /**
     * Closes the instance of the BaseBlockFile
     * @throws IOException if there is a failure in the IO system
     */
    @Override
    public void close()throws IOException
    {
        data.persistMetadata();
        data.close();
    }

    /**
     * Opens a BaseBlockFile that has been saved to the specified path
     * @param path the path of the file
     * @return an instance of BaseBlockFile that has been loaded from a file
     * @throws IOException if there is a failure in the IO system
     */
    public static BaseBlockFile open(String path) throws IOException
    {
        BaseBlockFile bbFile = new BaseBlockFile();
        bbFile.data = MdFileTemp.open(path);
        bbFile.metadata = bbFile.data.getMetadata();
        bbFile.getMetadata();
        return bbFile;
    }

    /**
     * Gets a ByteBuffer reference with access to bytes of the metadata remaining after this object
     *   in the metadata.  The buffer has the position set to 0 which is the location the the parent
     *   class will start storing data.  The limit and capacity are both set to the total metadata size
     *   minus the relative position
     * @return a bytebuffer pointing into the shared metadata space.
     * @throws IOException if there is a failure in the IO system
     */
    @Override
    public ByteBuffer getMetadata() throws IOException
    {
        metadata = data.getMetadata();
        nextIndex = metadata.getLong();
        recordSize = metadata.getInt();
        return metadata.slice();
    }

    /**
     * Copies the instance defined metadata fields into the metadata buffer and calls the persist method
     *   on any subclasses, resulting in the buffer being written into the persisted file.
     * @throws IOException if there is a failure in the IO system
     */
    @Override
    public void persistMetadata() throws IOException
    {
        metadata.position(0);
        metadata.putLong(nextIndex);
        metadata.putInt(recordSize);
        data.persistMetadata();
    }
}
