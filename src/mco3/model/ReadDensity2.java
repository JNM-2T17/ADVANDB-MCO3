package mco3.model;

public class ReadDensity2 extends AbstractTransaction {
	public ReadDensity2(int id, IsoLevel isolation) {
		super(id);

		if( isolation.level() > IsoLevel.READ_UNCOMMITTED.level() ) {
			transaction.add(new ReadLock(this,"hpq_hh"));
			transaction.add(new ReadLock(this,"hpq_crop"));
			transaction.add(new ReadLock(this,"hpq_alp"));
		}
		transaction.add(new ReadAction(null,"",null,new String[] {
			// "hpq_hh",
			// "hpq_crop",
			"hpq_alp"
		}));
		if( isolation == IsoLevel.READ_COMMITTED ) {
			transaction.add(new UnlockAction(this,"hpq_hh"));
			transaction.add(new UnlockAction(this,"hpq_crop"));
			transaction.add(new UnlockAction(this,"hpq_alp"));
			transaction.add(new ReadLock(this,"hpq_hh"));
			transaction.add(new ReadLock(this,"hpq_crop"));
			transaction.add(new ReadLock(this,"hpq_alp"));
		}
		transaction.add(new ReadAction(null,"",null,new String[] {
			// "hpq_hh",
			// "hpq_crop",
			"hpq_alp"
		}));
		if( isolation.level() > IsoLevel.READ_UNCOMMITTED.level() ) {
			transaction.add(new UnlockAction(this,"hpq_hh"));
			transaction.add(new UnlockAction(this,"hpq_crop"));
			transaction.add(new UnlockAction(this,"hpq_alp"));
		}
		transaction.add(new CommitAction(this));
	}

	/**
	 * This method must release all locks held by this method
	 */
	public void releaseLocks() {
		LockManager.instance().unlock(this,"hpq_hh");
		LockManager.instance().unlock(this,"hpq_crop");
		LockManager.instance().unlock(this,"hpq_alp");
	}
}