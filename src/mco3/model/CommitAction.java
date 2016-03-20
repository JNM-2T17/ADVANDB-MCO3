package mco3.model;

public class CommitAction implements DBAction {
	private Transaction t;

	public CommitAction(Transaction t) {
		this.t = t;
	}

	public void execute() {
		LogManager.instance().writeCommit(t);
		t.commit();
	}

	public String toString() {
		return "COMMIT TRAN";
	}
}