package persistent.data;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface BlockFile {

    /**
     * Returns the number of bytes required for this object to use in the metadata buffer
     * @return the number of bytes required for this object to use in the metadata buffer
     */
    //static int getMdSize();

    /**
     * Returns an index that can be used for the next row.  Multiple calls are not guaranteed
     * to be sequential and the space is not guaranteed to be empty.
     *
     * @return the index of the allocated space
     * @throws IOException if there is a failure in the IO system
     */
    long allocate() throws IOException;

    /**
     * Persists the current metadata and closes the file
     *
     * @throws IOException if there is a failure in the IO system
     */
    void close() throws IOException;

    /**
     * Gets the bytes at the index
     *
     * @param index the index of the bytes
     * @return bytes that are stored at the index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws IOException               if there is a failure in the IO system
     */
    ByteBuffer get(long index) throws IOException;

    /**
     * Puts the bytes at the index
     *
     * @param index  the index of the bytes
     * @param buffer a buffer of bytes to store
     * @throws IndexOutOfBoundsException if the index is out of bounds
     * @throws IllegalArgumentException  if the buffer is not the correct length
     * @throws IOException               if there is a failure in the IO system
     */
    void put(long index, ByteBuffer buffer) throws IOException;

    /**
     * Gets the number of persisted items
     *
     * @return the number of persisted items
     */
    long getRecordCount();

    /**
     * Gets a ByteBuffer reference with access to bytes of the metadata remaining after this object
     * in the metadata.  The buffer has the position set to 0 which is the location the the parent
     * class will start storing data.  The limit and capacity are both set to the total metadata size
     * minus the relative position
     *
     * @return a ByteBuffer pointing into the shared metadata space.
     * @throws IOException if there is a failure in the IO system
     */
    ByteBuffer getMetadata() throws IOException;

    /**
     * Copies the instance defined metadata fields into the metadata buffer and calls the persist method
     * on any subclasses, resulting in the buffer being written into the persisted file.
     *
     * @throws IOException if there is a failure in the IO system
     */
    void persistMetadata() throws IOException;
}
