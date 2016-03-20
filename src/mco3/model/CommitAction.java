package mco3.model;

/**
 * this action commits a transaction
 */
public class CommitAction implements DBAction {
	private Transaction t;

	/**
	 * basic constructor
	 * @param t transaction to commit
	*/
	public CommitAction(Transaction t) {
		this.t = t;
	}

	/**
	 * commits the transaction
	 */
	public void execute() {
		LogManager.instance().writeCommit(t);
		t.commit();
	}

	public String toString() {
		return "COMMIT TRAN";
	}
}