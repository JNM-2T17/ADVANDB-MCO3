package mco3.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

import mco3.model.ReadResult;

public class ResultsFrame extends JFrame {
	public ResultsFrame(ReadResult rr) {
		setTitle("Read Results");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel result = new ResultsPanel(rr);
		Dimension d = result.getPreferredSize();
		setSize(500,500);
		add(result,BorderLayout.CENTER);
		setLocationRelativeTo(null);
		System.out.println(rr);


		setVisible(true);
	}
}