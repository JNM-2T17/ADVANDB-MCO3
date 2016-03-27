package mco3;

import mco3.controller.MCO3Controller;

public class DriverPal {
	public static final String SCHEMA = "db_hpq_palawan";

	public static void main(String[] args) throws Exception {
		new MCO3Controller(SCHEMA);
	}
}