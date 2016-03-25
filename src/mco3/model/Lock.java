package mco3.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Command object that issues a write lock for the given transaction and item
 */
public class Lock implements DBAction {
	private Transaction t;
	private String[] items;
	private String[] modes;

	/**
	 * basic constructor
	 * @param t owning transaction
	 * @param item item to lock
	 */
	public Lock(Transaction t, String[] items, String[] modes) {
		this.t = t;
		this.items = items;
		this.modes = modes;
	}

	/**
	 * requests the write lock
	 */
	public void execute() {
		String lock = "LOCK TABLES ";
		for(int i = 0; i < items.length; i++) {
			if( i > 0 ) {
				lock += ",";
			}
			lock += items[i] + " " + modes[i];
			// if( modes[i].equals("WRITE")) {
				// System.out.println(t.transactionId() + " wl(" + item + ")");
				// LockManager.instance().writeLock(t,item);
			// } else if( modes[i].equals("READ")) {
				// System.out.println(t.transactionId() + " wl(" + item + ")");
				// LockManager.instance().readLock(t,item);
			// }
		}
		try {
			PreparedStatement ps 
				= t.getConnection().prepareStatement(lock);
			System.out.println(ps);
			ps.execute();
			ps.close();
		} catch( SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * returns the string representation of this write lock request
	 * @return the string representation of this write lock request
	 */
	public String toString() {
		String ret = "";
		for(int i = 0; i < items.length; i++) {
			if( i > 0 ) {
				ret += "<br>";
			}
			if( modes[i].equals("WRITE")) {
				ret += "wl(" + items[i] + ")";
			} else if( modes[i].equals("READ")) {
				ret += "rl(" + items[i] + ")";
			}
		}
		return ret;
	}
}