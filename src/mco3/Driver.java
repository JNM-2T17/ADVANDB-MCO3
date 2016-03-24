package mco3;

import mco3.controller.MCO3Controller;

public class Driver {
	public static final String SCHEMA = "db_hpq";

	public static void main(String[] args) throws Exception {
		new MCO3Controller();
	}
}