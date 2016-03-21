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
	 * partially commits this transaction. This means writing &lt;T commit&gt; 
	 * in the recovery log, which implementers must do.
	 */
	public void commit() {
		TransactionManager.instance().unregister(this);
	}

	/**
	 * restarts the transaction
	 */
	public void restart() {
		super.restart();
		LockManager.instance().unlock(this,"hpq_hh");
		LockManager.instance().unlock(this,"hpq_crop");
		LockManager.instance().unlock(this,"hpq_alp");
	}

	/**
	 * rolls back the changes made by this transaction
	 */
	public void rollback() {
		setStatus(FINISHED);
	}
}