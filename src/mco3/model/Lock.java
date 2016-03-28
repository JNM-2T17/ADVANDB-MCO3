package mco3.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import mco3.controller.MCO3Controller;

/**
 * Command object that issues a write lock for the given transaction and item
 */
public class Lock implements DBAction {
	private Transaction t;
	private String[] items;
	private String[] modes;

	/**
	 * basic constructor
	 * @param t owning transaction
	 * @param item item to lock
	 */
	public Lock(Transaction t, String[] items, String[] modes) {
		this.t = t;
		this.items = items;
		this.modes = modes;
	}

	/**
	 * requests the write lock
	 */
	public synchronized void execute() {
		String lock = "LOCK TABLES ";
		boolean isRead = true;
		for(int i = 0; i < items.length; i++) {
			if( i > 0 ) {
				lock += ",";
			}
			lock += items[i] + " " + modes[i];
			isRead = isRead && modes[i].equals("READ");
			// if( modes[i].equals("WRITE")) {
				// System.out.println(t.transactionId() + " wl(" + item + ")");
				// LockManager.instance().writeLock(t,item);
			// } else if( modes[i].equals("READ")) {
				// System.out.println(t.transactionId() + " wl(" + item + ")");
				// LockManager.instance().readLock(t,item);
			// }
		}

		ConnectionManager cm = ConnectionManager.instance();
		try {
			switch(MCO3Controller.schema) {
				case "db_hpq":
					PreparedStatement ps 
						= t.getConnection().prepareStatement(lock);
					// System.out.println(ps);
					ps.execute();
					ps.close();
					if( isRead) {
						return;
					}

					if( cm.isConnected("db_hpq_marinduque") ) {
						cm.sendMessage("db_hpq_marinduque","LOCK " + t.transactionId()
											 + MCO3Controller.schema
											 + " " + lock.length() + (char)30 
											 + lock + (char)4,"OKLOCK " 
											 + t.transactionId() 
											 + MCO3Controller.schema,this);
						try {
							wait();
							if( isRead ) {
								return;
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else if( !isRead ) {
						t.rollback();
					}
					
					if( cm.isConnected("db_hpq_palawan") ) {
						cm.sendMessage("db_hpq_palawan","LOCK " + t.transactionId()
											 + MCO3Controller.schema
											 + " " + lock.length() + (char)30 
											 + lock + (char)4,"OKLOCK " 
											 + t.transactionId() 
											 + MCO3Controller.schema,this);
						try {
							wait();
							if( isRead ) {
								return;
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else if( !isRead ) {
						t.rollback();
					}

					break;
				case "db_hpq_marinduque":
					if( cm.isConnected("db_hpq") ) {
						cm.sendMessage("db_hpq","LOCK " + t.transactionId()
											 + MCO3Controller.schema
											 + " " + lock.length() + (char)30 
											 + lock + (char)4,"OKLOCK " 
											 + t.transactionId() 
											 + MCO3Controller.schema,this);
						try {
							wait();
							if( isRead) {
								return;
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else if( !isRead ) {
						t.rollback();
					}
					
					ps = t.getConnection().prepareStatement(lock);
					// System.out.println(ps);
					ps.execute();
					ps.close();
					if( isRead ) {
						return;
					}

					if( cm.isConnected("db_hpq_palawan") ) {
						cm.sendMessage("db_hpq_palawan","LOCK " + t.transactionId()
											 + MCO3Controller.schema
											 + " " + lock.length() + (char)30 
											 + lock + (char)4,"OKLOCK " 
											 + t.transactionId() 
											 + MCO3Controller.schema,this);
						try {
							wait();
							if( isRead ) {
								return;
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else if( !isRead ) {
						t.rollback();
					}

					break;
				case "db_hpq_palawan":
					if( cm.isConnected("db_hpq") ) {
						cm.sendMessage("db_hpq","LOCK " + t.transactionId()
											 + MCO3Controller.schema
											 + " " + lock.length() + (char)30 
											 + lock + (char)4,"OKLOCK " 
											 + t.transactionId() 
											 + MCO3Controller.schema,this);
						try {
							wait();
							if( isRead ) {
								return;
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else if( !isRead ) {
						t.rollback();
					}

					if( cm.isConnected("db_hpq_marinduque") ) {
						cm.sendMessage("db_hpq_marinduque","LOCK " + t.transactionId()
											 + MCO3Controller.schema
											 + " " + lock.length() + (char)30 
											 + lock + (char)4,"OKLOCK " 
											 + t.transactionId() 
											 + MCO3Controller.schema,this);
						try {
							wait();
							if( isRead ) {
								return;
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else if( !isRead ) {
						t.rollback();
					}

					ps = t.getConnection().prepareStatement(lock);
					// System.out.println(ps);
					ps.execute();
					ps.close();
					break;
				default:
			}
		} catch( SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * returns the string representation of this write lock request
	 * @return the string representation of this write lock request
	 */
	public String toString() {
		String ret = "<html>";
		for(int i = 0; i < items.length; i++) {
			if( i > 0 ) {
				ret += "<br/>";
			}
			if( modes[i].equals("WRITE")) {
				ret += "wl(" + items[i] + ")";
			} else if( modes[i].equals("READ")) {
				ret += "rl(" + items[i] + ")";
			}
		}
		return ret + "</html>";
	}

	public synchronized void wakeUp(boolean status) {
		if( status ) {
			notifyAll();
		} else {
			t.rollback();
		}
	}
}