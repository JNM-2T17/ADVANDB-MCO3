package mco3.controller;

import java.util.ArrayList;

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
	// private ConcurrencyPanel cPanel;
	private ExecutePanel ePanel;
	// private MCO3Menu menu;
	private ConnectScreen cFrame;
	private ConStatusPanel csPanel;
	private AddTransactionPanel atPanel;
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

		// menu = new MCO3Menu(this);
		// mf.setJMenuBar(menu);

		// cm = CheckpointManager.instance(30000);
		// cm.start();

		cm = ConnectionManager.instance(this,csPanel);

		tranList = new ArrayList<Transaction>();

		// cPanel = new ConcurrencyPanel(tranList,this);
		// mf.setMain(cPanel);
		ePanel = new ExecutePanel(this,tranList);
		atPanel = new AddTransactionPanel(this);
		mf.setMain(ePanel);

		// setMain(ADD);
		(new Thread() {
			public void run() {
				cm.autoConnect();
			}
		}).start();
	}

	public void addTransaction(Transaction t) {
		tranList.add(t);
		ePanel.update();
		setMain(RUN);
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

	public ReadResult read(String tag, String query, String[] cols, String[] params) {
		return dm.read(tag,query,cols,params);
	}

	public void unregister(String tag) {
		dm.unregister(tag);
	}

	public void commit(String tag) {
		dm.commit(tag);
	}

	public void abort(String tag) {
		dm.abort(tag);
	}

	public boolean check(String tag) {
		return dm.check(tag);
	}

	public void setMain(int value) {
		switch(value) {
			case ADD:
				mf.setMain(atPanel);
				break;
			case RUN:
				mf.setMain(ePanel);
				break;
			default:
		}
	}

	public void syncAll() {
		switch(schema) {
			case "db_hpq":
				cm.sendMessage("db_hpq_marinduque","RUN  0" + (char)30 + (char)4);
				cm.sendMessage("db_hpq_palawan","RUN  0" + (char)30 + (char)4);
				break;
			case "db_hpq_marinduque":
				cm.sendMessage("db_hpq","RUN  0" + (char)30 + (char)4);
				cm.sendMessage("db_hpq_palawan","RUN  0" + (char)30 + (char)4);
				break;
			case "db_hpq_palawan":
				cm.sendMessage("db_hpq","RUN  0" + (char)30 + (char)4);
				cm.sendMessage("db_hpq_marinduque","RUN  0" + (char)30 + (char)4);
				break;
			default:
		}
		runAll();
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
					ePanel.update();
					// cPanel.update();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		if( tranList.size() > 0 ) {
			ePanel.finishPrompt();
		}
	}

	public void delete(Transaction model) {
		tranList.remove(model);
		ePanel.update();
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
			// cPanel.update();
			ePanel.update();
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