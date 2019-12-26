package declareextraction.constructs;

public class DeclareConstraint {

    ConstraintType type;
    Action actionA;
    Action actionB;
    boolean isNegative = false;

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

    @Override
    public String toString() {
        if (actionB == null) {
            return "Constraint [type=" + type + ", A=" + actionA + "]";
        }
        if (isNegative) {
            return "Constraint [type= NOT-" + type + ", A=" + actionA + ", B=" + actionB + "]";
        }
        return "Constraint [type=" + type + ", A=" + actionA + ", B=" + actionB + "]";
    }

    public String toRuMString() {
        StringBuilder sb = new StringBuilder();

        if (actionB != null && actionB.isNegative()) {
            sb.append("Not ");
        }

        //The order of actions might depend on the constraint
        sb.append(type.getConstraintName()).append('[').append(actionA.actionStr());
        if (actionB != null) {
            sb.append(", ").append(actionB.baseStr()).append("] | | |");
        } else {
            sb.append("] | |");
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
