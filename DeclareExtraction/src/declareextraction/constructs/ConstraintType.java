package declareextraction.constructs;

public enum ConstraintType {

	//DeclareConstraints
	PRECEDENCE("Precedence"),
	/*ALTPRECEDENCE,*/
	RESPONSE("Response"),
	SUCCESSION("Succession"),
	CHAINPRECEDENCE("Chain Precedence"),
	CHAINRESPONSE("Chain Response"),
	CHAINSUCCESSION("Chain Succession"),
	INIT("Init"),
	END("End"),
	/*ATMOSTONCE,*/
	EXISTENCE("Existence"),
	ABSENCE("Absence"),

	//ConditionalConstraints
	ACTIVATION("Activation"),
	CORRELATION("Correlation"),
	TIME("Time");

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
