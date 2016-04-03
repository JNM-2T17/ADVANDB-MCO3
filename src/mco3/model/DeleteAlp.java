package mco3.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class DeleteAlp extends AbstractTransaction {
	public DeleteAlp(int id, IsoLevel isolation,int alpid) throws SQLException {
		super(id,isolation);

		buildTransaction(alpid);
	}

	public DeleteAlp(int id, IsoLevel isolation,int abort,int alpid) throws SQLException {
		super(id,isolation,abort);

		buildTransaction(alpid);
	}

	private void buildTransaction(int alpid) {
		transaction.add(new Lock(this,new String[] {
			"hpq_alp"
		},new String[] {
			"WRITE"
		}));
		transaction.add(new WriteAction(this,"DELETE FROM hpq_alp WHERE " 
											+ "hpq_hh_id = ?",new String[] {
											alpid + ""
										},"hpq_alp"));
		transaction.add(new CommitAction(this));
		transaction.add(new UnlockAction(this,"hpq_alp"));
	}

	/**
	 * This method must release all locks held by this method
	 */
	public void releaseLocks() {
		super.releaseLocks();
		LockManager.instance().unlock(this,"hpq_alp");
	}

	public String toString() {
		return "Delete Land Transaction";
	}
}