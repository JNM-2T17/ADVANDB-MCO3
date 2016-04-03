package mco3.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DummyTransaction extends AbstractTransaction {
	public DummyTransaction(int id, IsoLevel isolation) throws SQLException {
		super(id,isolation);
	}

	public ReadResult read(String query,String[] columns,String[] params) {
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(query);
			for(int i = 0; i < params.length; i++) {
				ps.setString(i + 1, params[i]);
			}
			System.out.println(ps);
			ResultSet rs1 = ps.executeQuery();
			return new ReadResult(columns,rs1);
		} catch(Exception e) {
			e.printStackTrace();
			rollback();
			return null;
		}
	}

	public synchronized void write(String[] query) {
		try {
			PreparedStatement ps = con.prepareStatement(query[0]);
			for(int i = 1; i < query.length; i++) {
				ps.setString(i,query[i]);
			}
			// System.out.println(ps);
			ps.execute();
			ps.close();
			// ps = con.prepareStatement("SELECT alp_area FROM hpq_alp WHERE hpq_hh_id = 11328");
			// System.out.println(ps);
			// ResultSet rs = ps.executeQuery();
			// if(rs.next() ) {
			// 	System.out.println("AFTER WRITING ALP_AREA = " + rs.getInt("alp_area"));
			// } else {
			// 	System.out.println("NO DATA");
			// }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void lock(String stmt) {
		try {
			PreparedStatement ps = con.prepareStatement(stmt);
			// System.out.println(ps);
			ps.execute();
			ps.close();
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}

	public void releaseLocks() {
		try {
			PreparedStatement ps = con.prepareStatement("UNLOCK TABLES");
			// System.out.println(ps);
			ps.execute();
			ps.close();
		} catch(Exception e) {}	
	}
}