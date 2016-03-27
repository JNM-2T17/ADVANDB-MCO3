package mco3.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import mco3.controller.MCO3Controller;

/**
 * Command object that unlocks an item for the given transaction
 */
public class UnlockAction implements DBAction {
	private Transaction t;
	private String item;

	/**
	 * basic constructor
	 * @param t owning transaction
	 * @param item item to lock
	 */
	public UnlockAction(Transaction t, String item) {
		this.t = t;
		this.item = item;
	}

	/**
	 * requests the read lock
	 */
	public void execute() {
		LockManager.instance().unlock(t,item);
		ConnectionManager cm = ConnectionManager.instance();

		System.out.println(MCO3Controller.schema);
		switch(MCO3Controller.schema) {
			case "db_hpq":
			System.out.println("db_hpq");
				try {
					PreparedStatement s = t.getConnection().prepareStatement("UNLOCK TABLES");
					s.execute();
					s.close();
				} catch(Exception e) {}

				// System.out.println(cm.isConnected("db_hpq_marinduque"));
				// System.out.println(cm.isConnected("db_hpq_palawan"));
				cm.sendMessage("db_hpq_marinduque","UNLOCK " + t.transactionId()
										 + MCO3Controller.schema
										 + " 0" + (char)30 + (char)4);
				cm.sendMessage("db_hpq_palawan","UNLOCK " + t.transactionId()
										 + MCO3Controller.schema
										 + " 0" + (char)30 + (char)4);
				break;
			case "db_hpq_marinduque":
			System.out.println("db_hpq_marinduque");
				// System.out.println(cm.isConnected("db_hpq"));
				// System.out.println(cm.isConnected("db_hpq_palawan"));
				cm.sendMessage("db_hpq","UNLOCK " + t.transactionId()
										 + MCO3Controller.schema
										 + " 0" + (char)30 + (char)4);
				
				try {
					PreparedStatement s = t.getConnection().prepareStatement("UNLOCK TABLES");
					s.execute();
					s.close();
				} catch(Exception e) {}

				cm.sendMessage("db_hpq_palawan","UNLOCK " + t.transactionId()
										 + MCO3Controller.schema
										 + " 0" + (char)30 + (char)4);
				break;
			case "db_hpq_palawan":
			System.out.println("db_hpq_palawan");
				// System.out.println(cm.isConnected("db_hpq"));
				// System.out.println(cm.isConnected("db_hpq_marinduque"));
				
				cm.sendMessage("db_hpq","UNLOCK " + t.transactionId()
										 + MCO3Controller.schema
										 + " 0" + (char)30 + (char)4);

				cm.sendMessage("db_hpq_marinduque","UNLOCK " + t.transactionId()
										 + MCO3Controller.schema
										 + " 0" + (char)30 + (char)4);
				
				try {
					PreparedStatement s = t.getConnection().prepareStatement("UNLOCK TABLES");
					s.execute();
					s.close();
				} catch(Exception e) {}
				break;
			default:
		}
	}

	/**
	 * returns the string representation of this read lock request
	 * @return the string representation of this read lock request
	 */
	public String toString() {
		return "u(" + item + ")";
	}

	public synchronized void wakeUp(boolean status) {
		notifyAll();
	}
}