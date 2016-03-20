package mco3.model;

import java.util.ArrayList;

/**
 * This class provides a generic implementation for the transaction interface
 * @author Austin Fernandez
 */
public abstract class AbstractTransaction implements Transaction {
	private int transactionId;
	private int status;
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
		transaction = new ArrayList<DBAction>();
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
		LogManager.instance().writeStart(this);
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
		DBAction dba = getStep(position);
		position++;
		dba.execute();
	}

	/**
	 * returns the current status of this transaction
	 * @return current status of this transaction
	 */
	public int status() {
		return status;
	}

	/**
	 * sets the status of this transaction
	 * @param status status of this transaction
	 */
	public void setStatus(int status) {
		if( status >= NOT_STARTED && status <= ROLLBACK ) {
			this.status = status;
		}
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

	public String toString() {
		String ret = "";
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