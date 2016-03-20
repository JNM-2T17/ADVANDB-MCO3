package mco3.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages locks in the database
 * @author Austin Fernandez
 */
public class LockManager {
	private static LockManager instance = null;

	private HashMap<String,Lock> locks;

	private LockManager() {
		locks = new HashMap<String,Lock>();
	}

	/**
	 * returns the instance of the LockManager
	 * @return the instance of the LockManager
	 */
	public synchronized static LockManager instance() {
		if( instance == null ) {
			instance = new LockManager();
		}

		return instance;
	}

	/**
	 * claims a read lock on the chosen item for the transaction
	 * @param t transaction claiming the lock
	 * @param item item to lock
	 */
	public synchronized void readLock(Transaction t, String item) {
		Lock l = locks.get(item);
		if( l == null ) {
			locks.put(item, l = new Lock());
			l.addReader(t);
		} else {
			while( l.isWriting() ) {
				//wound
				if( t.timestamp() < l.getHeadLock().timestamp() ) {
					l.getHeadLock().restart();
				}
				try {
					t.setStatus(Transaction.WAITING);
					wait();
					t.setStatus(Transaction.RUNNING);
				} catch(InterruptedException ie) {
					ie.printStackTrace();
				}
			}
			l.addReader(t);
		}
	}

	/**
	 * claims a write lock on the chosen item for the transaction
	 * @param t transaction claiming the lock
	 * @param item item to lock
	 */
	public synchronized void writeLock(Transaction t, String item) {
		Lock l = locks.get(item);
		if( l == null ) {
			locks.put(item, l = new Lock());
			l.setWriting(t,true);
		} else {
			while( l.isWriting() || l.readers() > 0 ) {
				//wound
				if( t.timestamp() < l.getHeadLock().timestamp() ) {
					l.getHeadLock().restart();
				}
				try {
					t.setStatus(Transaction.WAITING);
					wait();
					t.setStatus(Transaction.RUNNING);
				} catch(InterruptedException ie) {
					ie.printStackTrace();
				}
			}
			l.addReader(t);
		}	
	}

	/**
	 * unlocks an item for a transaction
	 * @param t transaction claiming the lock
	 * @param item item to unlock
	 */
	public synchronized void unlock(Transaction t, String item) {
		Lock l = locks.get(item);
		if( l == null ) {
			return;
		} else {
			if( l.isWriting() ) {
				l.setWriting(t,false);
			} else {
				l.subReader(t);
			}
			notifyAll();
		}
	}

	/**
	 * class that manages locks on one item
	 */
	public class Lock {
		private int readers;
		private boolean isWriting;
		private ArrayList<Transaction> lockers;

		/**
		 * initializes lock values
		 */
		public Lock() {
			readers = 0;
			isWriting = false;
			lockers = new ArrayList<Transaction>();
		}

		/**
		 * returns number of transactions reading an item
		 * @return number of transactions reading an item
		 */
		public int readers() {
			return readers;
		}

		/**
		 * returns whether a transaction is writing to the item
		 * @return whether a transaction is writing to the item
		 */
		public boolean isWriting() {
			return isWriting;
		}

		/**
		 * get first transaction to have acquired current lock
		 * @return first transaction to have acquired current lock
		 */
		public Transaction getHeadLock() {
			if( lockers.size() == 0 ) {
				return null;
			} else {
				return lockers.get(0);
			}
		}

		/**
		 * registers a reader in this lock
		 * @param t transaction to read the item
		 */
		public void addReader(Transaction t) {
			readers++;
			lockers.add(t);
		}

		/**
		 * unregisters a reader in this lock
		 * @param t transaction to unregister
		 */
		public void subReader(Transaction t) {
			readers--;
			lockers.remove(t);
		}

		/**
		 * sets whether a given transaction is writing to this item or not
		 * @param t transaction to set
		 * @param isWriting whether transaction is writing or not
		 */
		public void setWriting(Transaction t,boolean isWriting) {
			this.isWriting = isWriting;
			if( isWriting ) {
				lockers.add(t);
			} else {
				lockers.remove(t);
			}
		}
	}
}