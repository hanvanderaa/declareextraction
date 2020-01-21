package declareextraction.constructs;

public enum ConditionRelation {
    GREATER(">"),
    LOWER("<"),
    /*GREATER_EQUAL SMALLER_EQUAL NOT_EQUAL*/
    EQUALS("=");

    private String conditionSymbol;

    ConditionRelation(String conditionSymbol) {
        this.conditionSymbol = conditionSymbol;
    }

    public String getConditionSymbol() {
        return conditionSymbol;
    }
}