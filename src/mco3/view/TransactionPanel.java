package mco3.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import mco3.controller.MCO3Controller;
import mco3.model.Transaction;

public class TransactionPanel extends JPanel implements Updatable{
	private MCO3Controller control;

	private Transaction model;
	private JButton stepButton;
	private JButton rollbackButton;
	private Box buttonPanel;
	private JScrollPane stepsScroll;

	public TransactionPanel(Transaction tModel, MCO3Controller controller ) {
		super(new BorderLayout());
		
		this.model = tModel;
		this.control = controller; 

		buttonPanel = Box.createVerticalBox();
		stepButton = new JButton("Step");
		stepButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				control.step(model);
			}
		});
		buttonPanel.add(stepButton);
		rollbackButton = new JButton("Rollback");
		rollbackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				control.rollback(model);
			}
		});
		buttonPanel.add(rollbackButton);
		rollbackButton.setVisible(false);
		update();
	}

	public void update() {
		if( !model.status().equals(model.NOT_STARTED) ) {
			rollbackButton.setVisible(true);
		}
		if( model.isFinished() ) {
			rollbackButton.setVisible(false);
			stepButton.setText("Remove Transaction");
		}
		Box content = Box.createVerticalBox();
		JLabel statusLabel = new JLabel(model.status() + " " 
										+ model.transactionId());
		content.add(statusLabel);
		// content.add(Box.createVerticalStrut(5));
		JLabel tsLabel = new JLabel("Timestamp: " + model.timestamp());
		content.add(tsLabel);
		// content.add(Box.createVerticalStrut(5));
		content.add(buttonPanel);
		// content.add(Box.createVerticalStrut(5));
		Box steps = Box.createVerticalBox();
		
		for(int i = 0; i < model.size(); i++ ) {
			
			JLabel stepLabel = new JLabel(model.getStep(i).toString());
			if( i == model.position() ) {
				stepLabel.setBorder(
					new javax.swing.border.CompoundBorder(
						BorderFactory.createLineBorder(Color.BLACK)
						,BorderFactory.createEmptyBorder(10,10,10,10)));
			} else {
				stepLabel.setBorder(
					BorderFactory.createEmptyBorder(5,10,5,10));
			}

			steps.add(stepLabel);
		}
		int val = stepsScroll == null ? val = 0 
					: stepsScroll.getVerticalScrollBar().getValue();
		stepsScroll 
			= new JScrollPane(steps,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
								JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		Dimension d = stepsScroll.getPreferredSize();
		d.width = 150;
		d.height = 300;
		stepsScroll.setPreferredSize( d );
		stepsScroll.getVerticalScrollBar().setValue(val);
		content.add(stepsScroll);

		removeAll();
		add(content,BorderLayout.CENTER);
		repaint();
		revalidate();
	}
}