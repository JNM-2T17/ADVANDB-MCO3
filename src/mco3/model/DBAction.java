package mco3.model;

public interface DBAction {
	public void execute();
	public void wakeUp(boolean ok);
}