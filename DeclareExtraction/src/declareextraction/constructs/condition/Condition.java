package declareextraction.constructs.condition;

public abstract class Condition {

    protected ConditionType type;

    Condition(ConditionType type) {
        this.type = type;
    }

    public abstract String toRuMString();

    public enum ConditionType {
        ACTIVATION,
        CORRELATION,
        TIME,
    }
}
