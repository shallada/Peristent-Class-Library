package persistent.collections.jbopa;
import java.io.IOException;
import java.nio.ByteBuffer;

import persistent.collections.PersistentArray;


public class JBOPA implements PersistentArray{

	IndexedPA[] PAs;
	
	/**
	*	Takes in a variable number of PersistentArrays that are wrapped by the IndexedPA
	*	class. The IndexedPA needs a maximum number of records a PersistentArray will be allowed to store.
	*	The PersistentArrays need to be wrapped because our PersistentArray has no notion of max file size, which is something
	*	that the JBOPA needs in order to be effective.
	 * @throws IOException 
	*/
	public static void Create(IndexedPA... PersistentArrays) throws IOException{
			
		long maxIndex = 0;
		for(int i = 0; i < PersistentArrays.length; i++){
			ByteBuffer buffer = PersistentArrays[i].getPA().getMetadata();
			buffer.putInt(i);
			buffer.putLong(PersistentArrays[i].getMaxRecordCount());
			PersistentArrays[i].getPA().persistMetadata();
		}
	}
	
	/**
	*	Takes in a variable number of Persistent arrays and tries to create a JBOPA from them. 
	*	I have stored an order variable so that even if the arrays come in out of order, as long as 
	*	all the arrays are here, I can put them in order.
	 * @throws DuplicateArguementException 
	 * @throws IOException 
	*/
	public static JBOPA Open(PersistentArray... PersistentArrays) throws DuplicateArguementException, IOException{
		JBOPA jbop = new JBOPA();
		
		jbop.PAs = new IndexedPA[PersistentArrays.length];
		
		try{
			for(int i = 0; i < PersistentArrays.length; i++){
				ByteBuffer buffer = PersistentArrays[i].getMetadata();
				int index = buffer.getInt();
				long size = buffer.getLong();
				if(jbop.PAs[index] != null){
					throw new DuplicateArguementException("Received two arrays with the same order.");
				}
				jbop.PAs[index] = new IndexedPA(PersistentArrays[i], size, index);
			}
		} catch(IndexOutOfBoundsException ex){
			throw new IllegalArgumentException("The arrays passed in didn't fit the existing JBOPA");
		}
		
		return jbop;
	}
	
	public static int getMdSize(){
		return Integer.BYTES + Long.BYTES;
	}
	
	/**
	 * allocates a record in the next available persistent array
	 * Throws 
	 * @return index in JBOPA
	 */
	@Override
	public long allocate() throws IOException {
		//TODO: Adjust index to be JBOPA's index instead of PAs index
		long indexOffset = 0;
		long index = -1;
		for(int i = 0; i < PAs.length; i++){
			if(PAs[i].getPA().getRecordCount() < PAs[i].getMaxRecordCount()){
				index = PAs[i].getPA().allocate();
				break;
			}
			indexOffset += PAs[i].getMaxRecordCount();
		}
		if(index < 0){
			throw new OutOfStorageException("Persistent Arrays are full");
		}
		return index + indexOffset;
	}

	/**
	 * Closes each contained PersistentArray
	 */
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		for(IndexedPA pa : PAs){
			pa.getPA().close();
		}
	}

	/**
	 * Finds the PersistentArray associated with the index and calls delete.
	 */
	@Override
	public void delete(long index) throws IOException {
		IndexAndPA PAwithIndex = JBOPAoffset(index);
		PAwithIndex.pa.delete(PAwithIndex.index);
	}

	/**
	 * Finds the PersistentArray associated with the index and returns the ByteBuffer at the index.
	 */
	@Override
	public ByteBuffer get(long index) throws IOException {
		IndexAndPA PAwithIndex = JBOPAoffset(index);
		return PAwithIndex.pa.get(PAwithIndex.index);
	}

	/**
	 * Finds the PersistentArray associated with the index and puts the passed in ByteBuffer into that index.
	 */
	@Override
	public void put(long index, ByteBuffer buffer) throws IOException {
		IndexAndPA PAwithIndex = JBOPAoffset(index);
		PAwithIndex.pa.put(PAwithIndex.index, buffer);
	}

	/**
	 * Counts the number of records in each contained PersistentArray
	 */
	@Override
	public long getRecordCount() {
		long recordCount = 0;
		for(IndexedPA pa : PAs){
			recordCount += pa.getPA().getRecordCount();
		}
		return recordCount;
	}

	/**
	 * returns the metadata that the containing object is interested in. It gets stored in the first array.
	 */
	@Override
	public ByteBuffer getMetadata() throws IOException {
		return PAs[0].getPA().getMetadata();

	}

	/**
	 * Calls peristMetadata on the first persistent array.
	 */
	@Override
	public void persistMetadata() throws IOException {
		PAs[0].getPA().persistMetadata();
	}

	/**
	 * Returns an object containing a Persistent array with an index that targets the PersistentArray.
//	 * Works to convert the larger JBOPA indexes into the individual PersistentArray indexes
	 * @return
	 */
	private IndexAndPA JBOPAoffset(long startIndex){
		long IndexOffset = 0;
		long PAIndex = -1;
		PersistentArray selectedPA = null;
		
		for(int i = 0; i <= PAs.length; i++){
			if(startIndex < PAs[i].getMaxRecordCount() + IndexOffset){
				PAIndex = startIndex - IndexOffset;
				selectedPA = PAs[i].getPA();
				break;
			}
			IndexOffset += PAs[i].getMaxRecordCount();
		}
		if(PAIndex == -1){
			throw new IndexOutOfBoundsException();
		}
		return new IndexAndPA(PAIndex, selectedPA);
	}
	
	/**
	 * A small object that holds a PersistentArray and a long index.
	 * @author David Borland
	 *
	 */
	private class IndexAndPA{
		long index;
		PersistentArray pa;
		
		public IndexAndPA(long index, PersistentArray PA){
			this.index = index;
			this.pa = PA;
		}
	}
	
	
}
