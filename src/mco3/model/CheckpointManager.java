package mco3.model;

public class CheckpointManager extends Thread {
	private long interval;

	public CheckpointManager(long interval) {
		this.interval = interval;
	}

	public void run() {
		while(true) {
			try {
				Thread.sleep(interval);
			} catch(Exception e) {
				e.printStackTrace();
			}
			LogManager.instance().writeCheckpoint();
		}
	}
}