package mco3.model;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

import mco3.controller.MCO3Controller;

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
	 * @param query query to execute
	 * @param params parameters for the query
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
		// LogManager.instance().flush();
		// LogManager.instance().writeChange(t,item,oldVal,newVal);
		try {
			String message = query;
			for(int i = 0; i < params.length; i++) {
				message += (char)31 + params[i];
			}
			ConnectionManager cm = ConnectionManager.instance();
			switch(MCO3Controller.schema) {
				case "db_hpq":
					PreparedStatement ps = con.prepareStatement(query);
					for(int i = 0; i < params.length; i++) {
						ps.setString(i + 1, params[i]);
					}
					System.out.println(ps);
					ps.execute();

					if( !cm.sendMessage("db_hpq_marinduque","WRITE " 
										+ t.transactionId() 
										+ MCO3Controller.schema + " " 
										+ message.length() + (char)30 + message
										+ (char)4)) {
						t.rollback();
					}

					if( !cm.sendMessage("db_hpq_palawan","WRITE " 
										+ t.transactionId() 
										+ MCO3Controller.schema + " " 
										+ message.length() + (char)30 + message
										+ (char)4)) {
						t.rollback();
					}
					break;
				case "db_hpq_marinduque":
					if( !cm.sendMessage("db_hpq","WRITE " 
										+ t.transactionId() 
										+ MCO3Controller.schema + " " 
										+ message.length() + (char)30 + message
										+ (char)4)) {
						t.rollback();
					}

					ps = con.prepareStatement(query);
					for(int i = 0; i < params.length; i++) {
						ps.setString(i + 1, params[i]);
					}
					System.out.println(ps);
					ps.execute();

					if( !cm.sendMessage("db_hpq_palawan","WRITE " 
										+ t.transactionId() 
										+ MCO3Controller.schema + " " 
										+ message.length() + (char)30 + message
										+ (char)4)) {
						t.rollback();
					}
					break;
				case "db_hpq_palawan":
					if( !cm.sendMessage("db_hpq","WRITE " 
										+ t.transactionId() 
										+ MCO3Controller.schema + " " 
										+ message.length() + (char)30 + message
										+ (char)4)) {
						t.rollback();
					}

					if( !cm.sendMessage("db_hpq_marinduque","WRITE " 
										+ t.transactionId() 
										+ MCO3Controller.schema + " " 
										+ message.length() + (char)30 + message
										+ (char)4)) {
						t.rollback();
					}

					ps = con.prepareStatement(query);
					for(int i = 0; i < params.length; i++) {
						ps.setString(i + 1, params[i]);
					}
					System.out.println(ps);
					ps.execute();
					break;
				default:
			}
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