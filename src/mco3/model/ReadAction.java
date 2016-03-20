package mco3.model;

import java.sql.ResultSet;
import java.sql.Connection;

/**
 * DBAction for reading from the database
 * @author Austin Fernandez
 */
public class ReadAction implements DBAction {
	private ResultSet result;
	private String query;
	private String[] params;
	private String[] items;
	private Connection con;

	/**
	 * basic constructor
	 * @param con DBConnection
	 * @param query query to execute
	 * @param params parameters for the query
	 * @param items db items to read
	 */
	public ReadAction(Connection con,String query, String[] params
						,String[] items) {
		this.con = con;
		this.query = query;
		this.params = params;
		this.items = items;
	}

	/**
	 * reads from the database
	 */
	public void execute() {
		//SQL shit here
	}

	/**
	 * returns the result set
	 * @return result set
	 */
	public ResultSet result() {
		return result;
	}

	public String toString() {
		String ret = "";
		for( int i = 0; i < items.length; i++ ) {
			if( i > 0 ) {
				ret += ";";
			}
			ret += "read(" + items[i] + ")";
		}
		return ret;
	}
}