package mco3.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class EditAlp extends AbstractTransaction {
	public EditAlp(int id, IsoLevel isolation) throws SQLException {
		super(id,isolation);

		buildTransaction();
	}

	public EditAlp(int id, IsoLevel isolation,int abort) throws SQLException {
		super(id,isolation,abort);

		buildTransaction();
	}

	private void buildTransaction() {
		transaction.add(new Lock(this,new String[] {
			"hpq_alp"
		},new String[] {
			"WRITE"
		}));
		transaction.add(new ReadAction(con,"",null,new String[] {
			"hpq_alp"
		}));
		transaction.add(new WriteAction(this,"UPDATE hpq_alp SET alp_area = alp_area + 10 WHERE hpq_hh_id = 11328",null,"val=1000"
										,"val=2000","hpq_alp"));
		transaction.add(new CommitAction(this));
		transaction.add(new UnlockAction(this,"hpq_alp"));
		transaction.add(new UnlockAction(this,"hpq_crop"));
		transaction.add(new UnlockAction(this,"hpq_hh"));
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