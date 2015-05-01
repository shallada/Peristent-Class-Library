package persistent.collections;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface PersistentArray
{
        /**
         * Returns the number of bytes the implementation will need to pass to the factory
         *   to ensure enough metadata space
         * @return the number of bytes the implementation will need to pass to the factory
         *   to ensure enough metadata space
         */
        //static int getMdSize();

        /**
         * Returns an index that can be used for the next row
         * @return the index of the allocated space
         */
        long allocate() throws IOException;

        /**
         * Persists the current metadata and closes the array
         */
        void close() throws IOException;

        /**
         * Removes the bytes at the index
         * @param index the index of the bytes to remove
         */
        void delete(long index) throws IOException;

        /**
         * Gets the bytes at the index
         * @param index the index of the bytes
         * @return bytes that are stored at the index
         */
        ByteBuffer get(long index) throws IOException;

        /**
         * Puts the bytes at the index
         * @param index the index of the bytes
         * @param buffer a buffer of bytes to store
         */
        void put(long index, ByteBuffer buffer) throws IOException;

        /**
         * Gets the number of persisted items
         * @return the number of persisted items
         */
        long getRecordCount();

        /**
         * Gets a ByteBuffer reference with access to bytes of the metadata remaining after this object
         *   in the metadata.  The buffer has the position set to 0 which is the location the the parent
         *   class will start storing data.  The limit and capacity are both set to the total metadata size
         *   minus the relative position
         * @return a bytebuffer pointing into the shared metadata space.
         */
        ByteBuffer getMetadata() throws IOException;

        /**
         * Copies the instance defined metadata fields into the metadata buffer and calls the persist method
         *   on any subclasses, resulting in the buffer being written into the persisted file.
         */
        void persistMetadata() throws IOException;
}
