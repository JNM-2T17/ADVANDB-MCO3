package mco3.model;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

import mco3.controller.MCO3Controller;
import mco3.view.ResultsFrame;

/**
 * DBAction for reading from the database
 * @author Austin Fernandez
 */
public class ReadAction implements DBAction {
	private ResultSet result;
	private Transaction t;
	private String query;
	private String[] params;
	private String[] columns;
	private String[] items;
	private Connection con;
	private boolean status;
	private ReadResult curr;

	/**
	 * basic constructor
	 * @param t transaction
	 * @param query query to executeQuery
	 * @param columns columns in query
	 * @param params parameters for the query
	 * @param items db items to read
	 */
	public ReadAction(Transaction t,String query, String[] columns, String[] params
						,String[] items) {
		this.t = t;
		this.con = t.getConnection();
		this.query = query;
		this.columns = columns;
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

		ConnectionManager cm = ConnectionManager.instance();

		switch(MCO3Controller.schema) {
			case "db_hpq":
				ResultSet rs1,rs2;
				PreparedStatement ps;
				try {
					ps = con.prepareStatement(query);
					for(int i = 0; i < params.length; i++) {
						ps.setString(i + 1, params[i]);
					}
					System.out.println(ps);
					rs1 = ps.executeQuery();
					new ResultsFrame(new ReadResult(columns,rs1));
				} catch(Exception e) {
					e.printStackTrace();
					t.rollback();
				}
				//Display Results
				break;
			case "db_hpq_marinduque":
				//try sending to central READ <this.id> message.length() + (char)30 + message + (char)4
				//resultsHeader is DATA<space>transactionId()+MCO3Controller.schema
				if(cm.sendMessage("db_hpq","READ " 
										+ t.transactionId() 
										+ MCO3Controller.schema + " " 
										+ message.length() + (char)30 + message
										+ (char)4,"DATA " + t.transactionId()+MCO3Controller.schema,this)){//if success, create results object and display results
					//create results object and display results
					try{
				      t.setStatus(Transaction.WAITING);
				      wait();
				      t.setStatus(Transaction.RUNNING);
				      if(status){
				      	new ResultsFrame(curr);
				      }
				    }catch(Exception e){
					    e.printStackTrace();
					    t.rollback();
				    }
				    //Display Results
				}
				//SEND READ TO OTHER NODE
				else{//else if not connected or failed 
				   if(cm.sendMessage("db_hpq_palawan","READ " 
										+ t.transactionId() 
										+ MCO3Controller.schema + " " 
										+ message.length() + (char)30 + message
										+ (char)4,"DATA " + t.transactionId()+MCO3Controller.schema,this)){
				   		try{
						    t.setStatus(Transaction.WAITING);
						    wait();
						    t.setStatus(Transaction.RUNNING);
						    if(status) {
						    	//process
							    ps = con.prepareStatement(query);
								for(int i = 0; i < params.length; i++) {
									ps.setString(i + 1, params[i]);
								}
								System.out.println(ps);
								rs1 =ps.executeQuery();

								//Read from self
								ps = con.prepareStatement(query);
								for(int i = 0; i < params.length; i++) {
									ps.setString(i + 1, params[i]);
								}
								System.out.println(ps);
								rs2 = ps.executeQuery();
							}
						}catch(Exception e){
							e.printStackTrace();
							t.rollback();
						}	
				    }
				      //if success store temporary results object
				    //get union of results
	           	   //display
				}
				
				break;
			case "db_hpq_palawan":
				//try sending to central READ <this.id> message.length() + (char)30 + message + (char)4
				//resultsHeader is DATA<space>transactionId()+MCO3Controller.schema				
				if(cm.sendMessage("db_hpq","READ " 
										+ t.transactionId() 
										+ MCO3Controller.schema + " " 
										+ message.length() + (char)30 + message
										+ (char)4,"DATA " + t.transactionId()+MCO3Controller.schema,this)){//if success, create results object and display results
					//create results object and display results
					try{
					    t.setStatus(Transaction.WAITING);
					    wait();
					    t.setStatus(Transaction.RUNNING);
					    if(status){
					    	new ResultsFrame(curr);
				      	}
					}catch(Exception e){
						e.printStackTrace();
						t.rollback();
					}	
				}
				//SEND READ TO OTHER NODE
				else{//else if not connected or failed
					if(cm.sendMessage("db_hpq_marinduque","READ " 
										+ t.transactionId() 
										+ MCO3Controller.schema + " " 
										+ message.length() + (char)30 + message
										+ (char)4,"DATA " +t.transactionId()+MCO3Controller.schema,this)){
						try{
						    t.setStatus(Transaction.WAITING);
						    wait();
						    t.setStatus(Transaction.RUNNING);
						    if(status){
						    	ps = con.prepareStatement(query);
							    for(int i = 0; i < params.length; i++) {
								    ps.setString(i + 1, params[i]);
							    }
							    System.out.println(ps);
							    rs1 =ps.executeQuery();

							    //Read from self
								ps = con.prepareStatement(query);
								for(int i = 0; i < params.length; i++) {
									ps.setString(i + 1, params[i]);
								}
								System.out.println(ps);
								rs2 = ps.executeQuery();
						    }
						}catch(Exception e){
							e.printStackTrace();
							t.rollback();
						}	
					}
				      //if success store temporary results object
				    //get union of results
				   //display
				}
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

	public synchronized void wakeUp(ReadResult rr) {
		status = true;
		this.curr = rr;
		notifyAll();
	}
}