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

	public void lock(String tag,String stmt) {
		DummyTransaction dt = dummyMap.get(tag);
		if( dt != null  ) {
			dt.lock(stmt);
		}
	}

	public void unlock(String tag) {
		DummyTransaction dt = dummyMap.get(tag);
		if( dt != null  ) {
			dt.releaseLocks();
		}	
	}
}