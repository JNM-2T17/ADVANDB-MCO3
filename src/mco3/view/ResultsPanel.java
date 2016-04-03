package mco3.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import mco3.model.ReadResult;

public class ResultsPanel extends JPanel {
	
	public static Connection conn;
	
	private JTextArea queryDisplayer;
	private JLabel timeDisplayer;
	private JButton btnSummary;
	private JPanel tablePane;
	private ArrayList<String> times;
	
	public void setTimes(ArrayList<String> times){
		this.times = times;
	}
	
	public ResultsPanel(ReadResult rs) {
		createTablePanel();
		this.add(tablePane);
		resultTable(rs);
	}

	public void createTablePanel()
	{
		tablePane = new JPanel();
	    tablePane.setLayout(new BorderLayout());
	}
	
	public void resultTable(ReadResult rs) 
	{
		try {
			JTable table = new JTable(buildTableModel(rs));
		    table.setEnabled(false);

		    ColumnsAutoSizer.sizeColumnsToFit(table);
		    
		   	tablePane.add(new JScrollPane(table
		   					,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
		   					,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
		   				,BorderLayout.CENTER);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public DefaultTableModel buildTableModel(ReadResult rs) {

	     // names of columns
	    Vector<String> columnNames = new Vector<String>();
	    int columnCount = rs.colCtr();
	    for (int column = 0; column < columnCount; column++) {
	        columnNames.add(rs.column(column));
	    }

	    // data of the table
	    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	    for(int i = 0; i < rs.size(); i++ ){
	        Vector<Object> vector = new Vector<Object>();
	        for (int j = 0; j < rs.colCtr(); j++) {
	            vector.add(rs.get(i,rs.column(j)));
	        }
	        data.add(vector);
	    }

	    return new DefaultTableModel(data, columnNames);

	}
}
