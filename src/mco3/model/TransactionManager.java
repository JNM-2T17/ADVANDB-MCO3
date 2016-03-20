package mco3.model;

import java.util.ArrayList;

public class TransactionManager {
	private static TransactionManager instance = null;

	private ArrayList<String> active;
	private int timestamp;

	private TransactionManager(int startTimestamp) {
		timestamp = startTimestamp;
		active = new ArrayList<String>();
	}

	/**
	 * returns the instance of the Timestamp Manager
	 * @return the instance of the Timestamp Manager
	 */
	public static TransactionManager instance() {
		if( instance == null ) {
			instance = new TransactionManager(1);
		}

		return instance;
	}

	/**
	 * returns the instance of the Timestamp Manager
	 * @param startTimestamp start timestamp for recovery
	 * @return the instance of the Timestamp Manager
	 */
	public static TransactionManager instance(int startTimestamp) {
		if( instance == null ) {
			instance = new TransactionManager(startTimestamp);
		} else {
			instance.setTimestamp(startTimestamp);
		}

		return instance;
	}

	private synchronized void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * registers a transaction and sets its timestamp
	 * @param t transaction to register
	 */
	public synchronized void register(Transaction t) {
		t.setTimestamp(timestamp);
		timestamp++;
		active.add(t.transactionId() + "");
	}

	/**
	 * removes a transaction
	 * @param t transaction to remove
	 */
	public synchronized void unregister(Transaction t) {
		active.remove("" + t.transactionId());
	}

	/**
	 * returns comma separated list of active transaction ids
	 * @return comma separated list of active transaction ids
	 */
	public synchronized String transactionList() {
		String ret = "";
		for(int i = 0; i < active.size(); i++) {
			if(i > 0) {
				ret += ",";
			}
			ret += active.get(i);
		}
		return ret;
	}

}