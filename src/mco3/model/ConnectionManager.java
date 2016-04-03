package mco3.model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import mco3.view.ConStatusPanel;
import mco3.controller.MCO3Controller;

public class ConnectionManager {
	private static ConnectionManager instance;

	private String schema;
	private HashMap<String,Socket> sockets;
	private HashMap<String,DBAction> actions;
	private HashMap<String,String> pendingLocks;
	private Receiver r;
	private ConStatusPanel csPanel;
	private boolean[] status;
	public boolean temp;

	private MCO3Controller control;

	private ConnectionManager(MCO3Controller control,ConStatusPanel csPanel) {
		temp = false;
		this.control = control;
		this.csPanel = csPanel;
		sockets = new HashMap<String,Socket>();
		actions = new HashMap<String,DBAction>();
		pendingLocks = new HashMap<String,String>();
		this.schema = control.schema;
		status = new boolean[3];
		switch(schema) {
			case "db_hpq":
				r = new Receiver("db_hpq_palawan",9090);
				status[0] = true;
				break;
			case "db_hpq_marinduque":
				r = new Receiver("db_hpq",9091);
				status[1] = true;
				break;
			case "db_hpq_palawan":
				r = new Receiver("db_hpq_marinduque",9092);
				status[2] = true;
				break;	
			default:
		}
		r.start();
	}

	public static synchronized ConnectionManager instance(MCO3Controller control
													,ConStatusPanel csPanel) {
		if( instance == null ) {
			instance = new ConnectionManager(control,csPanel);
		}
		return instance;
	}

	public static synchronized ConnectionManager instance() {
		return instance;
	}

