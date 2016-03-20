package mco3.model;

/**
 * This interface defines a transaction
 * @author Austin Fernandez
 */
public interface Transaction {
	public static final int NOT_STARTED = 0;
	public static final int RUNNING = 1;
	public static final int WAITING = 2;
	public static final int COMMITTED = 3;
	public static final int ROLLBACK = 4;
	public static final int FINISHED = 5;

	/**
	 * returns this transaction's id
	 * @return this transaction's id
	 */
	public int transactionId();

	/**
	 * begins the transaction. This method should assign a timestamp to this 
	 * transaction
	 */
	public void begin();

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
	public int status();

	/**
	 * sets the status of this transaction
	 * @param status status of this transaction
	 */
	public void setStatus(int status);

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