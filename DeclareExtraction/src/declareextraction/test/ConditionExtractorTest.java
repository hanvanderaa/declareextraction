package declareextraction.test;

import declareextraction.constructs.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConditionExtractorTest {
    @Test
    public void printConstraint() {
        ConditionalConstraint constraint = new ConditionalConstraint(ConstraintType.ACTIVATION, "Time", ConditionRelation.LOWER, 5.d);
        System.out.println(constraint.toString());
    }
}