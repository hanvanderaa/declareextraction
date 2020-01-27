package declareextraction.constructs;

import declareextraction.constructs.condition.ActivationCondition;
import declareextraction.constructs.condition.CorrelationCondition;
import declareextraction.constructs.condition.TimeCondition;

public class DeclareConstraint {

    ConstraintType type;
    Action actionA;
    Action actionB;
    boolean isNegative = false;
    private ActivationCondition activationCondition;
    private CorrelationCondition correlationCondition;
    private TimeCondition timeCondition;

    public DeclareConstraint(ConstraintType type, Action actionA) {
        this.type = type;
        this.actionA = actionA;
    }

    public DeclareConstraint(ConstraintType type, Action actionA, Action actionB) {
        this.type = type;
        this.actionA = actionA;
        this.actionB = actionB;
    }

    public DeclareConstraint(ConstraintType type, Action actionA, Action actionB, boolean isNegative) {
        this(type, actionA, actionB);
        this.isNegative = isNegative;
    }

    public int size() {
        int n = 2;
        if (actionB != null) {
            n++;
        }
        if (isNegative) {
            n++;
        }
        return n;
    }

    public ConstraintType getType() {
        return type;
    }

    public Action getActionA() {
        return actionA;
    }

    public Action getActionB() {
        return actionB;
    }

    public void setNegative() {
        this.isNegative = true;
    }

    public boolean isNegative() {
        return isNegative;
    }

    public ActivationCondition getActivationCondition() {
        return activationCondition;
    }

    public void setActivationCondition(ActivationCondition activationCondition) {
        this.activationCondition = activationCondition;
    }

    public CorrelationCondition getCorrelationCondition() {
        return correlationCondition;
    }

    public void setCorrelationCondition(CorrelationCondition correlationCondition) {
        this.correlationCondition = correlationCondition;
    }

    public TimeCondition getTimeCondition() {
        return timeCondition;
    }

    public void setTimeCondition(TimeCondition timeCondition) {
        this.timeCondition = timeCondition;
    }

    public void setToAlternate() {
        switch (type) {
            case PRECEDENCE:
                this.type = ConstraintType.ALTERNATE_PRECEDENCE;
                break;
            case RESPONSE:
                this.type = ConstraintType.ALTERNATE_RESPONSE;
                break;
            case SUCCESSION:
                this.type = ConstraintType.ALTERNATE_SUCCESSION;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("Constraint [type=");

        if (isNegative) {
            sb.append("NOT-");
        }

        sb.append(type).append(", A=");

        if(type != null && type.equals(ConstraintType.ABSENCE)) {
            sb.append("Action: ").append(actionA.baseStr());
        } else {
            sb.append(actionA);
        }

        if (actionB != null) {
            sb.append(", B=").append("Action: ").append(actionB.baseStr()).append("]");
        } else {
            sb.append(']');
        }

        return sb.toString();
    }

    public String toRuMString() {
        StringBuilder sb = new StringBuilder();

        if (isNegative) {
            sb.append("Not ");
        }

        sb.append(type.getConstraintName()).append('[');
        if (type.equals(ConstraintType.ABSENCE)) {
            sb.append(actionA.baseStr());
        } else {
            sb.append(actionA.actionStr());
        }

        if (actionB != null) {
            sb.append(", ").append(actionB.baseStr()).append("] |");
            if (activationCondition != null) {
                sb.append(activationCondition.toRuMString());
            }
            sb.append(" |");
            if (correlationCondition != null) {
                sb.append(correlationCondition.toRuMString());
            }
            sb.append(" |");
            if (timeCondition != null) {
                sb.append(timeCondition.toRuMString());
            }
        } else {
            sb.append("] |");
            if (activationCondition != null) {
                sb.append(activationCondition.toRuMString());
            }
            sb.append(" |");
            if (timeCondition != null) {
                sb.append(timeCondition.toRuMString());
            }
        }

        return sb.toString();
    }

    public boolean equalsGS(DeclareConstraint gsc) {
        return (this.getType() == gsc.getType() && areEqual(this.getActionA(), gsc.getActionA())
                && areEqual(this.getActionB(), gsc.getActionB()));
    }

    private boolean areEqual(Action a, Action gsa) {
        return (gsa.actionStr().trim().equalsIgnoreCase(a.actionStr().trim()));
    }
}
