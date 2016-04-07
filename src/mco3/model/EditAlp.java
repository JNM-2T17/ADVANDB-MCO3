package mco3.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class EditAlp extends AbstractTransaction {
	public EditAlp(int id, IsoLevel isolation,int alpid,int increment) throws SQLException {
		super(id,isolation);

		buildTransaction(alpid,increment);
	}

	public EditAlp(int id, IsoLevel isolation,int abort,int alpid,int increment) throws SQLException {
		super(id,isolation,abort);

		buildTransaction(alpid,increment);
	}

	private void buildTransaction(int alpid,int increment) {
		transaction.add(new Lock(this,new String[] {
			"hpq_alp"
		},new String[] {
			"WRITE"
		}));
		if( abortStatus() != AbstractTransaction.ABORT_AFTER ) {
			transaction.add(new WriteAction(this,"UPDATE hpq_alp SET alp_area = " 
							+ "alp_area + ? WHERE hpq_hh_id = ?",new String[] {
				increment + "", alpid + ""
			},"hpq_alp"));
		}
		transaction.add(new CommitAction(this));
		transaction.add(new UnlockAction(this,"hpq_alp"));
	}

	/**
	 * This method must release all locks held by this method
	 */
	public void releaseLocks() {
		super.releaseLocks();
		LockManager.instance().unlock(this,"hpq_hh");
		LockManager.instance().unlock(this,"hpq_crop");
		LockManager.instance().unlock(this,"hpq_alp");
	}

	public String toString() {
		return "Edit Land Transaction";
	}
}