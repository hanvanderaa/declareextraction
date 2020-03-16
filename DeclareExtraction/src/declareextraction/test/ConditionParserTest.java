package declareextraction.test;

import declareextraction.constructs.condition.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static declareextraction.textprocessing.ConditionParser.*;
import static org.junit.jupiter.api.Assertions.*;

class ConditionParserTest {

    @Test
    public void testActivationCondition() {
        Map<String, String> textToConditionRuMString = new HashMap<>();
        //ActivationCatCondition
        textToConditionRuMString.put("source not in car duck dolphin", "A.source not in (car, duck, dolphin)");
        textToConditionRuMString.put("source is in grass tree", "A.source in (grass, tree)");
        textToConditionRuMString.put("source is black", "A.source is black");
        textToConditionRuMString.put("format is not zipped", "A.format is not zipped");

        //ActivationNumCondition
        textToConditionRuMString.put("price point is greater than 6.0", "A.price_point > 6.0");
        textToConditionRuMString.put("value greater than or equal to 5.9999", "A.value >= 5.9999");
        textToConditionRuMString.put("value is two", "A.value = 2.0");
        textToConditionRuMString.put("tree size smaller than or equal to 4.123", "A.tree_size <= 4.123");
        textToConditionRuMString.put("value is smaller than five", "A.value < 5.0");
        textToConditionRuMString.put("value is not -1", "A.value != -1.0"); //is this on in RuM?

        for (Map.Entry<String, String> entry : textToConditionRuMString.entrySet()) {
            Condition c = parseActivationCondition(entry.getKey());
            assertNotNull(c, entry.getKey());
            assertEquals(entry.getValue(), c.toRuMString());
        }
    }

    @Test
    public void testActivationConditions() {
        String text = "source not in car duck dolphin and price point is greater than 6.0 or value is not -1";
        String result = parseActivationConditions(text);
        assertEquals("A.source not in (car, duck, dolphin) and A.price_point > 6.0 or A.value != -1.0", result);
    }

    @Test
    public void testCorrelationCondition() {
        Map<String, String> textToConditionRuMString = new HashMap<>();
        textToConditionRuMString.put("date is same", "same date");
        textToConditionRuMString.put("timestamp hour is different", "different timestamp_hour");

        for (Map.Entry<String, String> entry : textToConditionRuMString.entrySet()) {
            Condition c = parseCorrelationCondition(entry.getKey());
            assertNotNull(c, entry.getKey());
            assertEquals(entry.getValue(), c.toRuMString());
        }
    }

    @Test
    public void testActivationOrCorrelationConditions() {
        String text = "source is different and price point is same or value is not -1";
        String result = parseActivationOrCorrelationConditions(text);
        assertEquals("different source and same price_point or A.value != -1.0", result);
    }

    @Test
    public void testTimeCondition() {
        Map<String, String> textToConditionRuMString = new HashMap<>();
        textToConditionRuMString.put("between 5 and 6 seconds", "5,6,s");
        textToConditionRuMString.put("between one and 2.0 minutes", "1,2,m");
        textToConditionRuMString.put("between 112 and 234 hours", "112,234,h");
        textToConditionRuMString.put("between 1.2 and 6.9999 days", "1,6,d");

        textToConditionRuMString.put("at most eight seconds", "0,8,s");
        textToConditionRuMString.put("at most 23 days", "0,23,d");

        textToConditionRuMString.put("not before 10 and no later than 15 days", "10,15,d");

        for (Map.Entry<String, String> entry : textToConditionRuMString.entrySet()) {
            Condition c = parseTimeCondition(entry.getKey());
            assertNotNull(c, entry.getKey());
            assertEquals(entry.getValue(), c.toRuMString());
        }
    }

    @Test
    public void testToRuMString() {
        ActivationCatCondition catCondition = new ActivationCatCondition("Source", true, Stream.of("Networking", "HR", "Business").collect(Collectors.toList()));
        assertEquals("A.Source not in (Networking, HR, Business)", catCondition.toRuMString());

        ActivationNumCondition numCondition = new ActivationNumCondition("Grade", ActivationNumCondition.ConditionRelation.GREATER_EQUAL, 5);
        assertEquals("A.Grade >= 5.0", numCondition.toRuMString());

        CorrelationCondition correlationCondition = new CorrelationCondition(true, "Creator");
        assertEquals("different Creator", correlationCondition.toRuMString());

        TimeCondition timeCondition = new TimeCondition(1, 2, TimeCondition.TimeUnit.DAY);
        assertEquals("1,2,d", timeCondition.toRuMString());
    }
}