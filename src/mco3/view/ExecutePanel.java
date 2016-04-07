package mco3.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import mco3.controller.MCO3Controller;
import mco3.model.Transaction;

public class ExecutePanel extends JPanel implements Updatable {
	private List<Transaction> model;

	private JButton addButton;
	private JButton runLocalButton;
	private JButton runGlobalButton;
	private JPanel transPanel;

	private MCO3Controller control;

	public ExecutePanel(MCO3Controller controller,List<Transaction> model) {
		super(new BorderLayout());
		this.model = model;
		this.control = controller;

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		addButton = new JButton("Add Transaction");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				control.setMain(MCO3Controller.ADD);
			}
		});
		buttonPanel.add(addButton);

		runLocalButton = new JButton("Run Local Transactions");
		runLocalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				(new Thread() {
					public void run() {
						control.runAll();
					}
				}).start();
			}
		});
		buttonPanel.add(runLocalButton);

		runGlobalButton = new JButton("Run Global Transactions");
		runGlobalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				(new Thread() {
					public void run() {
						control.syncAll();
					}
				}).start();
			}
		});
		buttonPanel.add(runGlobalButton);

		add(buttonPanel,BorderLayout.NORTH);

		transPanel = new JPanel(new AGBLayout());
		add(transPanel,BorderLayout.CENTER);
		update();
	}

	public void update() {
		transPanel.removeAll();
		for(int i = 0; i < model.size(); i++) {
			TranPanel tp = new TranPanel(model.get(i));
			model.get(i).setView(tp);
			AGBLayout.addComp(transPanel,tp,0,i,1,1,0,0
								,GridBagConstraints.CENTER
								,GridBagConstraints.BOTH);
		}
		transPanel.repaint();
		transPanel.revalidate();
	}

	public void finishPrompt() {
		JOptionPane.showMessageDialog(null,"Execution Finished"
										,"Execution Finished"
										,JOptionPane.INFORMATION_MESSAGE);
	}

	class TranPanel extends JPanel implements Updatable {
		private Transaction model;
		private JLabel numberLabel;
		private JLabel typeLabel;
		private JLabel isolationLabel;
		private JProgressBar progress;
		private JButton deleteButton;

		public TranPanel(Transaction _model) {
			super(new FlowLayout(FlowLayout.LEFT));
			this.model = _model;

			numberLabel = new JLabel("");
			add(numberLabel);

			typeLabel = new JLabel("");
			add(typeLabel);

			isolationLabel = new JLabel("");
			add(isolationLabel);

			progress = new JProgressBar(0,model.size());
			progress.setStringPainted(true);
			add(progress);

			deleteButton = new JButton("Delete");
			deleteButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					control.delete(model);
				}
			});
			add(deleteButton);

			update();
		}

		public void update() {
			numberLabel.setText("Transaction #" + model.transactionId());
			typeLabel.setText(model.toString());
			isolationLabel.setText(model.isoLevel().toString());
			progress.setString(model.status());
			progress.setValue(model.position());
		}
	}
}