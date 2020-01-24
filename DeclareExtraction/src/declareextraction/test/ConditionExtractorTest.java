package declareextraction.test;


import declareextraction.constructs.condition.ActivationCatConstraint;
import declareextraction.constructs.condition.ActivationNumConstraint;
import declareextraction.constructs.condition.CorrelationConstraint;
import declareextraction.constructs.condition.TimeConstraint;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ConditionExtractorTest {

    @Test
    public void testToRuMString() {
        ActivationCatConstraint catConstraint = new ActivationCatConstraint("Source", true, Stream.of("Networking", "HR", "Business").collect(Collectors.toList()));
        assertEquals("A.Source not in (Networking, HR, Business)", catConstraint.toRuMString());

        ActivationNumConstraint numConstraint = new ActivationNumConstraint("Grade", ActivationNumConstraint.ConditionRelation.GREATER_EQUAL, 5);
        assertEquals("A.Grade >= 5.0", numConstraint.toRuMString());

        CorrelationConstraint correlationConstraint = new CorrelationConstraint(true, "Creator");
        assertEquals("same Creator", correlationConstraint.toRuMString());

        TimeConstraint timeConstraint = new TimeConstraint(1, 2, TimeConstraint.TimeUnit.DAY);
        assertEquals("1,2,d", timeConstraint.toRuMString());
    }
}