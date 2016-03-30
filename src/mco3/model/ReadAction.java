package mco3.model;

import java.sql.ResultSet;
import java.sql.Connection;

import mco3.controller.MCO3Controller;

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
	private boolean status;

	/**
	 * basic constructor
	 * @param con DBConnection
	 * @param query query to execute
	 * @param params parameters for the query
	 * @param items db items to read
	 */
	public ReadAction(Connection con,String query, String[] columns, String[] params
						,String[] items) {
		this.con = con;
		this.query = query;
		this.params = params;
		this.items = items;
	}

	/**
	 * reads from the database
	 */
	public synchronized void execute() {
		String message = query + (char)28;
		for( int i = 0; i < columns.length; i++ ) {
			if( i > 0 ) {
				message += ",";
			}
			message += columns[i];
		}
		message += (char)28;
		for( int i = 0; i < params.length; i++ ) {
			if( i > 0 ) {
				message += ",";
			}
			message += params[i];
		}

		switch(MCO3Controller.schema) {
			case "db_hpq":
				// just query
				break;
			case "db_hpq_marinduque":
				//try sending to central READ <this.id> message.length() + (char)30 + message + (char)4
				//resultsHeader is DATA<space>transactionId()+MCO3Controller.schema
				//wait();
				//if success, create results object and display results
				//else if not connected or failed
				//SEND READ TO OTHER NODE
				//if success store temporary results object
				//Read from self
				//get union of results
				//display
				break;
			case "db_hpq_palawan":
				//try sending to central READ <this.id> message.length() + (char)30 + message + (char)4
				//resultsHeader is DATA<space>transactionId()+MCO3Controller.schema
				//wait();
				//if success, create results object and display results
				//else if not connected or failed
				//SEND READ TO OTHER NODE
				//if success store temporary results object
				//Read from self
				//get union of results
				//display
				break;
			default:
		}
	}

	/**
	 * returns the result set
	 * @return result set
	 */
	public ResultSet result() {
		return result;
	}

	public String toString() {
		String ret = "<html>";
		for( int i = 0; i < items.length; i++ ) {
			if( i > 0 ) {
				ret += "<br/>";
			}
			ret += "read(" + items[i] + ")";
		}
		return ret + "</html>";
	}

	public synchronized void wakeUp(boolean status) {
		this.status = status;
		notifyAll();
	}
}