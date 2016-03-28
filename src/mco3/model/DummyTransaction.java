package mco3.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DummyTransaction extends AbstractTransaction {
	public DummyTransaction(int id, IsoLevel isolation) throws SQLException {
		super(id,isolation);
	}

	public void read(String query) {

	}

	public void write(String[] query) {
		try {
			PreparedStatement ps = con.prepareStatement(query[0]);
			for(int i = 1; i < query.length; i++) {
				ps.setString(i,query[i]);
			}
			System.out.println(ps);
			ps.execute();
			ps.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void lock(String stmt) {
		try {
			PreparedStatement ps = con.prepareStatement(stmt);
			ps.execute();
			ps.close();
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}

	public void releaseLocks() {
		try {
			PreparedStatement ps = con.prepareStatement("UNLOCK TABLES");
			ps.execute();
			ps.close();
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
}