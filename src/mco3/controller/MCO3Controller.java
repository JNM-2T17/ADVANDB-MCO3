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
	// private CheckpointManager cm;
	private ConnectionManager cm;

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

		cm = ConnectionManager.instance(this,csPanel);

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
	}

	public String schema() {
		return schema;
	}

	public void addDummy(String tag, String id, String isolation) {
		dm.add(tag,id,isolation);
	}

	public void lock(String tag, String stmt,String socket) {
		if( dm.lock(tag,stmt) ) {
			cm.sendMessage(socket,"OKLOCK " + tag + " 0" + (char)30 + (char)4);
		}
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

	public void runAll() {
		Thread[] threads = new Thread[tranList.size()];
		int i = 0;
		for(Transaction t : tranList) {
			threads[i] = (new Thread(t));
			i++;
		}
		for(Thread t : threads) {
			t.start();
		}
		for(Thread t : threads) {
			try {
				t.join();
				if( tranList.size() > 0 ) {
					tranList.remove(0);
					cPanel.update();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
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
		cm.connect(ip);
		cFrame.dispose();
		connectOpen = false;
	}

	public void step(final Transaction model) {
		if( !cm.temp ) {
			cm.temp = true;
			(new Thread() {
				public void run() {
					runAll();
				}
			}).start();
			return;
		}
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