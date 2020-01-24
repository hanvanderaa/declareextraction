package declareextraction.constructs.condition;

public class ActivationNumConstraint extends ConditionalConstraint {

    private String field;
    private ConditionRelation relation;
    private double value;

    public ActivationNumConstraint(String field, ConditionRelation relation, double value) {
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
        /*NOT_EQUAL("!=")*/; //TODO: is this one in? what format?

        private String conditionSymbol;

        ConditionRelation(String conditionSymbol) {
            this.conditionSymbol = conditionSymbol;
        }
    }
}
