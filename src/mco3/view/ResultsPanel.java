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
	
	public ResultsPanel() throws SQLException {
		createTablePanel();
		this.add(tablePane);
	}
	public void createTablePanel()
	{
		tablePane = new JPanel();
	    tablePane.setLayout(new BorderLayout());
	}
	
	public JTable resultTable(ResultSet rs) throws SQLException
	{
		JTable table = new JTable(buildTableModel(rs));
	    table.setEnabled(false);

	    ColumnsAutoSizer.sizeColumnsToFit(table);
	    
	    //an event listener to automatically resize the columns every time data is added or changed in the table, like this
	    // table.getModel().addTableModelListener(new TableModelListener() {
	    //     public void tableChanged(TableModelEvent e) {
	    //         ColumnsAutoSizer.sizeColumnsToFit(table);
	    //     }
	    // });
	    return table;
	}
	
	public static DefaultTableModel buildTableModel(ResultSet rs)
	        throws SQLException {

	    ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();

	    // names of columns
	    Vector<String> columnNames = new Vector<String>();
	    int columnCount = metaData.getColumnCount();
	    for (int column = 1; column <= columnCount; column++) {
	        columnNames.add(metaData.getColumnName(column));
	    }

	    // data of the table
	    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	    while (rs.next()) {
	        Vector<Object> vector = new Vector<Object>();
	        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	            vector.add(rs.getObject(columnIndex));
	        }
	        data.add(vector);
	    }

	    return new DefaultTableModel(data, columnNames);

	}
}
