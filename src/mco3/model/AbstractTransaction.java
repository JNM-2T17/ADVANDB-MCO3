package mco3.model;

import java.util.ArrayList;

import mco3.view.Updatable;

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

	private Updatable view;

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
	 * sets the view that displays this transaction
	 * @param view view to update
	 */
	public void setView(Updatable view) {
		this.view = view;
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
	 * returns number of steps in this transaction
	 * @return number of steps in this transaction
	 */
	public int size() {
		return transaction.size();
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
		if( !isFinished() && status != WAITING ) {
			DBAction dba = getStep(position);
			dba.execute();
			if( status != WAITING ) {
				position++;
			}
			if( position == size() ) {
				status = FINISHED;
			}
			view.update();
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
		switch(status) {
			case NOT_STARTED:
				break;
			default:
				this.status = status;	
				view.update();
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
		releaseLocks();
		view.update();
	}

	/**
	 * rolls back the changes made by this transaction
	 */
	public void rollback() {
		CheckpointManager.instance().lock();
		LogManager.instance().writeAbort(this);
		//undo transaction
		TransactionManager.instance().unregister(this);
		releaseLocks();
		CheckpointManager.instance().unlock();
		position = size();
		status = ROLLBACK;
		view.update();
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
		return status.equals(FINISHED) || status.equals(ROLLBACK);
	}

	/**
	 * partially commits this transaction. This means writing &lt;T commit&gt; 
	 * in the recovery log, which implementers must do.
	 */
	public void commit() {
		TransactionManager.instance().unregister(this);
		status = COMMIT;
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