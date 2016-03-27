package mco3.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import javax.swing.JLabel;

public class ConStatusPanel extends JPanel {
	private JLabel centralLabel;
	private JLabel marinLabel;
	private JLabel palawanLabel;

	public ConStatusPanel(String schema) {
		super(new AGBLayout());
		JLabel centLabel = new JLabel("Central: ");
		centLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
		AGBLayout.addComp(this,centLabel,0,0,1,1,0,0,GridBagConstraints.EAST
							,GridBagConstraints.NONE);
		centralLabel = new JLabel("");
		centralLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
		AGBLayout.addComp(this,centralLabel,1,0,1,1,0,0,GridBagConstraints.WEST
							,GridBagConstraints.NONE);

		JLabel marLabel = new JLabel("Marinduque: ");
		marLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
		AGBLayout.addComp(this,marLabel,0,1,1,1,0,0,GridBagConstraints.EAST
							,GridBagConstraints.NONE);
		marinLabel = new JLabel("");
		marinLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
		AGBLayout.addComp(this,marinLabel,1,1,1,1,0,0,GridBagConstraints.WEST
							,GridBagConstraints.NONE);

		JLabel palLabel = new JLabel("Palawan: ");
		palLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
		AGBLayout.addComp(this,palLabel,0,2,1,1,0,0,GridBagConstraints.EAST
							,GridBagConstraints.NONE);
		palawanLabel = new JLabel("");
		palawanLabel.setFont(new Font("Segoe UI",Font.PLAIN,14));
		AGBLayout.addComp(this,palawanLabel,1,2,1,1,0,0,GridBagConstraints.WEST
							,GridBagConstraints.NONE);

		switch(schema) {
			case "db_hpq":
				setModel(true,false,false);
				break;
			case "db_hpq_marinduque":
				setModel(false,true,false);
				break;
			case "db_hpq_palawan":
				setModel(false,false,true);
				break;	
			default:
		}
	}

	public void setModel(boolean central,boolean marin,boolean palawan) {
		if( central ) {
			centralLabel.setForeground(Color.GREEN);
			centralLabel.setText("Connected");
		} else {
			centralLabel.setForeground(Color.RED);
			centralLabel.setText("Not Connected");
		}

		if( marin ) {
			marinLabel.setForeground(Color.GREEN);
			marinLabel.setText("Connected");
		} else {
			marinLabel.setForeground(Color.RED);
			marinLabel.setText("Not Connected");
		}

		if( palawan ) {
			palawanLabel.setForeground(Color.GREEN);
			palawanLabel.setText("Connected");
		} else {
			palawanLabel.setForeground(Color.RED);
			palawanLabel.setText("Not Connected");
		}
	}
}