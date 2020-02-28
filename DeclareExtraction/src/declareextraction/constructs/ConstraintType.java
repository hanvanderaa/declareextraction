package declareextraction.constructs;

public enum ConstraintType {

	PRECEDENCE("Precedence"),
	RESPONSE("Response"),
	SUCCESSION("Succession"),
	ALTERNATE_PRECEDENCE("Alternate Precedence"),
	ALTERNATE_RESPONSE("Alternate Response"),
	ALTERNATE_SUCCESSION("Alternate Succession"),
	CHAIN_PRECEDENCE("Chain Precedence"),
	CHAIN_RESPONSE("Chain Response"),
	CHAIN_SUCCESSION("Chain Succession"),
	INIT("Init"),
	END("End"),
	ATMOSTONCE("At most once"),
	EXISTENCE("Existence"),
	COEXISTENCE("Co-existence"),
	RESPONDED_EXISTENCE("Responded existence"),
	ABSENCE("Absence");

	private final String constraintName;

	ConstraintType (String constraintName) {
		this.constraintName = constraintName;
	}

	public String getConstraintName() {
		return constraintName;
	}

	public static ConstraintType getType(String s) {
		for (ConstraintType t : ConstraintType.values()) {
			if (t.toString().equalsIgnoreCase(s)) {
				return t;
			}
		}
		return null;
	}
}
