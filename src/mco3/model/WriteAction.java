package mco3.model;

import java.sql.ResultSet;
import java.sql.Connection;

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
	 * @param con DBConnection
	 * @param query query to execute
	 * @param params parameters for the query
	 * @param item db item to write
	 */
	public WriteAction(Transaction t,Connection con,String query, String[] params
						,String oldVal, String newVal, String item) {
		this.con = con;
		this.query = query;
		this.params = params;
		this.item = item;
		this.oldVal = oldVal;
		this.newVal = newVal;
	}

	/**
	 * reads from the database
	 */
	public void execute() {
		//SQL shit here
		LogManager.instance().flush();
		LogManager.instance().writeChange(t,item,oldVal,newVal);
	}

	public String toString() {
		return "write(" + item + ")";
	}
}