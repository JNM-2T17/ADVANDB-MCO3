package mco3;

import mco3.model.*;

public class Driver {
	public static void main(String[] args) {
		CheckpointManager cm = new CheckpointManager(30000);
		cm.start();
	}
}