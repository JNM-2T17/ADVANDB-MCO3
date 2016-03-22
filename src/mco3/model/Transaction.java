package mco3.model;

import mco3.view.Updatable;

/**
 * This interface defines a transaction
 * @author Austin Fernandez
 */
public interface Transaction extends Runnable {
	public static final String NOT_STARTED = "Pending";
	public static final String RUNNING = "Running";
	public static final String WAITING = "Waiting";
	public static final String COMMIT = "Commit";
	public static final String ROLLBACK = "Rollback";
	public static final String FINISHED = "Finish";

	/**
	 * returns this transaction's id
	 * @return this transaction's id
	 */
	public int transactionId();

	/**
	 * sets the view that displays this transaction
	 * @param view view to update
	 */
	public void setView(Updatable view);

	/**
	 * begins the transaction. This method should assign a timestamp to this 
	 * transaction
	 */
	public void begin();

	/**
	 * returns number of steps in this transaction
	 * @return number of steps in this transaction
	 */
	public int size();

	/**
	 * This method must release all locks held by this method
	 */
	public void releaseLocks();

	/**
	 * sets this transaction's timestamp
	 * @param timestamp this transaction's timestamp
	 */
	public void setTimestamp(int timestamp);

	/**
	 * returns the step at the given index
	 * @param index index of step
	 * @return step at index
	 */
	public DBAction getStep(int index);

	/**
	 * executes the next action in this transaction
	 */
	public void step();

	/**
	 * returns the current status of this transaction
	 * @return current status of this transaction
	 */
	public String status();

	/**
	 * sets the status of this transaction
	 * @param status status of this transaction
	 */
	public void setStatus(String status);

	/**
	 * commits this transaction. This means writing &lt;T commit&gt; 
	 * in the recovery log, which implementers must do.
	 */
	public void commit();

	/**
	 * returns whether this transaction is about to choose to commit or rollback
	 * @return whether this transaction is about to choose to commit or rollback
	 */
	public boolean isCommitting();

	/**
	 * returns current position in the transaction's steps
	 * @return current position in the transaction's steps
	 */
	public int position();

	/**
	 * restarts the transaction. Implementers must release all locks.
	 */
	public void restart();

	/**
	 * undoes all changes done by this transaction so far
	 */
	public void undoChanges();

	/**
	 * gets this transaction's timestamp
	 * @return transaction timestamp
	 */
	public int timestamp();

	/**
	 * rolls back the changes made by this transaction
	 */
	public void rollback();

	/**
	 * returns whether this transaction is finished
	 * @return whether this transaction is finished
	 */
	public boolean isFinished();
}