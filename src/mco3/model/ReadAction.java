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
	private String[] columns;
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

		switch(MCO3Controller.schema) {
			ResultSet rs1,rs2;
			PreparedStatement ps;
			case "db_hpq":
				ps = con.prepareStatement(query);
				for(int i = 0; i < params.length; i++) {
						ps.setString(i + 1, params[i]);
					}
					System.out.println(ps);
					ps.execute();
					//Display Results
				break;
			case "db_hpq_marinduque":
				//try sending to central READ <this.id> message.length() + (char)30 + message + (char)4
				//resultsHeader is DATA<space>transactionId()+MCO3Controller.schema
				if(cm.sendMessage("db_hpq","READ " 
										+ t.transactionId() 
										+ MCO3Controller.schema + " " 
										+ message.length() + (char)30 + message
										+ (char)4,"DATA " + t.transactionId()+MCO3Controller.schema)){//if success, create results object and display results
					//create results object and display results
					try{
				      wait();
				      if(status){
				      	ps = con.prepareStatement(query);
					    for(int i = 0; i < params.length; i++) {
						    ps.setString(i + 1, params[i]);
					    }
					    System.out.println(ps);
					    rs1 = ps.execute();
				      }
				    }catch(Exception e){
					    e.printStackTrace();
				    }
				    //Display Results
				}
				//SEND READ TO OTHER NODE
				else{//else if not connected or failed 
				   if(cb.sendMessage("db_hpq_palawan","READ " 
										+ t.transactionId() 
										+ MCO3Controller.schema + " " 
										+ message.length() + (char)30 + message
										+ (char)4,"DATA " + t.transactionId()+MCO3Controller.schema)){
				   		try{
						    wait();
						    if(status) {
						    	//process
							    ps = con.prepareStatement(query);
								for(int i = 0; i < params.length; i++) {
									ps.setString(i + 1, params[i]);
								}
								System.out.println(ps);
								rs1 =ps.execute();
							}
						}catch(Exception e){
							e.printStackTrace();
						}	
						//Read from self
						ps = con.prepareStatement(query);
						for(int i = 0; i < params.length; i++) {
							ps.setString(i + 1, params[i]);
						}
						System.out.println(ps);
						rs2 = ps.execute();
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
										+ (char)4,"DATA " + t.transactionId()+MCO3Controller.schema)){//if success, create results object and display results
					//create results object and display results
				try{
				    wait();
				    if(status){
				    	ps = con.prepareStatement(query);
					    for(int i = 0; i < params.length; i++) {
						    ps.setString(i + 1, params[i]);
					    }
					    System.out.println(ps);

					    rs1 = ps.execute();
				      }
					}catch(Exception e){
						e.printStackTrace();
					}	
				}
				//SEND READ TO OTHER NODE
				else{//else if not connected or failed
					if(cm.sendMessage("db_hpq_marinduque","READ " 
										+ t.transactionId() 
										+ MCO3Controller.schema + " " 
										+ message.length() + (char)30 + message
										+ (char)4,"DATA " +t.transactionId()+MCO3Controller.schema)){
						try{
						    wait();
						    if(status){
						    	ps = con.prepareStatement(q);
							    for(int i = 0; i < params.length; i++) {
								    ps.setString(i + 1, params[i]);
							    }
							    System.out.println(ps);
							    rs1 =ps.execute();
						    }
						}catch(Exception e){
							e.printStackTrace();
						}	

						//Read from self
						ps = con.prepareStatement(query);
						for(int i = 0; i < params.length; i++) {
							ps.setString(i + 1, params[i]);
						}
						System.out.println(ps);
						rs2 = ps.execute();

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
}