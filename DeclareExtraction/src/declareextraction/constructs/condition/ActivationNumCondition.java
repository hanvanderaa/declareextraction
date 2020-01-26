package declareextraction.constructs.condition;

public class ActivationNumCondition extends Condition {

    private String field;
    private ConditionRelation relation;
    private double value;

    public ActivationNumCondition(String field, ConditionRelation relation, double value) {
        super(ConditionType.ACTIVATION);
        this.field = field;
        this.relation = relation;
        this.value = value;
    }

    @Override
    public String toRuMString() {
        return "A." + field + ' ' + relation.conditionSymbol + ' ' + value;
    }

    public enum ConditionRelation {
        GREATER(">"),
        GREATER_EQUAL(">="),
        LOWER("<"),
        LOWER_EQUAL("<="),
        EQUALS("="),
        NOT_EQUAL("!=");

        private String conditionSymbol;

        ConditionRelation(String conditionSymbol) {
            this.conditionSymbol = conditionSymbol;
        }
    }
}
