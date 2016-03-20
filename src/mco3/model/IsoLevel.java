package mco3.model;

public enum IsoLevel {
	READ_UNCOMMITTED(1),
	READ_COMMITTED(2),
	READ_REPEATABLE(3),
	SERIALIZABLE(4);

	private final int level;

	IsoLevel(int level) {
		this.level = level;
	}

	int level() {
		return level;
	}
}