package mco3.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import mco3.view.Updatable;
import mco3.controller.MCO3Controller;

/**
 * This class provides a generic implementation for the transaction interface
 * @author Austin Fernandez
 */
public abstract class AbstractTransaction implements Transaction {
	public static final int ABORT_NEVER = 0;
	public static final int ABORT_AFTER = 1;
	public static final int FAIL_AFTER = 2;

	private int transactionId;
	private String status;
	private int timestamp;
	private int position;
	protected ArrayList<DBAction> transaction;
	protected Connection con;
	protected int abort;
	private IsoLevel isolation;

	private Updatable view;

	/**
	 * basic constructor
	 * @param id transaction id
	 */
	public AbstractTransaction(int id,IsoLevel isolevel) throws SQLException {
		transactionId = id;
		isolation = isolevel;
		position = 0;
		status = NOT_STARTED;
		transaction = new ArrayList<DBAction>();
		transaction.add(new BeginAction(this));
		con = DBManager.getInstance().getConnection();
		con.setAutoCommit(false);
		switch(isolevel.level()) {
			case 1:
				con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				break;
			case 2:
				con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
				break;
			case 3:
				con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
				break;
			case 4:
				con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				break;
			default:
		}
		abort = ABORT_NEVER;
	}

	/**
	 * basic constructor
	 * @param id transaction id
	 */
	public AbstractTransaction(int id,IsoLevel isolevel,int abort) throws SQLException {
		transactionId = id;
		isolation = isolevel;
		position = 0;
		status = NOT_STARTED;
		transaction = new ArrayList<DBAction>();
		transaction.add(new BeginAction(this));
		con = DBManager.getInstance().getConnection();
		con.setAutoCommit(false);
		switch(isolevel.level()) {
			case 1:
				con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				break;
			case 2:
				con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
				break;
			case 3:
				con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
				break;
			case 4:
				con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				break;
			default:
		}
		this.abort = abort;
	}

	public IsoLevel isoLevel() {
		return isolation;
	}

	public int abortStatus() {
		return abort;
	}

	public void run() {
		while(!isFinished()) {
			step();
		}
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

	public void releaseLocks() {
		if( this instanceof DummyTransaction ) {
			try {
				PreparedStatement s = getConnection().prepareStatement("UNLOCK TABLES");
				s.execute();
				s.close();
			} catch(Exception e) {}
		} else {
			ConnectionManager cm = ConnectionManager.instance();
			switch(MCO3Controller.schema) {
				case "db_hpq":
					try {
						PreparedStatement s = getConnection().prepareStatement("UNLOCK TABLES");
						s.execute();
						s.close();
					} catch(Exception e) {}

					// System.out.println(cm.isConnected("db_hpq_marinduque"));
					// System.out.println(cm.isConnected("db_hpq_palawan"));
					cm.sendMessage("db_hpq_marinduque","UNLOCK " + transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4);
					cm.sendMessage("db_hpq_palawan","UNLOCK " + transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4);
					break;
				case "db_hpq_marinduque":
					// System.out.println(cm.isConnected("db_hpq"));
					// System.out.println(cm.isConnected("db_hpq_palawan"));
					cm.sendMessage("db_hpq","UNLOCK " + transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4);
					
					try {
						PreparedStatement s = getConnection().prepareStatement("UNLOCK TABLES");
						s.execute();
						s.close();
					} catch(Exception e) {}

					cm.sendMessage("db_hpq_palawan","UNLOCK " + transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4);
					break;
				case "db_hpq_palawan":
					// System.out.println(cm.isConnected("db_hpq"));
					// System.out.println(cm.isConnected("db_hpq_marinduque"));
					
					cm.sendMessage("db_hpq","UNLOCK " + transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4);

					cm.sendMessage("db_hpq_marinduque","UNLOCK " + transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4);
					
					try {
						PreparedStatement s = getConnection().prepareStatement("UNLOCK TABLES");
						s.execute();
						s.close();
					} catch(Exception e) {}
					break;
				default:
			}
		}
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
			if( view != null ) {
				view.update();
			}
		}
	}

