package persistance.collections;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by stephen on 4/27/15.
 */
public class CachedPersistentArray<E> implements PersistentArray{

    private PersistentArray wrappedPA;
    private SizedLinkedList<E> itemCache;
    private String pathToOpen;
    // long nextIndex;
    // int recordSize;
    // long recordCount
    // ByteBuffer metadata;
    // MdFIle data;

    public CachedPersistentArray(/* necessary params */) {
        // Code goes here

    }

    /**
     * Returns an index that can be used for the next insert
     * @return tail-end byte index of all the space currently occupied
     * @throws IOException
     */
    @Override
    public long allocate() throws IOException {
        // set some initial returnValue
        //
        return 0; // the bytes of the wrappedPA + the size of the cache
    }

    @Override
    public void close() throws IOException {
        wrappedPA.close();
    }

    @Override
    public void delete( long index ) throws IOException {
        // if cache contains key
        if ( itemCache.containsKey(index) ) {
            // remove KV pair of index
        }

        wrappedPA.delete(index);
        
        /*
        Example implementation from BasePersistentArray:
            // allocate a ByteBuffer of record size
            // write the delete index to the ByteBuffer at position 0
            // put ByteBuffer at index
            // update deleteIndex to be index
            // persist metadata
         */
    }

    /**
     * Perform the fetching behavior of a CachedPersistentArray:
     *      if our desired index is in the cache, return it from
     *          the cache.
     *      otherwise, perform the fetch from the wrappedPA and
     *          store that retrieval in the <codec>itemCache</codec>
     * @param index the index of the bytes
     * @return ByteBuffer of the data requested
     * @throws IOException
     */
    @Override
    public ByteBuffer get( long index ) throws IOException {
        // if cache contains index (as key)
        //     get the E from the cache
        //     fill the ByteBuffer with property values
        //     return said buffer
        // else cache doesn't contain key
        //     perform wrappedPA.get(index)
        //     store said get into the cache

        // Example wrappedPA.get() behavior (from BaseBlockFile.java):
        //     new ByteBuffer of record size
        //     its contents will be the record at the index
        //     read for the record size into the ByteBuffer instance
        //     return said ByteBuffer instance

        return null;
    }

    @Override
    public void put( long index, ByteBuffer buffer ) throws IOException {
        // caching in done on get requests, so wrappPA behavior is it
        wrappedPA.put( index, buffer ) ;
    }

    @Override
    public long getRecordCount() {
        // return the wrappedPA.getRecordCount()
        return 0;
    }

    @Override
    public ByteBuffer getMetadata() throws IOException {
        /*
        ByteBuffer oldInstance = wrappedPA.getMetadata(),
          newByteBuffer = new ByteBuffer of size oldInstance PLUS cache MD

         */
        return null;
    }

    @Override
    public void persistMetadata() throws IOException {
        // reset the metadata ByteBuffer position
        // capture getMetaData (thusly delegate the heavy work to getMetadata())
        // overwrite the bytes in (metadata : ByteBuffer) field
    }
}
