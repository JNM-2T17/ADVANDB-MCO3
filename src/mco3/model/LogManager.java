package mco3.model;

import java.io.*;

public class LogManager {
	private static LogManager instance = null;

	private PrintWriter pw;

	private LogManager() {
		try {
			openWriter(true);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static LogManager instance() {
		if( instance == null ) {
			instance = new LogManager();
		}
		return instance;
	}

	public synchronized void flush() {
		try {
			closeWriter();
		} catch(IOException ioe ) {
			ioe.printStackTrace();
		}

		try {
			openWriter(true);
		} catch(IOException ioe ) {
			ioe.printStackTrace();
		}
	}

	public synchronized void flushClear() {
		try {
			closeWriter();
		} catch(IOException ioe ) {
			ioe.printStackTrace();
		}

		try {
			openWriter(false);
		} catch(IOException ioe ) {
			ioe.printStackTrace();
		}
	}

	public synchronized void writeStart(Transaction t) {
		pw.println(t.transactionId() + " start");
	}

	public synchronized void writeChange(Transaction t, String item
											, String oldVal, String newVal) {
		pw.println(t.transactionId() + (char)30 + item + (char)30 + oldVal 
						+ (char)30 + newVal);
	}

	public synchronized void writeCommit(Transaction t) {
		flush();
		pw.println(t.transactionId() + " commit");	
		flush();
	}

	public synchronized void writeCheckpoint() {
		flush();
		pw.println("CHECKPOINT " 
					+ TransactionManager.instance().transactionList());
		flush();
	}

	private synchronized void openWriter(boolean append) throws IOException {
		pw = new PrintWriter(
				new BufferedWriter(
					new FileWriter(
						new File("recovery.txt"),append)));
	}

	private synchronized void closeWriter() throws IOException {
		pw.close();
	}
}