package mco3.model;

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
		LockManager.instance().writeLock(t,item);
	}

	/**
	 * returns the string representation of this write lock request
	 * @return the string representation of this write lock request
	 */
	public String toString() {
		return "wl(" + item + ")";
	}
}