package mco3.model;

import java.sql.Connection;
import java.sql.SQLException;

public class ReadDensity2 extends AbstractTransaction {
	public ReadDensity2(int id, IsoLevel isolation) throws SQLException {
		super(id,isolation);

		buildTransaction();
	}

	public ReadDensity2(int id, IsoLevel isolation, int abort) throws SQLException {
		super(id,isolation,abort);

		buildTransaction();
	}

	public void buildTransaction() {
		transaction.add(new Lock(this,new String[] {
			"hpq_alp",
			"hpq_hh"
		},new String[] {
			"READ",
			"READ"
		}));
		transaction.add(new ReadAction(null,"",null,new String[] {
			"hpq_hh",
			"hpq_alp"
		}));
		transaction.add(new ReadAction(null,"",null,new String[] {
			"hpq_hh",
			"hpq_alp"
		}));
		transaction.add(new CommitAction(this));
		transaction.add(new UnlockAction(this,"hpq_alp"));
		transaction.add(new UnlockAction(this,"hpq_hh"));
	}

	/**
	 * This method must release all locks held by this method
	 */
	public void releaseLocks() {
		super.releaseLocks();
		LockManager.instance().unlock(this,"hpq_hh");
		LockManager.instance().unlock(this,"hpq_alp");
	}
}