	/**
	 * returns the database connection
	 * @return the database connection
	 */
	public Connection getConnection() {
		return con;
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
				if( view != null ) {
					view.update();
				}
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
		try {
			CheckpointManager.instance().lock();
			LogManager.instance().writeAbort(this);
			con.rollback();
			undoChanges();
			releaseLocks();
			LogManager.instance().writeStart(this);
			CheckpointManager.instance().unlock();
			status = RUNNING;
			position = 1;
			if( view != null ) {
				view.update();
			}
		} catch(SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * undoes all changes done by this transaction so far
	 */
	public void undoChanges() {
		//TO-DO
	}

	/**
	 * rolls back the changes made by this transaction
	 */
	public void rollback() {
		try {
			System.out.println("Aborting " + transactionId());
			// CheckpointManager.instance().lock();
			// LogManager.instance().writeAbort(this);
			if( !(this instanceof DummyTransaction ) ) {
				ConnectionManager cm = ConnectionManager.instance();
				switch(MCO3Controller.schema) {
					case "db_hpq":
						cm.sendMessage("db_hpq_marinduque","ABORT " + transactionId()
												 + MCO3Controller.schema
												 + " 0" + (char)30 + (char)4);
						cm.sendMessage("db_hpq_palawan","ABORT " + transactionId()
												 + MCO3Controller.schema
												 + " 0" + (char)30 + (char)4);
						break;
					case "db_hpq_marinduque":
						cm.sendMessage("db_hpq","ABORT " + transactionId()
												 + MCO3Controller.schema
												 + " 0" + (char)30 + (char)4);
						
						cm.sendMessage("db_hpq_palawan","ABORT " + transactionId()
												 + MCO3Controller.schema
												 + " 0" + (char)30 + (char)4);
						break;
					case "db_hpq_palawan":
						cm.sendMessage("db_hpq","ABORT " + transactionId()
												 + MCO3Controller.schema
												 + " 0" + (char)30 + (char)4);

						cm.sendMessage("db_hpq_marinduque","ABORT " + transactionId()
												 + MCO3Controller.schema
												 + " 0" + (char)30 + (char)4);
						
						break;
					default:
				}
			}
			System.out.println("ROLLING BACK " + transactionId());
			// PreparedStatement ps = con.prepareStatement("SELECT alp_area FROM hpq_alp WHERE hpq_hh_id = 11328");
			// System.out.println(ps);
			// ResultSet rs = ps.executeQuery();
			// if(rs.next() ) {
			// 	System.out.println("BEFORE ROLLBACK WITH ALP_AREA = " + rs.getInt("alp_area"));
			// } else {
			// 	System.out.println("NO DATA");
			// }
			con.rollback();
			// ps = con.prepareStatement("SELECT alp_area FROM hpq_alp WHERE hpq_hh_id = 11328");
			// System.out.println(ps);
			// rs = ps.executeQuery();
			// if(rs.next() ) {
			// 	System.out.println("ROLLBACK WITH ALP_AREA = " + rs.getInt("alp_area"));
			// } else {
			// 	System.out.println("NO DATA");
			// }
			releaseLocks();
			con.close();
			TransactionManager.instance().unregister(this);
			
			// CheckpointManager.instance().unlock();
			position = size();
			status = ROLLBACK;

			if( view != null ) {
				view.update();
			}
		} catch(SQLException se) {
			se.printStackTrace();
		}
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
		try {
			System.out.println("Committing " + transactionId() );
			// PreparedStatement ps = con.prepareStatement("SELECT alp_area FROM hpq_alp WHERE hpq_hh_id = 11328");
			// System.out.println(ps);
			// ResultSet rs = ps.executeQuery();
			// if(rs.next() ) {
			// 	System.out.println("BEFORE COMMITTING WITH ALP_AREA = " + rs.getInt("alp_area"));
			// } else {
			// 	System.out.println("NO DATA");
			// }
			con.commit();
			// ps = con.prepareStatement("SELECT alp_area FROM hpq_alp WHERE hpq_hh_id = 11328");
			// System.out.println(ps);
			// rs = ps.executeQuery();
			// if(rs.next() ) {
			// 	System.out.println("COMMITTING WITH ALP_AREA = " + rs.getInt("alp_area"));
			// } else {
			// 	System.out.println("NO DATA");
			// }
			releaseLocks();
			con.close();
			TransactionManager.instance().unregister(this);
			status = COMMIT;
		} catch(SQLException se) {
			se.printStackTrace();
			rollback();
		}
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