package mco3.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mco3.controller.MCO3Controller;

public class ConnectScreen extends JFrame {
	public ConnectScreen(final MCO3Controller control) {
		setTitle("Connect to a Node");
		setSize(600,400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel content = new JPanel(new AGBLayout());
		String connection = "";
		switch(control.schema()) {
			case "db_hpq":
				connection = "Marinduque IP Address";
				break;
			case "db_hpq_palawan":
				connection = "Central IP Address";
				break;
			case "db_hpq_marinduque":
				connection = "Palawan IP Address";
				break;
			default:
		}
		JLabel ipLabel = new JLabel(connection);
		ipLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
		AGBLayout.addComp(content,ipLabel,0,0,1,1,0,0,GridBagConstraints.EAST
							,GridBagConstraints.NONE);

		final JTextField ipField = new JTextField(20);
		ipField.setFont(new Font("Segoe UI",Font.PLAIN,14));
		AGBLayout.addComp(content,ipField,1,0,1,1,0,0,GridBagConstraints.WEST
							,GridBagConstraints.NONE);

		JButton connectButton = new JButton("Connect");
		connectButton.setFont(new Font("Segoe UI",Font.PLAIN,14));
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				control.connect(ipField.getText());
			}
		});
		AGBLayout.addComp(content,connectButton,2,0,1,1,0,0,GridBagConstraints.CENTER
							,GridBagConstraints.BOTH);		

		add(content,BorderLayout.CENTER);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
