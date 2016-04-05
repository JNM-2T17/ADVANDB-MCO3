package mco3.model;

import java.util.HashMap;

public class DummyManager {
	private HashMap<String,DummyTransaction> dummyMap;

	public DummyManager() {
		dummyMap = new HashMap<String,DummyTransaction>();
	}

	public void add(String tag, String id,String isoLevel) {
		int idNo = Integer.parseInt(id);
		IsoLevel level = null;
		switch(isoLevel) {
			case "1":
				level = IsoLevel.READ_UNCOMMITTED;
				break;
			case "2":
				level = IsoLevel.READ_COMMITTED;
				break;
			case "3":
				level = IsoLevel.READ_REPEATABLE;
				break;
			case "4":
				level = IsoLevel.SERIALIZABLE;
				break;
			default:
		}
		try {
			DummyTransaction dt = new DummyTransaction(idNo,level);
			System.out.println("New Dummy " + idNo + " " + level);
			dummyMap.put(tag,dt);
		} catch( Exception e) {
			e.printStackTrace();
		}
	}

	public void unregister(String tag) {
		String[] temp = dummyMap.keySet().toArray(new String[0]);

		for( String s : temp ) {
			if( s.endsWith(tag) ) {
				dummyMap.get(s).rollback();
				dummyMap.remove(s);
			}
		}
	}

	public boolean lock(String tag,String stmt) {
		DummyTransaction dt = dummyMap.get(tag);
		if( dt != null  ) {
			dt.lock(stmt);
			return true;
		} else {
			return false;
		}
	}

	public boolean check(String tag) {
		return dummyMap.get(tag) != null;
	}

	public void unlock(String tag) {
		DummyTransaction dt = dummyMap.get(tag);
		if( dt != null  ) {
			dt.releaseLocks();
		}	
	}

	public synchronized void write(String tag,String[] query) {
		DummyTransaction dt = dummyMap.get(tag);
		if( dt != null  ) {
			System.out.println(tag + " IS WRITING " + query[0]);
			dt.write(query);
		}	
	}

	public ReadResult read(String tag, String query, String[] cols, String[] params) {
		DummyTransaction dt = dummyMap.get(tag);
		if( dt != null  ) {
			return dt.read(query,cols,params);
		} else {
			return null;
		}
	}

	public synchronized void commit(String tag) {
		DummyTransaction dt = dummyMap.get(tag);
		if( dt != null  ) {
			dt.commit();
			dummyMap.remove(tag);
		}	
	}

	public synchronized void abort(String tag) {
		DummyTransaction dt = dummyMap.get(tag);
		if( dt != null  ) {
			System.out.println(tag + " IS ABORTING");
			dt.rollback();
			dummyMap.remove(tag);
		}	
	}
}