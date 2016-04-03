package mco3.model;

public enum IsoLevel {
	READ_UNCOMMITTED(1,"Read Uncommitted"),
	READ_COMMITTED(2,"Read Committed"),
	READ_REPEATABLE(3,"Repeatable Read"),
	SERIALIZABLE(4,"Serializable");

	private final int level;
	private final String string;

	IsoLevel(int level,String string) {
		this.level = level;
		this.string = string;
	}

	int level() {
		return level;
	}

	public String toString() {
		return string;
	}
}