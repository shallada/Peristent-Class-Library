package persistent.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BaseBlockFile implements BlockFile
{
    protected static int ourMetadataSize =
              Long.BYTES // nextIndex
            + Integer.BYTES // recordSize
            + Long.BYTES; // recordCount
    // long nextIndex;
    // int recordSize;
    // long recordCount
    // ByteBuffer metadata;
    // MdFIle data;

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
     */
    @Override
    public long allocate() throws IOException
    {
        // save nextIndex to tempIndex and increment
        // increment recordCount
        // persist metadata
        // return tempIndex;
        return 0;
    }

    /**
     * Gets the bytes at the index
     * @param index the index of the bytes
     * @return bytes that are stored at the index
     */
    @Override
    public ByteBuffer get(long index) throws IOException
    {
        // allocate a ByteBuffer of record size
        // seek to position (index * recordSize) in the data file
        // read bytes from data file into buffer
        // make sure we read the correct number of bytes
        // return the buffer
        return null;
    }

    /**
     * Puts the bytes at the index
     * @param index the index of the bytes
     * @param buffer a buffer of bytes to store
     */
    @Override
    public void put(long index, ByteBuffer buffer) throws IOException
    {
        // seek to position (index * recordSize) in the data file
        // write buffer to data file
    }

    /**
     * Gets the number of persisted items
     * @return the number of persisted items
     */
    @Override
    public long getRecordCount()
    {
        // return recordCount
        return 0;
    }

    public static void create(String path, int mdSize, int recordSize) throws IOException
    {
        // make instance of BaseBlockFile
        // set mdSize to be (mdsizePassedIn + mdSizeForBaseBlockFile)
        // Create MdFile with path and mdsize
        // set BaseBlockFile internal data to mdfile
        // call BaseBlockFile's getMetadata to initialize internal md

        // set BaseBlockFile internal nextIndex to 0
        // set BaseBlockFile internal recordSize to recordSize
        // set BaseBlockFile internal recordCount to 0
        // call BaseBlockFile's persistMetadata to save md
        // close BaseBlockFile
    }

    public static boolean exists(String path) {
        // delegate to static MdFile
        return false;
    }

    @Override
    public void close()throws IOException
    {
        // Call persistMetadata to make sure we are in a consistent state
        // close internal data file
    }

    public static BaseBlockFile open(String path) throws IOException
    {
        // make instance of BaseBlockFile
        // open MdFile at path
        // set BaseBlockFile internal data to mdfile
        // call BaseBlockFile's getMetadata to load md
        // return BaseBlockFile
        return null;
    }

    @Override
    public ByteBuffer getMetadata() throws IOException
    {
        // get metadata bytebuffer from data file
        // set internal metadata to metadata buffer
        // read nextIndex from metadata
        // read recordSize from metadata
        // read recordCount from metadata
        // return new ByteBuffer instance sliced from the current position (ready for our parent)
        return null;
    }

    @Override
    public void persistMetadata() throws IOException
    {
        // set metadata position to 0
        // write nextIndex to metadata
        // write recordSize to metadata
        // write recordCount to metadata
        // call persistMetadata on data file
    }
}
