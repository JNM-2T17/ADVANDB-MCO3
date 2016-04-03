package mco3.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import mco3.controller.MCO3Controller;
import mco3.model.*;

public class AddTransactionPanel extends JPanel {
	private int transactionNo;

	private JPanel typePanel;
	private ButtonGroup typeGroup;
	private JRadioButton[] typeRadios;
	
	private JPanel isoPanel;
	private ButtonGroup isoGroup;
	private JRadioButton[] isoRadios;
	
	private JPanel abortPanel;
	private ButtonGroup abortGroup;
	private JRadioButton[] abortRadios;

	private JPanel inputPanel;
	private JLabel idLabel;
	private JTextField idField;
	private JLabel incrementLabel;
	private JTextField incrementField;
	private JLabel tenurLabel;
	private JComboBox<String> tenurBox;

	private JPanel optionsPanel;
	private JButton addButton;
	private JButton cancelButton;

	private MCO3Controller control;

	public AddTransactionPanel(MCO3Controller controller) {
		super(new AGBLayout());
		this.control = controller;
		transactionNo = 1;

		typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		typePanel.setBorder(BorderFactory.createTitledBorder("Transaction Type"));
		typeGroup = new ButtonGroup();
		typeRadios = new JRadioButton[3];
		String[] types = new String[] {
			"Read Alp Area OLAP",
			"Update Alp Area",
			"Delete Alp Area"
		};
		for(int i = 0; i < types.length; i++ ) {
			typeRadios[i] = new JRadioButton(types[i]);
			typeGroup.add(typeRadios[i]);
			typePanel.add(typeRadios[i]);
			typeRadios[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int i = 0;
					while( i < typeRadios.length ) {
						if( typeRadios[i] == e.getSource() ) {
							break;
						}
						i++;
					}
					updateInput(i);
				}
			});
		}
		typeRadios[0].setSelected(true);
		Dimension typeDim = typePanel.getPreferredSize();
		typeDim.width = 290;
		typeDim.height = 100;
		typePanel.setPreferredSize(typeDim);
		AGBLayout.addComp(this,typePanel,0,0,1,1,0,0,GridBagConstraints.CENTER
							,GridBagConstraints.BOTH);

		isoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		isoPanel.setBorder(BorderFactory.createTitledBorder("Isolation Level"));
		isoGroup = new ButtonGroup();
		isoRadios = new JRadioButton[4];
		String[] isos = new String[] {
			"Read Uncommitted",
			"Read Committed",
			"Repeatable Read",
			"Serializable"
		};
		for(int i = 0; i < isos.length; i++ ) {
			isoRadios[i] = new JRadioButton(isos[i]);
			isoGroup.add(isoRadios[i]);
			isoPanel.add(isoRadios[i]);
		}
		isoRadios[0].setSelected(true);
		Dimension isoDim = isoPanel.getPreferredSize();
		isoDim.width = 290;
		isoDim.height = 100;
		isoPanel.setPreferredSize(isoDim);
		AGBLayout.addComp(this,isoPanel,1,0,1,1,0,0,GridBagConstraints.CENTER
							,GridBagConstraints.BOTH);

		abortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		abortPanel.setBorder(BorderFactory.createTitledBorder("Abort Schedule"));
		abortGroup = new ButtonGroup();
		abortRadios = new JRadioButton[3];
		String[] aborts = new String[] {
			"Never Abort",
			"Abort Before Commit",
			"Fail Node Before Commit"
		};
		for(int i = 0; i < aborts.length; i++ ) {
			abortRadios[i] = new JRadioButton(aborts[i]);
			abortGroup.add(abortRadios[i]);
			abortPanel.add(abortRadios[i]);
		}
		abortRadios[0].setSelected(true);
		Dimension abortDim = abortPanel.getPreferredSize();
		abortDim.width = 290;
		abortDim.height = 75;
		abortPanel.setPreferredSize(abortDim);
		AGBLayout.addComp(this,abortPanel,0,1,2,1,0,0,GridBagConstraints.CENTER
							,GridBagConstraints.BOTH);

		inputPanel = new JPanel(new AGBLayout());
		idLabel = new JLabel("ID: ");
		AGBLayout.addComp(inputPanel,idLabel,0,0,1,1,0,0,GridBagConstraints.EAST
							,GridBagConstraints.NONE);
		idField = new JTextField(20);
		AGBLayout.addComp(inputPanel,idField,1,0,1,1,0,0,GridBagConstraints.WEST
							,GridBagConstraints.NONE);
		incrementLabel = new JLabel("Land Increment: ");
		AGBLayout.addComp(inputPanel,incrementLabel,0,1,1,1,0,0
							,GridBagConstraints.EAST
							,GridBagConstraints.NONE);
		incrementField = new JTextField(20);
		AGBLayout.addComp(inputPanel,incrementField,1,1,1,1,0,0
							,GridBagConstraints.WEST
							,GridBagConstraints.NONE);
		tenurLabel = new JLabel("Owner Tenure");
		AGBLayout.addComp(inputPanel,tenurLabel,0,2,1,1,0,0
							,GridBagConstraints.EAST
							,GridBagConstraints.NONE);
		tenurBox = new JComboBox<String>(new String[] {
			"Owner, owner-like possession of house and lot",
			"Rent house/room including lot",
			"Own house, rent lot",
			"Own house, rent-free lot with consent of owner",
			"Own house, rent-free lot without consent of owner",
			"Rent-free house and lot with consent of owner",
			"Rent-free house and lot without consent of owner",
			"Living in a public space with rent",
			"Living in a public space without rent",
			"Other tenure status"
		});
		AGBLayout.addComp(inputPanel,tenurBox,1,2,1,1,0,0
							,GridBagConstraints.WEST
							,GridBagConstraints.NONE);
		Dimension inputDim = inputPanel.getPreferredSize();
		inputDim.width = 290;
		inputDim.height = 100;
		inputPanel.setPreferredSize(inputDim);
		AGBLayout.addComp(this,inputPanel,0,2,2,1,0,0,GridBagConstraints.CENTER
							,GridBagConstraints.BOTH);		
		updateInput(0);
		
		optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		addButton = new JButton("Add Transaction");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processInput();
			}
		});
		optionsPanel.add(addButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				control.setMain(MCO3Controller.RUN);
			}
		});
		optionsPanel.add(cancelButton);
		AGBLayout.addComp(this,optionsPanel,0,3,2,1,0,0,GridBagConstraints.CENTER
							,GridBagConstraints.BOTH);		
	}

	public void processInput() {
		int type = 0;
		for(type = 0; type < typeRadios.length; type++ ) {
			if( typeRadios[type].isSelected() ) {
				break;
			}
		}

		IsoLevel il = IsoLevel.SERIALIZABLE;
		for(int i = 0; i < isoRadios.length; i++ ) {
			if( isoRadios[i].isSelected() ) {
				switch(i) {
					case 0:
						il = IsoLevel.READ_UNCOMMITTED;
						break;
					case 1:
						il = IsoLevel.READ_COMMITTED;
						break;
					case 2:
						il = IsoLevel.READ_REPEATABLE;
						break;
					case 3:
						il = IsoLevel.SERIALIZABLE;
						break;
					default:
				}
				break;
			}
		}

		int abort = AbstractTransaction.ABORT_NEVER;
		for(int i = 0; i < abortRadios.length; i++ ) {
			if( abortRadios[i].isSelected() ) {
				switch(i) {
					case 0:
						abort = AbstractTransaction.ABORT_NEVER;
						break;
					case 1:
						abort = AbstractTransaction.ABORT_AFTER;
						break;
					case 2:
						abort = AbstractTransaction.FAIL_AFTER;
						break;
					default:
				}
				break;
			}
		}

		Transaction t = null;
		String error = "";

		switch(type) {
			case 0:
				int tenur = tenurBox.getSelectedIndex() + 1;
				try {
					t = new ReadDensity2(transactionNo++,il,abort,tenur);
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;
			case 1:
				int id = 1,increment = 0;
				try {
					id = Integer.parseInt(idField.getText());
					if(id <= 0) {
						error += "Id must be a positive number.";	
					}
				} catch(NumberFormatException nfe) {
					error += "Id must be a positive number.";
				}
				try {
					increment = Integer.parseInt(incrementField.getText());
				} catch(NumberFormatException nfe) {
					error += (error.length() == 0 ? "" : "\n") 
								+ "Increment must be an integer.";
				}
				if( error.length() > 0 ) {
					JOptionPane.showMessageDialog(null,error,"Error"
											,JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						t = new EditAlp(transactionNo++,il,abort,id
									,increment);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case 2:
				id = 1;
				try {
					id = Integer.parseInt(idField.getText());
					if(id <= 0) {
						error += "Id must be a positive number.";	
					}
				} catch(NumberFormatException nfe) {
					error += "Id must be a positive number.";
				}
				if( error.length() > 0 ) {
					JOptionPane.showMessageDialog(null,error,"Error"
											,JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						t = new DeleteAlp(transactionNo++,il,abort,id);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}

				break;
			default:
		}
		if( t != null ) {
			control.addTransaction(t);
			reset();
		}
	}

	public void reset() {
		typeRadios[0].setSelected(true);
		isoRadios[0].setSelected(true);
		abortRadios[0].setSelected(true);
		idField.setText("");
		incrementField.setText("");
		tenurBox.setSelectedIndex(0);
		updateInput(0);
	}

	public void updateInput(int index) {
		switch(index) {
			case 0:
				idLabel.setVisible(false);
				idField.setVisible(false);
				incrementLabel.setVisible(false);
				incrementField.setVisible(false);
				tenurLabel.setVisible(true);
				tenurBox.setVisible(true);
				break;
			case 1:
				idLabel.setVisible(true);
				idField.setVisible(true);
				incrementLabel.setVisible(true);
				incrementField.setVisible(true);
				tenurLabel.setVisible(false);
				tenurBox.setVisible(false);
				break;
			case 2:
				idLabel.setVisible(true);
				idField.setVisible(true);
				incrementLabel.setVisible(false);
				incrementField.setVisible(false);
				tenurLabel.setVisible(false);
				tenurBox.setVisible(false);
				break;
			default:
		}
	}
}