package declareextraction.test;

import declareextraction.constructs.condition.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
        Map<String, String> map = new HashMap<>();
        map.put("date is same", "same date");
        map.put("timestamp hour is different", "different timestamp_hour");
        map.put("the type is the same", "same type");
        map.put("having the same type", "same type");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            Condition c = parseCorrelationCondition(entry.getKey());
            assertNotNull(c, entry.getKey());
            assertEquals(entry.getValue(), c.toRuMString());
        }
    }

    @Test
    public void testActivationOrCorrelationConditions() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("source is different and price point is same or value is not -1", "different source and same price_point or A.value != -1.0");
        map.put("the price is the same", "same price");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            final String s = parseActivationOrCorrelationConditions(entry.getKey());
            assertNotNull(entry.getKey());
            assertEquals(entry.getValue(), s);
        }
    }

    @Test
    public void testTimeCondition() {
        final Map<String, String> map = new LinkedHashMap<>();
        map.put("between 5 and 6 seconds", "5,6,s");
        map.put("between one and 2.0 minutes", "1,2,m");
        map.put("between 112 and 234 hours", "112,234,h");
        map.put("between 1.2 and 6.9999 days", "1,6,d");

        map.put("at most eight seconds", "0,8,s");
        map.put("at most 23 days", "0,23,d");

        map.put("not before 10 and no later than 15 days", "10,15,d");

        map.put("after 1 day", "1,1,d");
        map.put("after 50 to 68 seconds", "50,68,s");
        map.put("after 5 to 6 minutes", "5,6,m");

        for (Map.Entry<String, String> entry : map.entrySet()) {
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