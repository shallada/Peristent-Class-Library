import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


public class MirroredPersistentArray implements PersistentArray 
{
	private List<PersistentArray> arrays;
	private List<PersistentArray> outOfSyncArrays;
	private int nextReadIndex = 0;
	private long mdSize = 0;
	
	public MirroredPersistentArray(PersistentArray[] inputArrays)
	{
		arrays = new ArrayList<PersistentArray>();
		for(PersistentArray p : inputArrays)
		{
			arrays.add(p);
		}
		checkIntegrity();
	}
	
	private void checkIntegrity()
	{		
		HashMap<Long, ArrayList<PersistentArray>> dictionary = new HashMap<Long, ArrayList<PersistentArray>>();
		//Map record counts to lists of PersistentArrays
		for(PersistentArray p : arrays)
		{
			long recordCount = p.getRecordCount();
			ArrayList<PersistentArray> list = dictionary.get(recordCount);
			if(list == null)
			{				
				list = new ArrayList<PersistentArray>();
			}
			list.add(p);
			dictionary.put(recordCount, list);
		}
		//Determine the most common record count (assumed to be the most current)
		long highestKey = 0;
		long highestCount = 0;
		for (Entry<Long, ArrayList<PersistentArray>> entry : dictionary.entrySet()) 
		{
			if(entry.getValue().size() > highestCount)
			{
				highestKey = entry.getKey();
			}
		}
		//Flag all PersistentArrays that have a less common count as 'out of sync'
		for (Entry<Long, ArrayList<PersistentArray>> entry : dictionary.entrySet()) 
		{
			if(entry.getKey() != highestKey)
			{
				for(PersistentArray pa : entry.getValue())
				{
					flagOutOfSync(pa);
				}
			}
		}
	}
	
	private void flagOutOfSync(int index)
	{
		PersistentArray pa = arrays.remove(index);
		outOfSyncArrays.add(pa);
		increment();
	}
	
	private void flagOutOfSync(PersistentArray pa)
	{
		for(int i = 0; i < arrays.size(); i++)
		{
			if(arrays.get(i).equals(pa))
			{
				flagOutOfSync(i);
				break;
			}
		}
	}
	
	private void increment()
	{
		nextReadIndex++;
		if(nextReadIndex >= arrays.size())
		{
			nextReadIndex = 0;
		}
	}
	
	@Override
	public long allocate() throws IOException 
	{
		long index = 0;
		boolean successfulCapture = false;
		for(PersistentArray pa : arrays)
		{
			try
			{
				index = pa.allocate();
				successfulCapture = true;
			}
			catch(Exception e)
			{
				flagOutOfSync(pa);
			}
		}
		
		if(!successfulCapture)//Complete system failure has occurred
		{
			throw new IOException();
		}
		return index;
	}

	@Override
	public void close() throws IOException 
	{
		persistMetadata();
		for(PersistentArray pa : arrays)
		{
			pa.close();
		}
	}

	@Override
	public void delete(long index) throws IOException 
	{
		for(PersistentArray pa : arrays)
		{
			try
			{
				pa.delete(index);
			}
			catch(Exception e)
			{
				flagOutOfSync(pa);
			}
		}
	}

	@Override
	public ByteBuffer get(long index) throws IOException 
	{
		ByteBuffer buffer = null;		
		boolean findingReturn = true;
		
		while(findingReturn && arrays.size()>0)
		{
			try
			{
				buffer = arrays.get(nextReadIndex).get(index);
				findingReturn = false;
			}
			catch(Exception e2)
			{
				flagOutOfSync(nextReadIndex);
			}
			increment();
		}

		return buffer;
	}

	@Override
	public void put(long index, ByteBuffer buffer) throws IOException 
	{
		for(PersistentArray pa : arrays)
		{
			try
			{
				pa.put(index, buffer);
			}
			catch(Exception e)
			{
				flagOutOfSync(pa);
			}
		}
	}

	@Override
	public long getRecordCount() 
	{
		long recordCount = -1;
		boolean retrieving = true;
		while(retrieving && arrays.size()>0)
		{
			try
			{
				recordCount = arrays.get(nextReadIndex).getRecordCount();
				retrieving = false;
				increment();
			}
			catch(Exception e)
			{
				flagOutOfSync(nextReadIndex);
			}
		}

		if(recordCount < 1)
		{
			recordCount = 0;
			//Blow up instead?
		}
		return recordCount;
	}

	@Override
	public ByteBuffer getMetadata() throws IOException 
	{
		ByteBuffer buffer = null;
		boolean retrieving = true;
		while(retrieving && arrays.size()>0)
		{
			try
			{
				buffer = arrays.get(nextReadIndex).getMetadata();
				retrieving = false;
				increment();
			}
			catch(Exception e)
			{
				flagOutOfSync(nextReadIndex);
			}
		}
		return buffer;
	}

	@Override
	public void persistMetadata() throws IOException 
	{
		//nothing to persist
	}
	
	public long getMdSize()
	{
		return mdSize;
	}
}