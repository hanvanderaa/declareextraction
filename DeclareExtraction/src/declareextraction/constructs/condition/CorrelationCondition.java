package declareextraction.constructs.condition;

public class CorrelationCondition extends Condition {

    private boolean isNegative;
    private String field;

    public CorrelationCondition(boolean isNegative, String field) {
        super(ConditionType.CORRELATION);
        this.isNegative = isNegative;
        this.field = field;
    }

    @Override
    public String toRuMString() {
        return isNegative ? "different " + field : "same " + field;
    }
}
