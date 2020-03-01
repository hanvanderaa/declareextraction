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
	ATMOSTONCE("Absence2"),
	EXISTENCE("Existence"),
	COEXISTENCE("Co-Existence"),
	RESPONDED_EXISTENCE("Responded Existence"),
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
