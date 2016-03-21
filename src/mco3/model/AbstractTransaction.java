package mco3.model;

import java.util.ArrayList;

/**
 * This class provides a generic implementation for the transaction interface
 * @author Austin Fernandez
 */
public abstract class AbstractTransaction implements Transaction {
	private int transactionId;
	private String status;
	private int timestamp;
	private int position;
	protected ArrayList<DBAction> transaction;

	/**
	 * basic constructor
	 * @param id transaction id
	 */
	public AbstractTransaction(int id) {
		transactionId = id;
		position = 0;
		status = NOT_STARTED;
		transaction = new ArrayList<DBAction>();
		transaction.add(new BeginAction(this));
	}

	/**
	 * returns this transaction's id
	 * @return this transaction's id
	 */
	public int transactionId() {
		return transactionId;
	}

	/**
	 * begins the transaction. This method should assign a timestamp to this 
	 * transaction
	 */
	public void begin() {
		TransactionManager.instance().register(this);
		status = RUNNING;
	}

	/**
	 * sets this transaction's timestamp
	 * @param timestamp this transaction's timestamp
	 */
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * returns the step at the given index
	 * @param index index of step
	 * @return step at index
	 */
	public DBAction getStep(int index) {
		return (index >= 0 && index < transaction.size()) 
					? transaction.get(index) : null;
	}

	/**
	 * executes the next action in this transaction
	 */
	public void step() {
		if( !isFinished() && status != WAITING) {
			DBAction dba = getStep(position);
			dba.execute();
			if( status != WAITING ) {
				position++;
			}
			if( position == transaction.size() ) {
				status = FINISHED;
			}
		}
	}

	/**
	 * returns the current status of this transaction
	 * @return current status of this transaction
	 */
	public String status() {
		return status;
	}

	/**
	 * sets the status of this transaction
	 * @param status status of this transaction
	 */
	public void setStatus(String status) {
		if( !status.equals(NOT_STARTED) && !status.equals(FINISHED) ) {
			this.status = status;
		}
	}

	/**
	 * returns whether this transaction is about to choose to commit or rollback
	 * @return whether this transaction is about to choose to commit or rollback
	 */
	public boolean isCommitting() {
		return transaction.get(position) instanceof CommitAction;
	}

	/**
	 * returns current position in the transaction's steps
	 * @return current position in the transaction's steps
	 */
	public int position() {
		return position;
	}

	/**
	 * restarts the transaction
	 */
	public void restart() {
		status = RUNNING;
		position = 1;
	}

	/**
	 * gets this transaction's timestamp
	 * @return transaction timestamp
	 */
	public int timestamp() {
		return timestamp;
	}

	/**
	 * returns whether this transaction is finished
	 * @return whether this transaction is finished
	 */
	public boolean isFinished() {
		return status.equals(FINISHED);
	}

	public String toString() {
		String ret = status + " Tran" + transactionId + "\nTimestamp: " 
						+ timestamp + "\n";
		for(int i = 0; i < transaction.size(); i++) {
			if( i > 0 ) {
				ret += "\n";
			}
			if( i == position ) {
				ret += "> ";
			} 
			ret += transaction.get(i);
		}
		return ret;
	}
}