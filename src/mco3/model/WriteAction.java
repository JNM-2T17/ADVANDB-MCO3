package mco3.model;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * DBAction for writing from the database
 * @author Austin Fernandez
 */
public class WriteAction implements DBAction {
	private Transaction t;
	private String query;
	private String[] params;
	private String item;
	private String oldVal;
	private String newVal;
	private Connection con;

	/**
	 * basic constructor
	 * @param t transaction that owns this action
	 * @param con DBConnection
	 * @param query query to execute
	 * @param params parameters for the query
	 * @param oldVal old data values
	 * @param newVal new data values
	 * @param item db item to write
	 */
	public WriteAction(Transaction t,String query, String[] params,String item) {
		this.t = t;
		this.con = t.getConnection();
		this.query = query;
		this.params = params;
		this.item = item;
	}

	/**
	 * reads from the database
	 */
	public void execute() {
		//SQL shit here
		LogManager.instance().flush();
		LogManager.instance().writeChange(t,item,oldVal,newVal);
		try {
			PreparedStatement ps = con.prepareStatement(query);
			for(int i = 0; i < params.length; i++) {
				ps.setString(i + 1, params[i]);
			}
			System.out.println(ps);
			ps.execute();
		} catch(Exception e) {
			e.printStackTrace();
			t.rollback();
		}
	}

	public String toString() {
		return "write(" + item + ")";
	}

	public synchronized void wakeUp(boolean status) {
		notifyAll();
	}
}