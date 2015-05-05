package persistent.collections;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ParityPersistentArray implements PersistentArray{

	private PersistentArray[] PAA;

	private long getHiddenIndex(long index){
		return (index/PAA.length)+index+1+(index/(PAA.length*(PAA.length-1)));
	}

	private long getPublicIndex(long index){
		int n = PAA.length;
		return index-(1+((index%(n*n)/(n+1)))) - index/(n*n)*n;
	}

	public ByteBuffer get(long index) throws IOException{
		return PAA[(int) (getHiddenIndex(index)%PAA.length)]
			.get(getHiddenIndex(index)/PAA.length);
	}
	
	

	public long allocate() throws IOException{
		// whichArray = Find PA with the lowest size
		// long index = Call allocate on that array
		// int outwardIndex = getPublicIndex(PAA.length*index+whichArray)
		// return outwardIndex
		int smallest = 0;
		for(int i = 0; i < PAA.length; i++){
			if(PAA[smallest].getRecordCount() > PAA[i].getRecordCount())
				smallest = i;
		}
		
		long index = PAA[smallest].allocate();
		long outwardIndex = getPublicIndex(PAA.length*index+smallest);
		return outwardIndex;
	}

	public void put(long index, ByteBuffer buffer) throws IOException{
		PAA[(int) (getHiddenIndex(index)%PAA.length)]
				.put(getHiddenIndex(index)/PAA.length, buffer);
	}

	public void delete(long index) throws IOException{
		PAA[(int) (getHiddenIndex(index)%PAA.length)]
				.delete(getHiddenIndex(index)/PAA.length);
	}

	@Override
	public void close() throws IOException{
		// Call close on all PAs
		for(int i = 0; i < PAA.length; i++){
			PAA[i].close();
		}
	}

	@Override
	public long getRecordCount(){
		// Return summation of sizes of all PA arrays
		long size = 0;
		for(int i = 0; i < PAA.length; i++){
			size += PAA[i].getRecordCount();
		}
		return size;
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
