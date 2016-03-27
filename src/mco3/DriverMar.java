package mco3;

import mco3.controller.MCO3Controller;

public class DriverMar {
	public static final String SCHEMA = "db_hpq_marinduque";

	public static void main(String[] args) throws Exception {
		new MCO3Controller(SCHEMA);
	}
}