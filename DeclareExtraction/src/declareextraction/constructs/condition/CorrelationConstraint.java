package declareextraction.constructs.condition;

public class CorrelationConstraint extends ConditionalConstraint {

    private boolean isNegative;
    private String field;

    public CorrelationConstraint(boolean isNegative, String field) {
        this.isNegative = isNegative;
        this.field = field;
    }

    @Override
    public String toRuMString() {
        return isNegative ? "same " + field : "different " + field;
    }
}
