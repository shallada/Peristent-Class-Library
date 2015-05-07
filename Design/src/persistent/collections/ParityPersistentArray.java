package persistent.collections;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.naming.OperationNotSupportedException;

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
		

		long row = PAA[smallest].allocate();
		
		if(row%PAA.length == smallest){
			return allocate();
		}
		
		calculateParityForRow(row);
		
		long outwardIndex = getPublicIndex(PAA.length*row+smallest);
		return outwardIndex;
	}
	
	public void calculateParityForRow(long row) throws IOException{
		ByteBuffer parity = null;
		ByteBuffer xor = null;
		for(int i = 0; i < PAA.length; i++){
			if(row%PAA.length == i){
				// This is the parity
				try{
					parity = PAA[i].get(row);			
				} catch (Exception e){
					PAA[i].allocate();
					parity = PAA[i].get(row);
				}
			} else {
				try{
					ByteBuffer next = PAA[i].get(row);
					if(xor == null){
						xor = ByteBuffer.allocate(next.capacity());
					} else {
						xor.flip();
						ByteBuffer n = ByteBuffer.allocate(xor.capacity());
						for(byte b = next.get(); next.hasRemaining(); b = next.get()){
							n.put((byte) (b ^ xor.get()));
						}
						xor = n;
					}
				} catch (Exception e){
					PAA[i].allocate();
					parity = PAA[i].get(row);
				}
			}
		}
	}


	public void put(long index, ByteBuffer buffer) throws IOException{
		// PAA[getHiddenIndex(index)%PAA.length]
		//	.put(getHiddenIndex(index)/PAA.length, buffer);
		PAA[(int) (getHiddenIndex(index)%PAA.length)]
				.put(getHiddenIndex(index)/PAA.length, buffer);
		calculateParityForRow(index/PAA.length);
	}

	public void delete(long index) throws IOException{
		throw new UnsupportedOperationException("No deletions are allowed.");
//		PAA[(int) (getHiddenIndex(index)%PAA.length)]
//				.delete(getHiddenIndex(index)/PAA.length);
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
