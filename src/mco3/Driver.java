package mco3;

import mco3.model.*;

import java.util.ArrayList;
import java.util.Scanner;

public class Driver {
	public static void main(String[] args) {
		// CheckpointManager cm = new CheckpointManager(30000);
		// cm.start();

		ArrayList<Transaction> tranList = new ArrayList<Transaction>();
		tranList.add(new ReadDensity2(1,IsoLevel.SERIALIZABLE));
		tranList.add(new ReadDensity2(2,IsoLevel.READ_REPEATABLE));
		tranList.add(new ReadDensity2(3,IsoLevel.READ_COMMITTED));
		tranList.add(new ReadDensity2(4,IsoLevel.READ_UNCOMMITTED));

		boolean finished;

		do {
			String[][] transactions = new String[tranList.size()][];
			int max = 0;
			for(int i = 0; i < tranList.size(); i++) {
				transactions[i] = tranList.get(i).toString().split("\n");
				if( transactions[i].length > max ) {
					max = transactions[i].length;
				}
			}

			for(int i = 0; i < max; i++ ) {
				for(int j = 0; j < transactions.length; j++ ) {
					if( j > 0 ) {
						System.out.print("\t");
					}
					if( i < transactions[j].length) {
						System.out.print(transactions[j][i]);
					}
				}
				System.out.println();
			}

			Scanner sc = new Scanner(System.in);
			System.out.print("Which transaction? ");
			int tNo = sc.nextInt() - 1;

			tranList.get(tNo).step();

			finished = true;
			for(Transaction t : tranList ) {
				finished = finished && t.isFinished();
			}
		} while(!finished);
	}
}