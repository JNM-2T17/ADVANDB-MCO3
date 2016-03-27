package mco3.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import mco3.controller.MCO3Controller;	

public class MCO3Menu extends JMenuBar {
	private MCO3Controller control;

	public MCO3Menu(MCO3Controller controller) {
		this.control = controller;

		JMenu transactions = new JMenu("Transactions");
		JMenuItem add = new JMenuItem("Add Transaction");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				control.setMain(MCO3Controller.ADD);
			}
		});
		transactions.add(add);
		JMenuItem run = new JMenuItem("Run Transaction");
		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				control.setMain(MCO3Controller.RUN);
			}
		});
		transactions.add(run);
		add(transactions);

		JMenu connect = new JMenu("Connections");
		JMenuItem con = new JMenuItem("Connect to Node");
		con.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				control.connectScreen();
			}
		});
		connect.add(con);
		add(connect);

	}
}