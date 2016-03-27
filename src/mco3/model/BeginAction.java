package mco3.model;

import mco3.controller.MCO3Controller;

/**
 * this action begins a transaction
 */
public class BeginAction implements DBAction {
	private Transaction t;

	/**
	 * basic constructor
	 * @param t transaction to begin
	 */
	public BeginAction(Transaction t) {
		this.t = t;
	}

	/**
	 * begins the transaction
	 */
	public void execute() {
		t.begin();
		ConnectionManager cm = ConnectionManager.instance();
		String message = t.isoLevel().level() + "";
		switch(MCO3Controller.schema) {
			case "db_hpq":
				if( cm.isConnected("db_hpq_palawan") ) {
					cm.sendMessage("db_hpq_palawan","BEGIN " + t.transactionId()
										 + " " + message.length() + (char)30 
										 + message + (char)4);
				}

				if( cm.isConnected("db_hpq_marinduque") ) {
					cm.sendMessage("db_hpq_marinduque","BEGIN " + t.transactionId()
										 + " " + message.length() + (char)30 
										 + message + (char)4);
				}
				break;
			case "db_hpq_marinduque":
				if( cm.isConnected("db_hpq_palawan") ) {
					cm.sendMessage("db_hpq_palawan","BEGIN " + t.transactionId()
										 + " " + message.length() + (char)30 
										 + message + (char)4);
				}

				if( cm.isConnected("db_hpq") ) {
					cm.sendMessage("db_hpq","BEGIN " + t.transactionId()
										 + " " + message.length() + (char)30 
										 + message + (char)4);
				}
				break;
			case "db_hpq_palawan":
				if( cm.isConnected("db_hpq") ) {
					cm.sendMessage("db_hpq","BEGIN " + t.transactionId()
										 + " " + message.length() + (char)30 
										 + message + (char)4);
				}

				if( cm.isConnected("db_hpq_marinduque") ) {
					cm.sendMessage("db_hpq_marinduque","BEGIN " + t.transactionId()
										 + " " + message.length() + (char)30 
										 + message + (char)4);
				}
				break;
			default:
		}
	}

	public String toString() {
		return "BEGIN TRAN";
	}

	public synchronized void wakeUp(boolean status) {
		notifyAll();
	}
}