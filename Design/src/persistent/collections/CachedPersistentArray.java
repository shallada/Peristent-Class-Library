package persistent.collections;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Implementation of the PersistentArray with in-memory caching of elements.
 * Most heavy work is done by the cache. This implementation tries to maintain
 * original functionality of given <code>PersistentArray</code> and hasten
 * R/W with an appropriately sized cache of values
 * Created by stephen on 4/27/15.
 */
public class CachedPersistentArray<E> implements PersistentArray{

    private PersistentArray wrappedPA;
    private SizedLinkedList<E> itemCache;
    // long nextIndex;
    // int recordSize;
    // long recordCount
    private ByteBuffer instanceMetadata;

    public CachedPersistentArray(int cacheLimit, PersistentArray wrappablePA) {
        // Code goes here
        this.wrappedPA = wrappablePA;
        this.itemCache = new SizedLinkedList<E>(cacheLimit);
    }

    /**
     * Returns an index that can be used for the next insert
     * @return tail-end byte index of all the space currently occupied
     * @throws IOException
     */
    @Override
    public long allocate() throws IOException {
        return wrappedPA.allocate() + itemCache.size(); // the bytes of the wrappedPA + the size of the cache
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
            itemCache.remove(Long.valueOf(index).intValue());
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
        ByteBuffer bufferForYou;
        // if cache contains index (as key)
        //     get the E from the cache
        //     fill the ByteBuffer with property values
        //     return said buffer
        // else cache doesn't contain key
        //     perform wrappedPA.get(index)
        //     store said get into the cache

        if (itemCache.containsKey(index)) {
            E e = itemCache.get((int) index);
            bufferForYou = getMetadata();
        }
        else {
            wrappedPA.get(index);
            // make an E out of it... FIXME: Why doesn't this make sense?
            bufferForYou = wrappedPA.getMetadata();
        }

        // Example wrappedPA.get() behavior (from BaseBlockFile.java):
        //     new ByteBuffer of record size
        //     its contents will be the record at the index
        //     read for the record size into the ByteBuffer instance
        //     return said ByteBuffer instance

        return bufferForYou;
    }

    @Override
    public void put( long index, ByteBuffer buffer ) throws IOException {
        // caching in done on get requests, so wrappedPA behavior is it
        wrappedPA.put( index, buffer ) ;
    }

    @Override
    public long getRecordCount() {
        // return the wrappedPA.getRecordCount()
        return wrappedPA.getRecordCount();
    }

    @Override
    public ByteBuffer getMetadata() throws IOException {
        /*
        ByteBuffer oldInstance = wrappedPA.getMetadata(),
          newByteBuffer = new ByteBuffer of size oldInstance PLUS cache MD

         */
        ByteBuffer old = wrappedPA.getMetadata();
        ByteBuffer allocate = ByteBuffer.allocate(old.capacity() + ( Long.BYTES * itemCache.size() ))
                .put(old);

        itemCache.getIndexSet().forEach(allocate::putLong);

        return allocate;
    }

    @Override
    public void persistMetadata() throws IOException {
        // reset the metadata ByteBuffer position
        ByteBuffer metadata = (ByteBuffer) wrappedPA.getMetadata().position(0);
        // Behavior should be that of wrappedPA, because Cache will be kept in memory
        wrappedPA.persistMetadata();
    }
}
