package mco3.model;

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
		LockManager.instance().readLock(t,item);
	}

	/**
	 * returns the string representation of this read lock request
	 * @return the string representation of this read lock request
	 */
	public String toString() {
		return "rl(" + item + ")";
	}
}