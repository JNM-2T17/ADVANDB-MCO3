package mco3.model;

public class CheckpointManager extends Thread {
	public static CheckpointManager instance = null;

	private long interval;
	private boolean lock;

	private CheckpointManager(long interval) {
		this.interval = interval;
		lock = false;
	}

	/**
	 * returns the single instance of CheckpointManager
	 * @return the single instance of CheckpointManager
	 */
	public synchronized static CheckpointManager instance() {
		if( instance == null ) {
			instance = new CheckpointManager(30000);
		}
		return instance;
	}

	/**
	 * returns the single instance of CheckpointManager and sets the interval
	 * @param interval new interval in milliseconds
	 * @return the single instance of CheckpointManager
	 */
	public synchronized static CheckpointManager instance(long interval) {
		if( instance == null ) {
			instance = new CheckpointManager(interval);
		} else {
			instance.setInterval(interval);
		}
		return instance;
	}

	/**
	 * sets the CheckpointManager's interval 
	 * @param interval new interval in milliseconds
	 */
	public void setInterval(long interval) {
		if( interval > 0 ) {
			this.interval = interval;
		}
	}

	/**
	 * prevents checkpoints from being logged
	 */
	public void lock() {
		lock = true;
	}

	/**
	 * allows checkpoints to be logged
	 */
	public void unlock() {
		lock = false;
	}

	public void run() {
		while(true) {
			try {
				Thread.sleep(interval);
			} catch(Exception e) {
				e.printStackTrace();
			}
			if( !lock ) {
				LogManager.instance().writeCheckpoint();
			}
		}
	}
}