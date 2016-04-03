package mco3;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import mco3.controller.MCO3Controller;

public class DriverPal {
	public static final String SCHEMA = "db_hpq_palawan";

	public static void main(String[] args) throws Exception {
		try {
			for(LookAndFeelInfo lafi : UIManager.getInstalledLookAndFeels()) {
				if( "Nimbus".equals(lafi.getName())) {
					UIManager.setLookAndFeel(lafi.getClassName());
					break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		new MCO3Controller(SCHEMA);
	}
}