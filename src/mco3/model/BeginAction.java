package mco3.model;

public class BeginAction implements DBAction {
	private Transaction t;

	public BeginAction(Transaction t) {
		this.t = t;
	}

	public void execute() {
		LogManager.instance().writeStart(t);
		t.begin();
	}

	public String toString() {
		return "BEGIN TRAN";
	}
}