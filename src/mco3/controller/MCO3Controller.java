package mco3.controller;

import java.util.ArrayList;
import java.util.Scanner;

import mco3.model.*;
import mco3.view.*;

public class MCO3Controller {
	public static final int ADD = 1;
	public static final int RUN = 2;

	public static String schema;

	private ArrayList<Transaction> tranList;
	private CheckpointManager cm;
	private ConnectionManager conM;

	private MainFrame mf;
	private ConcurrencyPanel cPanel;
	private MCO3Menu menu;
	private ConnectScreen cFrame;
	private ConStatusPanel csPanel;
	private boolean connectOpen;
	private DummyManager dm;

	public MCO3Controller(String schema) throws Exception {
		this.schema = schema;
		DBManager.schema = schema;
		connectOpen = false;

		dm = new DummyManager();

		mf = new MainFrame();

		csPanel = new ConStatusPanel(schema);
		mf.setSouth(csPanel);

		menu = new MCO3Menu(this);
		mf.setJMenuBar(menu);

		// cm = CheckpointManager.instance(30000);
		// cm.start();

		conM = ConnectionManager.instance(this,csPanel);

		tranList = new ArrayList<Transaction>();

		IsoLevel[] isos = new IsoLevel[] {
			IsoLevel.READ_UNCOMMITTED,
			IsoLevel.READ_COMMITTED,
			IsoLevel.READ_REPEATABLE,
			IsoLevel.SERIALIZABLE
		};

		int i = 1;

		for( int j = 0; j < isos.length; j++, i += 2 ) {
			tranList.add(new ReadDensity2(i,isos[j]));
			tranList.add(new EditAlp(i + 1,isos[j],11328,10));
		}
		tranList.add(new EditAlp(9,IsoLevel.READ_UNCOMMITTED,11328,10,AbstractTransaction.ABORT_AFTER));

		cPanel = new ConcurrencyPanel(tranList,this);
		mf.setMain(cPanel);
		// for(Transaction t : tranList) {
		// 	(new Thread(t)).start();
		// }

		// boolean finished;

		// do {
		// 	String[][] transactions = new String[tranList.size()][];
		// 	int max = 0;
		// 	for(int i = 0; i < tranList.size(); i++) {
		// 		transactions[i] = tranList.get(i).toString().split("\n");
		// 		if( transactions[i].length > max ) {
		// 			max = transactions[i].length;
		// 		}
		// 	}
		// 	for(int i = 0; i < max; i++ ) {
		// 		for(int j = 0; j < transactions.length; j++ ) {
		// 			if( j > 0 ) {
		// 				System.out.print("\t");
		// 			}
		// 			if( i < transactions[j].length) {
		// 				System.out.print(transactions[j][i]);
		// 			} else {
		// 				System.out.print("\t");
		// 			}
		// 		}
		// 		System.out.println();
		// 	}

		// 	Scanner sc = new Scanner(System.in);
		// 	System.out.print("Which transaction? ");
		// 	final int tNo = sc.nextInt() - 1;

		// 	(new Thread(){
		// 		public void run() {
		// 			tranList.get(tNo).step();
		// 		}
		// 	}).start();

		// 	Thread.sleep(100);

		// 	finished = true;
		// 	for(Transaction t : tranList ) {
		// 		finished = finished && t.isFinished();
		// 	}
		// } while(!finished);
	}

	public String schema() {
		return schema;
	}

	public void addDummy(String tag, String id, String isolation) {
		dm.add(tag,id,isolation);
	}

	public boolean lock(String tag, String stmt) {
		return dm.lock(tag,stmt);
	}

	public void unlock(String tag) {
		dm.unlock(tag);
	}

	public void write(String tag,String[] query) {
		dm.write(tag,query);
	}

	public void commit(String tag) {
		dm.commit(tag);
	}

	public void abort(String tag) {
		dm.abort(tag);
	}

	public void setMain(int value) {
		switch(value) {
			case ADD:
				break;
			case RUN:
				break;
			default:
		}
	}

	public void connectScreen() {
		if( connectOpen ) {
			cFrame.dispose();
		}
		cFrame = new ConnectScreen(this);
		connectOpen = true;
	}

	public void connect(String ip) {
		System.out.println("Connect to " + ip);
		conM.connect(ip);
		cFrame.dispose();
		connectOpen = false;
	}

	public void step(final Transaction model) {
		if( model.isFinished()) {
			tranList.remove(model);
			cPanel.update();
		}
		(new Thread() {
			public void run() {
				model.step();
			}
		}).start();
	}

	public void rollback(Transaction model) {
		model.rollback();
	}
}