	public void autoConnect() {
		String tag = "";
		switch(schema) {
			case "db_hpq":
				tag = "db_hpq_marinduque";
				break;
			case "db_hpq_marinduque":
				tag = "db_hpq_palawan";
				break;
			case "db_hpq_palawan":
				tag = "db_hpq";
				break;	
			default:
		}	
		try {
			BufferedReader br = new BufferedReader(
									new FileReader(
										new File("address.txt")));
			String ip = br.readLine();
			br.close();
			System.out.println("Trying Connection with " + ip);
			while(!isConnected(tag)) {
				connect(ip);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void connect(String ipAddress) {
		try {
			Socket s = null;
			String tag = null;
			switch(schema) {
				case "db_hpq":
					s = new Socket(ipAddress,9091);
					tag = "db_hpq_marinduque";
					break;
				case "db_hpq_marinduque":
					s = new Socket(ipAddress,9092);
					tag = "db_hpq_palawan";
					break;
				case "db_hpq_palawan":
					s = new Socket(ipAddress,9090);
					tag = "db_hpq";
					break;	
				default:
			}	
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeBytes("CONNECT" + (char)4);

			DataInputStream dis = new DataInputStream(s.getInputStream());
			String message = "";
			char c;
			do {
				c = (char)dis.readUnsignedByte();
				if( c != 4 ) {
					message += c;
				}
			} while(c != 4);
			System.out.println(message + " from " + s.getInetAddress().getHostAddress());
			if( message.equals("OK") ) {
				register(tag,s);
			}
			r.wakeSend();
		} catch( Exception e) {
			System.out.println("Connection Failed");
		}
	}

	public synchronized void register(String tag, Socket registree) {
		sockets.put(tag,registree);
		switch(tag) {
			case "db_hpq":
				status[0] = true;
				break;
			case "db_hpq_marinduque":
				status[1] = true;
				break;
			case "db_hpq_palawan":
				status[2] = true;
				break;	
			default:
		}
		csPanel.setModel(status[0],status[1],status[2]);
		(new Listener(tag,registree)).start();
	}

	public synchronized void unregister(String tag) {
		sockets.remove(tag);
		switch(tag) {
			case "db_hpq":
				status[0] = false;
				if( "db_hpq_marinduque".equals(schema)) {
					r.wakeUp();
					System.out.println("RECEIVER OPEN");
				}
				break;
			case "db_hpq_marinduque":
				status[1] = false;
				if( "db_hpq_palawan".equals(schema)) {
					r.wakeUp();
					System.out.println("RECEIVER OPEN");
				}
				break;
			case "db_hpq_palawan":
				status[2] = false;
				if( "db_hpq".equals(schema)) {
					r.wakeUp();
					System.out.println("RECEIVER OPEN");
				}
				break;	
			default:
		}
		String[] keys = actions.keySet().toArray(new String[0]);
		for( String s : keys ) {
			actions.get(s).wakeUp(false);
			actions.remove(s);
		}
		csPanel.setModel(status[0],status[1],status[2]);
		control.unregister(tag);
	}

	public synchronized boolean sendMessage(String tag, String message) {
		Socket s = sockets.get(tag);
		// System.out.println("SENDING " + tag + " " + message);
		if( s != null ) {
			try {
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeBytes(message);
				dos.flush();
				return true;
			} catch( Exception e ) {
				e.printStackTrace();	
			}
		} 
		return false;
	}

	public synchronized boolean sendMessage(String tag, String message, String replyHeader,DBAction dba) {
		Socket s = sockets.get(tag);
		// System.out.println("SENDING " + tag + " " + message);
		if( s != null ) {
			try {
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				dos.writeBytes(message);
				dos.flush();
				System.out.println("Waiting for " + replyHeader);
				actions.put(replyHeader,dba);
				return true;
			} catch( Exception e ) {
				e.printStackTrace();	
			}
		} 
		return false;
	}

	public synchronized boolean isConnected(String tag) {
		return sockets.get(tag) != null;
	}

	public synchronized void processMessage(final String tag, String header
												, final String id
												, final String message) {
		System.out.println("RECEIVED: " + tag + " " + header + " " + id + " " + message);
		switch(header) {
			case "RUN":
				(new Thread() {
					public void run() {
						control.runAll();
					}
				}).start();
				break;
			case "BEGIN":
				// System.out.println("RECEIVED: " + tag + " " + header + " " + id + " " + message);
		
				// if( !temp ) {
				// 	temp = true;
				// 	(new Thread() {
				// 		public void run() {
				// 			control.runAll();
				// 		}
				// 	}).start();
				// }
				control.addDummy(id + tag,id,message);
				String pending = pendingLocks.get(id + tag);
				if( pending != null) {
					final String[] parts = pending.split("" + (char)30);
					pendingLocks.remove(id + tag);
					System.out.println("RELOCKING " + id + tag);
					(new Thread() {
						public void run() {
							control.lock(id + tag,parts[0],parts[1]);
						}
					}).start();
					try {
						Thread.sleep(200);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case "LOCK":
				// System.out.println("RECEIVED: " + tag + " " + header + " " + id + " " + message);
		
				if( control.check(id) ) {
					(new Thread() {
						public void run() {
							control.lock(id,message,tag);
						}
					}).start();
					try {
						Thread.sleep(200);
					} catch(Exception e) {
						e.printStackTrace();
					}
				} else {
					pendingLocks.put(id,message + (char)30 + tag);
				}
				break;
			case "OKLOCK":
				System.out.println(header + " " + id);
				actions.get(header + " " + id).wakeUp(true);
				actions.remove(header + " " + id);
				break;
			case "READY!":
				System.out.println(header + " " + id);
				actions.get(header + " " + id).wakeUp(message.equals("YES"));
				actions.remove(header + " " + id);
				break;
			case "DATA":
				System.out.println(message);
				ReadAction ra = (ReadAction)actions.get(header + " " + id);
				ra.wakeUp(new ReadResult(message));
				actions.remove(header + " " + id);
				break;
			case "UNLOCK":
				control.unlock(id);
				break;
			case "WRITE":
				control.write(id,message.split("" + (char)31));
				break;
			case "READY":
				if( control.check(id) ) {
					sendMessage(tag,"READY! " + id + " 3" + (char)30 + "YES" + (char)4);
				} else {
					sendMessage(tag,"READY! " + id + " 2" + (char)30 + "NO" + (char)4);
				}
				break;
			case "COMMIT":
				control.commit(id);
				break;
			case "ABORT":
				control.abort(id);
				break;
			case "RECON":
				if( !isConnected(id) ) {
					connect(message);
				}
				break;
			case "READ":
				String[] parts = message.split("" + (char)28);
				ReadResult rr = control.read(id,parts[0],parts[1].split(",")
												,parts[2].split(","));
				sendMessage(tag,"DATA " + id + " " + rr.toString().length() 
								+ (char)30 + rr.toString() + (char)4);
				break;
			default:
		}
	}

	private class Receiver extends Thread {
		private ServerSocket ss;
		private String tag;
		private boolean waitSending;

		public Receiver(String tag,int port) {
			this.tag = tag;
			try {
				ss = new ServerSocket(port);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		public synchronized void receive() {
			while(true) {
				waitSending = false;
				try {
					Socket s = ss.accept();
					String message = "";
					DataInputStream dis = new DataInputStream(s.getInputStream());
					char c;
					do {
						c = (char)dis.readUnsignedByte();
						if( c != 4 ) {
							message += c;
						}
					} while(c != 4);
					String ip = s.getInetAddress().getHostAddress();
					System.out.println(message + " from " + ip);
					if( message.equals("CONNECT")) {
						DataOutputStream dos = new DataOutputStream(s.getOutputStream());
						dos.writeBytes("OK" + (char)4);
						register(tag,s);
						String sendTag = "";
						switch(schema) {
							case "db_hpq":
								sendTag = "db_hpq_marinduque";
								break;
							case "db_hpq_marinduque":
								sendTag = "db_hpq_palawan";
								break;
							case "db_hpq_palawan":
								sendTag = "db_hpq";
								break;	
							default:
						}
						System.out.println("SENDING RECON TO " + sendTag);
						while(!sendMessage(sendTag,"RECON " + tag + " " + ip.length() 
										+ (char)30 + ip + (char)4)) {
							waitSending = true;
							wait();
						}
						wait();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void run() {
			receive();
		}

		public synchronized void wakeUp() {
			notifyAll();
		}

		public synchronized void wakeSend() {
			if( waitSending ) {
				notifyAll();
			}
		}
	}

	private class Listener extends Thread {
		private String tag;
		private DataInputStream dis;

		public Listener(String tag,Socket s) {
			this.tag = tag;
			try {
				dis = new DataInputStream(s.getInputStream());
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}

		public void run() {
			while(true) {
				try {
					String header = "";
					char c;
					do {
						try {
							c = (char)dis.readUnsignedByte();
							if( c != 30 ) {
								header += c;
							}
						} catch(Exception e ) {
							unregister(tag);
							return;
						}
					} while(c != 30);
					// System.out.println(header);
					//header is type<space>id<space>length
					final String[] parts = header.split(" ");
					int length = Integer.parseInt(parts[2]);
					byte[] data = new byte[length];

					int curr = 0;
					do {
						int read = dis.read(data,curr,length - curr);
						if( read != -1 ) {
							curr += read;
						} else {
							break;
						}
					} while( curr < length );
					final String message = new String(data);
					dis.readUnsignedByte(); //throw terminator
					(new Thread(new Runnable() {
						public void run() {
							processMessage(tag,parts[0],parts[1],message);
						}
					})).start();
				} catch( Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}