package mco3.model;

/**
 * this action begins a transaction
 */
public class BeginAction implements DBAction {
	private Transaction t;

	/**
	 * basic constructor
	 * @param t transaction to begin
	 */
	public BeginAction(Transaction t) {
		this.t = t;
	}

	/**
	 * begins the transaction
	 */
	public void execute() {
		t.begin();
	}

	public String toString() {
		return "BEGIN TRAN";
	}
}