package mco3.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class ReadResult {
	private String[] columns;
	private ArrayList<HashMap<String,String>> data;

	public ReadResult(String[] cols,ResultSet results) {
		columns = cols;
		data = new ArrayList<HashMap<String,String>>();

		try {
			while(results.next()) {
				HashMap<String,String> map = new HashMap<String,String>();
				for(String s : columns) {
					map.put(s,results.getString(s));
				}
				data.add(map);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public ReadResult(String str) {
		String[] rows = str.split("" + (char)30);
		columns = rows[0].split("" + (char)31);
		data = new ArrayList<HashMap<String,String>>();
		
		for(int i = 1; i < rows.length; i++) {
			HashMap<String,String> map = new HashMap<String,String>();
			String[] data = rows[i].split("" + (char)31);
			for(int j = 0; j < columns.length; j++) {
				map.put(columns[j],data[j]);
			}
			this.data.add(map);
		}
	}

	public int size() {
		return data.size();
	}

	public int colCtr() {
		return columns.length;
	}

	public String column(int index) {
		return columns[index];
	}

	public String get(int row, String column) {
		return data.get(row).get(column);
	}

	public String toString() {
		String ret = "";
		for(int i = 0; i < columns.length; i++) {
			if( i > 0 ) {
				ret += (char)31;
			}
			ret += columns[i];
		}
		for(int i = 0; i < data.size(); i++ ) {
			ret += (char)30;
			for(int j = 0; j < columns.length; j++) {
				if( j > 0 ) {
					ret += (char)31;
				}
				ret += data.get(i).get(columns[j]);
			}	
		}

		return ret;
	}
}