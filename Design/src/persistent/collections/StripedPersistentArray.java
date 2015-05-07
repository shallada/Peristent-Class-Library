package persistent.collections;

import java.io.IOException;
import java.nio.ByteBuffer;


public class StripedPersistentArray implements PersistentArray{

	private PersistentArray[] PAA;

	public StripedPersistentArray(PersistentArray[] PAA){
		if(PAA.length < 2)
			throw new IllegalArgumentException("Persistent Array array must have a size of 2 or greater.");
		this.PAA = PAA;
	}

	@Override
	public long allocate() throws IOException{
		// Get current
			// whichArray = Find PA with lowest count
		// long spot = PA.allocate();
		// return spot*PAA.length + whichArray
		int which = 0;
		for(int i = 0; i < PAA.length; i++){
			if(PAA[which].getRecordCount() > PAA[i].getRecordCount())
				which = i;
		}
		long spot = PAA[which].allocate();
		return spot*PAA.length + which;
	}

	@Override
	public void put(long index, ByteBuffer buffer) throws IOException{
		PAA[(int) (index%PAA.length)].put(index/PAA.length, buffer);	
	}
	
	@Override
	public void delete(long index) throws IOException{
		PAA[(int) (index%PAA.length)].delete(index/PAA.length);
	}

	public void close() throws IOException{
		for(PersistentArray pa : PAA){
			pa.close();
		}
	}

	@Override
	public long getRecordCount(){
		// return size of all subarrays
		long count = 0;
		for(PersistentArray pa : PAA){
			count += pa.getRecordCount();
		}
		return count;
	}

	@Override
	public ByteBuffer get(long index) throws IOException {
		return PAA[(int) (index%PAA.length)].get(index/PAA.length);
	}

	@Override
	public ByteBuffer getMetadata() throws IOException {
		return PAA[0].getMetadata();
	}

	@Override
	public void persistMetadata() throws IOException {
		PAA[0].persistMetadata();
	}
}
