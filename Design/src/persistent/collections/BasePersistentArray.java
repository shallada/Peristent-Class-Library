package persistent.collections;

import persistent.data.BaseBlockFile;
import persistent.data.InMemoryPData;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BasePersistentArray
        extends BaseBlockFile
        implements PersistentArray {
    private static int MIN_REC_SIZE = Long.BYTES * 2;
    private static long DELETED_ITEM = 0xFFFFFEEl;

    protected static int ourMetadataSize =
            Long.BYTES // nextIndex
                    + Integer.BYTES // recordSize
                    + Long.BYTES // recordCount
                    + Long.BYTES; // deleteIndex
    protected long deleteIndex;

    /**
     * Returns the number of bytes the implementation will need to pass to the factory
     * to ensure enough metadata space
     *
     * @return the number of bytes the implementation will need to pass to the factory
     * to ensure enough metadata space
     */
    public static int getMdSize() {
        return ourMetadataSize;
    }

    /**
     * Returns an index that can be used for the next row
     *
     * @return the index of the allocated space
     * @throws IOException if there is a failure in the IO system
     */
    @Override
    public long allocate() throws IOException {
        long returnValue;
        if (deleteIndex < 0) {
            returnValue = nextIndex++;
        } else {
            ByteBuffer buffer = super.get(deleteIndex);
            buffer.position(0);
            returnValue = deleteIndex;
            // Set the deleteIndex to the value stored in the previous deleteIndex
            deleteIndex = buffer.getLong();
        }
        recordCount++;
        persistMetadata();
        return returnValue;
    }

    /**
     * Removes the bytes at the index
     *
     * @param index the index of the bytes to remove
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws IOException               if there is a failure in the IO system
     */
    @Override
    public void delete(long index) throws IOException {
        if (index < 0 || index >= nextIndex)
            throw new IndexOutOfBoundsException();

        ByteBuffer buffer = get(index);
        buffer.position(0);
        long deletedMark = buffer.getLong();
        if (deletedMark == DELETED_ITEM) {
            // I don't know if we should, or should not throw on this. - Blake
            //throw new IllegalStateException("Item at " + " has already been deleted");
            return; // Oh no, I added a return statement.
        }
        // write the deleteFlag and deleteIndex over the top of the record
        buffer.position(0);
        buffer.putLong(DELETED_ITEM);
        buffer.putLong(deleteIndex);
        put(index, buffer);
        deleteIndex = index;
        recordCount--;
        persistMetadata();
    }

    /**
     * Creates a BasePersistentArray instance
     *
     * @param path       the location for create the file
     * @param mdSize     the size to allocate for super class metadata
     * @param recordSize the size of a record
     * @throws IOException                   if there is a failure in the IO system
     * @throws UnsupportedOperationException of the record size is smaller than 2x sizeof(long)
     */
    public static void create(String path, int mdSize, int recordSize) throws IOException {
        // We need to bind the recordSize to be larger than our deletion flag.
        // this was NOT discussed in class - Blake
        if (recordSize < MIN_REC_SIZE)
            throw new UnsupportedOperationException("Record has to be larger than " + MIN_REC_SIZE);

        BasePersistentArray bpa = new BasePersistentArray();
        InMemoryPData.create(path, mdSize + ourMetadataSize);
        bpa.data = InMemoryPData.open(path);
        bpa.nextIndex = 0;
        bpa.recordSize = recordSize;
        bpa.recordCount = 0;
        bpa.deleteIndex = -1;
        bpa.metadata = bpa.data.getMetadata();
        bpa.persistMetadata();
        bpa.close();
    }

    /**
     * Opens a BasePersistentArray that has been saved to the specified path
     *
     * @param path the path of the file
     * @return an instance of BasePersistentArray that has been loaded from a file
     * @throws IOException if there is a failure in the IO system
     */
    public static BasePersistentArray open(String path) throws IOException {
        BasePersistentArray bpa = new BasePersistentArray();
        bpa.data = InMemoryPData.open(path);
        bpa.metadata = bpa.getMetadata();
        return bpa;
    }

    /**
     * Gets a ByteBuffer reference with access to bytes of the metadata remaining after this object
     * in the metadata.  The buffer has the position set to 0 which is the location the the parent
     * class will start storing data.  The limit and capacity are both set to the total metadata size
     * minus the relative position
     *
     * @return a ByteBuffer pointing into the shared metadata space.
     * @throws IOException if there is a failure in the IO system
     */
    @Override
    public ByteBuffer getMetadata() throws IOException {
        metadata = data.getMetadata();
        nextIndex = metadata.getLong();
        recordSize = metadata.getInt();
        recordCount = metadata.getLong();
        deleteIndex = metadata.getLong();
        return metadata.slice();
    }

    /**
     * Copies the instance defined metadata fields into the metadata buffer and calls the persist method
     * on any subclasses, resulting in the buffer being written into the persisted file.
     *
     * @throws IOException if there is a failure in the IO system
     */
    @Override
    public void persistMetadata() throws IOException {
        metadata.position(0);
        metadata.putLong(nextIndex);
        metadata.putInt(recordSize);
        metadata.putLong(recordCount);
        metadata.putLong(deleteIndex);
        data.persistMetadata();
    }

}