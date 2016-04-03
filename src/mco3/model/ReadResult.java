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

	private ReadResult(String[] cols) {
		columns = cols;
		data = new ArrayList<HashMap<String,String>>();		
	}

	private void addMap(HashMap<String,String> map) {
		data.add(map);
	}

	private ArrayList<HashMap<String,String>> getData() {
		return data;
	}

	private int compare(int index1, int index2, int[] colIndex) {
		HashMap<String,String> map1 = data.get(index1);
		HashMap<String,String> map2 = data.get(index2);

		for(int i = 0; i < colIndex.length; i++) {
			String col = columns[i];
			int cmp = map1.get(col).compareTo(map2.get(col));
			if( cmp != 0 ) {
				return cmp;
			}
		}
		return 0;
	}

	public ReadResult merge(ReadResult rr2, int[] groupByCols) {
		ReadResult temp = new ReadResult(columns);
		for(HashMap<String,String> map : data) {
			temp.addMap(map);
		}
		for(HashMap<String,String> map : rr2.getData()) {
			temp.addMap(map);
		}
		temp.merge(groupByCols);
		return temp;
	}

	private void merge(int[] groupByCols) {
		//sort
		for(int i = size() - 1; i > 0; i-- ) {
			for(int j = 0; j < i; j++ ) {
				if( compare(i, j, groupByCols) < 0) {
					HashMap<String,String> temp = data.get(i);
					data.set(i, data.get(j));
					data.set(j, temp);
				}
			}
		}

		//merge
		for(int i = 0; i < size() - 1; ) {
			if( compare(i, i + 1, groupByCols) == 0 ) {
				HashMap<String,String> temp = data.get(i);
				HashMap<String,String> temp2 = data.get(i + 1);

				//for each column not in groupByCols, add
				for(int j = 0; j < columns.length; j++) {
					boolean isGB = false;
					for( int x : groupByCols) {
						if( j == x ) {
							isGB = true;
							break;
						}
					}
					if( !isGB ) {
						String column = columns[j];
						double col = Double.parseDouble(temp.get(column));
						double col2 = Double.parseDouble(temp2.get(column));
						col += col2;
						temp.put(column, "" + (int)col);
					}
				}

				data.remove(i + 1);
			} else {
				i++;
			}
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