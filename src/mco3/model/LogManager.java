package mco3.model;

import java.io.*;

/**
 * Class for manging the recovery log
 * @author Austin Fernandez
 */
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

	/**
	 * returns an instance of the LogManager
	 * @return an instance of the LogManager
	 */
	public static LogManager instance() {
		if( instance == null ) {
			instance = new LogManager();
		}
		return instance;
	}

	/**
	 * Flushes logs to disk
	 */
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

	/**
	 * clears the log
	 */
	public synchronized void clear() {
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

	/**
	 * writes a "start" message
	 * @param t transaction that started
	 */
	public synchronized void writeStart(Transaction t) {
		pw.println(t.transactionId() + " start");
	}

	/**
	 * writes a "change" message
	 * @param t transaction that wrote data
	 * @param item item that was changed
	 * @param oldVal old values
	 * @param newVal new values
	 */
	public synchronized void writeChange(Transaction t, String item
											, String oldVal, String newVal) {
		pw.println(t.transactionId() + (char)30 + item + (char)30 + oldVal 
						+ (char)30 + newVal);
	}

	/**
	 * writes a "commit" message
	 * @param t transaction that committed
	 */
	public synchronized void writeCommit(Transaction t) {
		flush();
		pw.println(t.transactionId() + " commit");	
		flush();
	}

	/**
	 * writes a "abort" message
	 * @param t transaction that aborted
	 */
	public synchronized void writeAbort(Transaction t) {
		flush();
		pw.println(t.transactionId() + " abort");	
		flush();
	}

	/**
	 * writes a checkpoint to the log
	 */
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