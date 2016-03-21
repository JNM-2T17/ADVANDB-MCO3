package mco3.model;

public class EditAlp extends AbstractTransaction {
	public EditAlp(int id, IsoLevel isolation) {
		super(id);

		transaction.add(new WriteLock(this,"hpq_alp"));
		if( isolation.level() > IsoLevel.READ_UNCOMMITTED.level() ) {
			transaction.add(new ReadLock(this,"hpq_hh"));
			transaction.add(new ReadLock(this,"hpq_crop"));
		}
		transaction.add(new ReadAction(null,"",null,new String[] {
			// "hpq_hh",
			// "hpq_crop",
			"hpq_alp"
		}));
		if( isolation.level() > IsoLevel.READ_UNCOMMITTED.level() ) {
			transaction.add(new UnlockAction(this,"hpq_hh"));
			transaction.add(new UnlockAction(this,"hpq_crop"));
		}
		
		transaction.add(new WriteAction(this,null,"",null,"val=1000"
										,"val=2000","hpq_alp"));
		transaction.add(new CommitAction(this));
		transaction.add(new UnlockAction(this,"hpq_alp"));
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