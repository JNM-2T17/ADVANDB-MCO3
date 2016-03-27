package mco3.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
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
		(new Listener(tag,registree)).start();
	}

	public void unregister(String tag) {
		sockets.remove(tag);
		switch(tag) {
			case "db_hpq":
				status[0] = false;
				break;
			case "db_hpq_marinduque":
				status[1] = false;
				break;
			case "db_hpq_palawan":
				status[2] = false;
				break;	
			default:
		}
		csPanel.setModel(status[0],status[1],status[2]);
		r.wakeUp();
	}

	public void sendMessage(String tag, String message, String replyHeader) {
		Socket s = sockets.get(tag);
		try {
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeBytes(message);
			//register replyHeader
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public void processMessage(String tag, String header, String message) {

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
					System.out.println(header);
					//header is type<space>id<space>length
					String[] parts = header.split(" ");
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
					String message = new String(data);
					processMessage(tag,parts[0] + " " + parts[1],message);
				} catch( Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}