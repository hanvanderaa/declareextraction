package declareextraction.constructs.condition;

public class TimeConstraint extends ConditionalConstraint {

    private int minTime;
    private int maxTime;
    private TimeUnit timeUnit;

    public TimeConstraint(int minTime, int maxTime, TimeUnit timeUnit) {
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.timeUnit = timeUnit;
    }

    @Override
    public String toRuMString() {
        return minTime + "," + maxTime + "," + timeUnit.unitToken;
    }

    public enum TimeUnit {
        SECOND("s"),
        MINUTE("m"),
        HOUR("h"),
        DAY("d");

        TimeUnit(String unitToken) {
            this.unitToken = unitToken;
        }

        private String unitToken;
    }
}
