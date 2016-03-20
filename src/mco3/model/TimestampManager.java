package mco3.model;

public class TimestampManager {
	private static TimestampManager instance = null;
	
	private int timestamp;

	private TimestampManager() {
		timestamp = 1;
	}

	/**
	 * returns the instance of the Timestamp Manager
	 */
	public static TimestampManager instance() {
		if( instance == null ) {
			instance = new TimestampManager();
		}

		return instance();
	}

	/**
	 * returns a timestamp
	 * @return latest timestamp
	 */
	public synchronized int timestamp() {
		return timestamp++;
	}
}