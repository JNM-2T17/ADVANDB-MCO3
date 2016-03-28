package mco3.model;

import mco3.controller.MCO3Controller;

/**
 * this action commits a transaction
 */
public class CommitAction implements DBAction {
	private Transaction t;

	/**
	 * basic constructor
	 * @param t transaction to commit
	*/
	public CommitAction(Transaction t) {
		this.t = t;
	}

	/**
	 * commits the transaction
	 */
	public synchronized void execute() {
		if( t.abortStatus() == AbstractTransaction.ABORT_AFTER ) {
			t.rollback();
		} else if( t.abortStatus() == AbstractTransaction.FAIL_AFTER ) {
			System.exit(0);
		} else {
			// LogManager.instance().writeCommit(t);
			ConnectionManager cm = ConnectionManager.instance();
			switch(MCO3Controller.schema) {
				case "db_hpq":
					if( cm.isConnected("db_hpq_marinduque") ) {
						cm.sendMessage("db_hpq_marinduque","READY " + t.transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4,"READY! " 
											 + t.transactionId() 
											 + MCO3Controller.schema,this);
						try {
							wait();
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else {
						t.rollback();
						return;
					}
					
					if( cm.isConnected("db_hpq_palawan") ) {
						cm.sendMessage("db_hpq_palawan","READY " + t.transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4,"READY! " 
											 + t.transactionId() 
											 + MCO3Controller.schema,this);
						try {
							wait();
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else {
						t.rollback();
						return;
					}
					
					if( cm.isConnected("db_hpq_marinduque") ) {
						cm.sendMessage("db_hpq_marinduque","COMMIT " + t.transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4);
					} else {
						t.rollback();
						return;
					}
					
					if( cm.isConnected("db_hpq_palawan") ) {
						cm.sendMessage("db_hpq_palawan","COMMIT " + t.transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4);
					} else {
						t.rollback();
						return;
					}

					t.commit();
					break;
				case "db_hpq_marinduque":
					if( cm.isConnected("db_hpq") ) {
						cm.sendMessage("db_hpq","READY " + t.transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4,"READY! " 
											 + t.transactionId() 
											 + MCO3Controller.schema,this);
						try {
							wait();
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else {
						t.rollback();
						return;
					}
					
					if( cm.isConnected("db_hpq_palawan") ) {
						cm.sendMessage("db_hpq_palawan","READY " + t.transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4,"READY! " 
											 + t.transactionId() 
											 + MCO3Controller.schema,this);
						try {
							wait();
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else {
						t.rollback();
						return;
					}

					if( cm.isConnected("db_hpq") ) {
						cm.sendMessage("db_hpq","COMMIT " + t.transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4);
					} else {
						t.rollback();
						return;
					}
					
					if( cm.isConnected("db_hpq_palawan") ) {
						cm.sendMessage("db_hpq_palawan","COMMIT " + t.transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4);
					} else {
						t.rollback();
						return;
					}

					t.commit();
					break;
				case "db_hpq_palawan":
					if( cm.isConnected("db_hpq") ) {
						cm.sendMessage("db_hpq","READY " + t.transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4,"READY! " 
											 + t.transactionId() 
											 + MCO3Controller.schema,this);
						try {
							wait();
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else {
						t.rollback();
						return;
					}

					if( cm.isConnected("db_hpq_marinduque") ) {
						cm.sendMessage("db_hpq_marinduque","READY " + t.transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4,"READY! " 
											 + t.transactionId() 
											 + MCO3Controller.schema,this);
						try {
							wait();
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else {
						t.rollback();
						return;
					}

					if( cm.isConnected("db_hpq") ) {
						cm.sendMessage("db_hpq","COMMIT " + t.transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4);
					} else {
						t.rollback();
						return;
					}
					
					if( cm.isConnected("db_hpq_marinduque") ) {
						cm.sendMessage("db_hpq_marinduque","COMMIT " + t.transactionId()
											 + MCO3Controller.schema
											 + " 0" + (char)30 + (char)4);
					} else {
						t.rollback();
						return;
					}

					t.commit();
					break;
				default:
			}
		}
	}

	public String toString() {
		return "COMMIT TRAN";
	}

	public synchronized void wakeUp(boolean status) {
		if( status ) {
			notifyAll();
		} else {
			t.rollback();
		}
	}
}