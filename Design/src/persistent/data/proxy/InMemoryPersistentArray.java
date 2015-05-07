package persistent.data.proxy;
 
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
 
public class InMemoryPersistentArray implements persistent.collections.PersistentArray{
       private ByteBuffer metadata;
       private List<ByteBuffer> persistentArray = new ArrayList<ByteBuffer>();
       private int recordSize;
      
       public InMemoryPersistentArray(String path, int recordSize, int metadataSize) {
              this.recordSize = recordSize;
              metadata = ByteBuffer.allocate(metadataSize);
       }
      
       @Override
       public long allocate() throws IOException {
              int index = persistentArray.size();
              persistentArray.add(index, null);
              return index;
       }
 
       @Override
       public void close() throws IOException {
              // TODO Auto-generated method stub
             
       }
 
       @Override
       public void delete(long index) throws IOException {
             
       }
 
       @Override
       public ByteBuffer get(long index) throws IOException {
              return persistentArray.get((int) index);
       }
 
       @Override
       public void put(long index, ByteBuffer buffer) throws IOException {
              if(buffer.limit() != recordSize) {
                     throw new RuntimeException();
              }
              persistentArray.set((int)index, buffer);
       }
 
       @Override
       public long getRecordCount() {
              return persistentArray.size();
       }
 
       @Override
       public ByteBuffer getMetadata() throws IOException {
              return metadata;
       }
 
       @Override
       public void persistMetadata() throws IOException {
             
             
       }
 
}