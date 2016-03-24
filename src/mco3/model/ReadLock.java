package mco3.model;

import java.sql.Statement;
import java.sql.SQLException;

/**
 * Command object that issues a read lock for the given transaction and item
 */
public class ReadLock implements DBAction {
	private Transaction t;
	private String item;

	/**
	 * basic constructor
	 * @param t owning transaction
	 * @param item item to lock
	 */
	public ReadLock(Transaction t, String item) {
		this.t = t;
		this.item = item;
	}

	/**
	 * requests the read lock
	 */
	public void execute() {
		// System.out.println(t.transactionId() + " rl(" + item + ")");
		// LockManager.instance().readLock(t,item);
		try {
			Statement s = t.getConnection().createStatement();
			s.executeUpdate("LOCK TABLE " + item + " READ");
			s.close();
		} catch( SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * returns the string representation of this read lock request
	 * @return the string representation of this read lock request
	 */
	public String toString() {
		return "rl(" + item + ")";
	}
}