package mco3.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import mco3.controller.MCO3Controller;

/**
 * Command object that unlocks an item for the given transaction
 */
public class UnlockAction implements DBAction {
	private Transaction t;
	private String item;

	/**
	 * basic constructor
	 * @param t owning transaction
	 * @param item item to lock
	 */
	public UnlockAction(Transaction t, String item) {
		this.t = t;
		this.item = item;
	}

	/**
	 * requests the read lock
	 */
	public void execute() {
		// LockManager.instance().unlock(t,item);
		ConnectionManager cm = ConnectionManager.instance();

		t.releaseLocks();
	}

	/**
	 * returns the string representation of this read lock request
	 * @return the string representation of this read lock request
	 */
	public String toString() {
		return "u(" + item + ")";
	}

	public synchronized void wakeUp(boolean status) {
		notifyAll();
	}
}