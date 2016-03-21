package mco3.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mco3.controller.*;
import mco3.model.*;

public class ConcurrencyPanel extends JPanel implements Updatable {
	private List<Transaction> model;
	private MCO3Controller control;

	public ConcurrencyPanel(List<Transaction> model,MCO3Controller control) {
		super(new BorderLayout());
		this.model = model;
		this.control = control;

		update();
	}

	public void update() {
		Box trans = Box.createHorizontalBox();
		for(Transaction t : model) {
			TransactionPanel tp = new TransactionPanel(t,control);
			t.setView(tp);
			trans.add(tp);
			trans.add(Box.createHorizontalStrut(10));
		}
		removeAll();
		JScrollPane transactions 
			= new JScrollPane(trans,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
								JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		Dimension d = transactions.getPreferredSize();
		d.width = 650;
		d.height = 450;
		transactions.setPreferredSize( d );
		add(transactions,BorderLayout.CENTER);
		repaint();
		revalidate();
	}
}