package declareextraction.constructs;

public enum ConstraintType {

	PRECEDENCE, ALTPRECEDENCE, RESPONSE, SUCCESSION, INIT, END, ATMOSTONCE, EXISTENCE;
	
	public static ConstraintType getType(String s) {
		for (ConstraintType t : ConstraintType.values()) {
			if (t.toString().equalsIgnoreCase(s)) {
				return t;
			}
		}
		return null;
	}
}
