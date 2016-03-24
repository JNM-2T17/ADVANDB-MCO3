package mco3.model;

import java.sql.Statement;
import java.sql.SQLException;

/**
 * Command object that issues a write lock for the given transaction and item
 */
public class WriteLock implements DBAction {
	private Transaction t;
	private String item;

	/**
	 * basic constructor
	 * @param t owning transaction
	 * @param item item to lock
	 */
	public WriteLock(Transaction t, String item) {
		this.t = t;
		this.item = item;
	}

	/**
	 * requests the write lock
	 */
	public void execute() {
		// System.out.println(t.transactionId() + " wl(" + item + ")");
		// LockManager.instance().writeLock(t,item);
		try {
			Statement s = t.getConnection().createStatement();
			s.executeUpdate("LOCK TABLE " + item + " WRITE");
			s.close();
		} catch( SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * returns the string representation of this write lock request
	 * @return the string representation of this write lock request
	 */
	public String toString() {
		return "wl(" + item + ")";
	}
}