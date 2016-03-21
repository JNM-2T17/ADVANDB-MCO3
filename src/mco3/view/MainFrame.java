package mco3.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MainFrame extends JFrame {
	private JPanel mainContainer;
	private JPanel mainPanel;
	private JPanel southPanel;
	private JPanel eastPanel;
	
	public MainFrame() {
		setTitle( "ADVANDB MCO3" );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		
		mainContainer = new JPanel( new BorderLayout() );
		
		mainPanel = new JPanel();
		//mainPanel.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
		mainContainer.add( mainPanel, BorderLayout.CENTER );
		
		southPanel = new JPanel();
		//southPanel.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
		mainContainer.add( southPanel, BorderLayout.SOUTH );
		
		add( mainContainer, BorderLayout.CENTER );
		
		eastPanel = new JPanel();
		add( eastPanel, BorderLayout.EAST );
		
		center();
		setVisible( true );
	}
	
	public void center() {
		setSize( 700, 500 );
		setLocationRelativeTo( null );
	}
	
	public void setMain( JPanel panel ) {
		mainPanel.removeAll();
		mainPanel.add( panel );
		mainPanel.repaint();
		mainPanel.revalidate();
		center();
	}
	
	public void setSouth( JPanel panel ) {
		southPanel.removeAll();
		southPanel.add( panel );
		southPanel.repaint();
		southPanel.revalidate();
		center();
	}
	
	public void setEast( JPanel panel ) {
		eastPanel.removeAll();
		eastPanel.add( panel );
		eastPanel.repaint();
		eastPanel.revalidate();
		center();
	}
}
