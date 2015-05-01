package persistent.collections;

import persistent.data.BaseBlockFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BasePersistentArray
        extends BaseBlockFile
        implements PersistentArray
{
    protected static int ourMetadataSize =
                    Long.BYTES // nextIndex
                    + Integer.BYTES // recordSize
                    + Long.BYTES // recordCount
                    + Long.BYTES; // deleteIndex
    // long deleteIndex;

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
        // set temp returnValue
        // if deleteIndex is less than 0
        //      set returnValue to nextIndex
        //      increment NextIndex

        // else deleteIndex is greater than or equal to 0
        // get the bytes at deleteIndex
        // set bytes position (in bytebuffer) to 0

        // set returnValue to deleteIndex
        // set deleteIndex to bytes.readlong.  this will set the deleteIndex to the value stored in the previous deleteIndex

        // persist metadata
        // return returnvalue
        return 0;
    }

    /**
     * Removes the bytes at the index
     * @param index the index of the bytes to remove
     */
    @Override
    public void delete(long index) throws IOException
    {
        // allocate a byteBuffer of record size
        // write the delete index to the bytebuffer at position 0
        // put bytebuffer at index
        // update deleteIndex to be index
        // persist metadata
    }

    public static void create(String path, int mdSize, int recordSize) throws IOException
    {
        // make instance of BasePersistentArray
        // set mdSize to be (mdsizePassedIn + mdSizeForBaseBlockFile)
        // Create MdFile with path and mdsize
        // set BasePersistentArray internal data file to mdfile
        // call BasePersistentArray's getMetadata to initialize internal md

        // set BasePersistentArray internal nextIndex to 0
        // set BasePersistentArray internal recordSize to recordSize
        // set BasePersistentArray internal recordCount to 0
        // set BasePersistentArray internal deleteIndex to -1
        // call BasePersistentArray's persistMetadata to save md
        // close BasePersistentArray
        //
    }

    public static BasePersistentArray open(String path) throws IOException
    {
        // make instance of BasePersistentArray
        // open MdFile at path
        // set BasePersistentArray internal data to mdfile
        // call BasePersistentArray's getMetadata to load md
        // return BasePersistentArray
        return null;
    }

    public ByteBuffer getMetadata() throws IOException
    {
        // get metadata bytebuffer from data file
        // set internal metadata to metadata buffer
        // read nextIndex from metadata
        // read recordSize from metadata
        // read recordCount from metadata
        // read deleteIndex from metadata
        // return new ByteBuffer instance sliced from the current position (ready for our parent)
        return null;
    }

    public void persistMetadata() throws IOException
    {
        // set metadata position to 0
        // write nextIndex to metadata
        // write recordSize to metadata
        // write recordCount to metadata
        // read deleteIndex from metadata
        // call persistMetadata on data file
    }

}