package mco3.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import mco3.view.ConStatusPanel;

public class ConnectionManager {
	private static ConnectionManager instance;

	private String schema;
	private HashMap<String,Socket> sockets;
	private Receiver r;
	private ConStatusPanel csPanel;
	private boolean[] status;

	private ConnectionManager(String schema,ConStatusPanel csPanel) {
		this.csPanel = csPanel;
		sockets = new HashMap<String,Socket>();
		this.schema = schema;
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

	public static synchronized ConnectionManager instance(String schema,ConStatusPanel csPanel) {
		if( instance == null ) {
			instance = new ConnectionManager(schema,csPanel);
		}
		return instance;
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
		} catch( Exception e) {
			e.printStackTrace();
		}
	}

	public void register(String tag, Socket registree) {
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
		//start Listener thread
	}

	public void sendMessage(String tag, String message) {
		
	}

	private class Receiver extends Thread {
		private ServerSocket ss;
		private String tag;

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
					System.out.println(message + " from " + s.getInetAddress().getHostAddress());
					if( message.equals("CONNECT")) {
						DataOutputStream dos = new DataOutputStream(s.getOutputStream());
						dos.writeBytes("OK" + (char)4);
						register(tag,s);
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
	}
}