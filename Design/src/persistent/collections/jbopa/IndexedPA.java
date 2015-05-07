package persistent.collections.jbopa;

import persistent.collections.PersistentArray;


public class IndexedPA{
	
	private int order;
	private long maxRecordCount;
	private PersistentArray PA;
	
	public IndexedPA(PersistentArray PA, long maxRecordCount){
		this.PA = PA;
		this.maxRecordCount = maxRecordCount;
	}

	public IndexedPA(PersistentArray PA, long maxRecordCount, int order) {
		// TODO Auto-generated constructor stub
		this.PA = PA;
		this.maxRecordCount = maxRecordCount;
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	public long getMaxRecordCount() {
		return maxRecordCount;
	}

	public PersistentArray getPA() {
		return PA;
	}
	
	
}
