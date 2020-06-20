package declareextraction.test;

import declareextraction.constructs.*;
import declareextraction.constructs.condition.ActivationCondition;
import declareextraction.constructs.condition.CorrelationCondition;
import declareextraction.constructs.condition.TimeCondition;
import declareextraction.textprocessing.ConditionParser;
import declareextraction.textprocessing.DeclareConstructor;
import declareextraction.textprocessing.PatternBasedDeclareConstructor;
import declareextraction.textprocessing.TextParser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//TODO: Test broken after logic changes
class DeclareExtractorTest {
    private static TextParser textParser;
    private static DeclareConstructor declareConstructor;

    @BeforeAll
    public static void setUp() {
        textParser = new TextParser();
        declareConstructor = new DeclareConstructor();
    }

    @Test
    public void singleConstraintEncoding() {
        String text = "The process starts when the customer arrives.";
        TextModel textModel = textParser.parseConstraintString(text);
        DeclareModel dm = declareConstructor.convertToDeclareModel(textModel);
        assertEquals(1, dm.getConstraints().size());

        DeclareConstraint dc = new DeclareConstraint(ConstraintType.INIT, new Action("the customer arrive"));
        DeclareConstraint dcEncoded = dm.getConstraints().get(0);
        assertEquals(dc.toRuMString(), dcEncoded.toRuMString());
    }

    @Test
    public void negativeConstraints() {
        String text = "the customer does not enter after the invoice is not paid";
        DeclareConstraint dc1 = getOnlyConstraintFromSentence(text);
        assertEquals("Not Succession[not pay the invoice, the customer enter] | | |", dc1.toRuMString());

        String text2 = "if the invoice is not paid the customer does not enter";
        DeclareConstraint dc2 = getOnlyConstraintFromSentence(text2);
        assertEquals("Not Co-Existence[not pay the invoice, the customer enter] | | |", dc2.toRuMString());

        DeclareConstraint dc3 = new DeclareConstraint(ConstraintType.PRECEDENCE, new Action("not pay the invoice"), new Action("the customer enter"), true);
        dc3.getActionB().setNegative(true);
        assertEquals("Not Precedence[not pay the invoice, the customer enter] | | |", dc3.toRuMString());
    }

    @Test
    public void RuMPrintConstraint() {
        //generic test method, uses the same method as RuM for extraction
        final DeclareModel generatedModel = declareConstructor.convertToDeclareModel("if the invoice is not paid the customer does not enter");
        assertEquals(1, generatedModel.getConstraints().size());
        System.out.println(generatedModel.getConstraints().get(0).toRuMString());
    }

    @Test
    public void constraintTypesCheck() {
        Map<String, ConstraintType> sentenceToConstraint = new HashMap<>();
        sentenceToConstraint.put("process starts with making an approval", ConstraintType.INIT);
        sentenceToConstraint.put("case ends with removing instances", ConstraintType.END);
        sentenceToConstraint.put("invoice is made", ConstraintType.EXISTENCE);
        sentenceToConstraint.put("customer is not served", ConstraintType.ABSENCE);
        sentenceToConstraint.put("after the results are not cleared, the employee is notified", ConstraintType.PRECEDENCE);
        //sentenceToConstraint.put("if something happens then we will react", ConstraintType.RESPONSE);
        sentenceToConstraint.put("once a meeting must be arranged, it must be held", ConstraintType.SUCCESSION);
        sentenceToConstraint.put("order shipping and invoice payment should occur together", ConstraintType.COEXISTENCE);
        sentenceToConstraint.put("when an incident is identified the details must be logged", ConstraintType.RESPONDED_EXISTENCE);
        sentenceToConstraint.put("an invoice should not be paid more than once", ConstraintType.ATMOSTONCE);
        //sentenceToConstraint.put("the results are displayed immediately, after the notification is received", ConstraintType.CHAIN_PRECEDENCE);
        //sentenceToConstraint.put("if something happens then we will react instantly", ConstraintType.CHAIN_RESPONSE);
        sentenceToConstraint.put("if a meeting must be arranged, it must directly be held", ConstraintType.CHAIN_SUCCESSION);

        for (Map.Entry<String, ConstraintType> entry: sentenceToConstraint.entrySet()) {
            DeclareConstraint dc = getOnlyConstraintFromSentence(entry.getKey());
            System.out.println(dc.toRuMString() + '\n');
            assertEquals(entry.getValue(), dc.getType());
            assertFalse(dc.getActionB() != null && dc.getActionB().isNegative()); //isNegative, as it is used for RuM
        }
    }

    @Test
    public void constraintAlternativeCheck() {
        Map<String, ConstraintType> sentenceToConstraint = new HashMap<>();
        sentenceToConstraint.put("after the results are not cleared, the employee is notified", ConstraintType.ALTERNATE_PRECEDENCE);
        //ALTERNATE_RESPONSE
        sentenceToConstraint.put("once a meeting must be arranged, it must be held", ConstraintType.ALTERNATE_SUCCESSION);

        for (Map.Entry<String, ConstraintType> entry: sentenceToConstraint.entrySet()) {
            DeclareConstraint dc = getOnlyConstraintFromSentence(entry.getKey());
            dc.setToAlternate();
            System.out.println(dc.toRuMString() + '\n');
            assertEquals(entry.getValue(), dc.getType());
            assertFalse(dc.getActionB() != null && dc.getActionB().isNegative());
        }
    }

    @Test
    public void constraintNegativesCheck() {
        Map<String, ConstraintType> sentenceToConstraint = new HashMap<>();
        sentenceToConstraint.put("invoice is not made", ConstraintType.ABSENCE);
        sentenceToConstraint.put("after the results are cleared, the employee is not notified", ConstraintType.SUCCESSION);
        sentenceToConstraint.put("if something happens, we will not react", ConstraintType.COEXISTENCE);
        sentenceToConstraint.put("once a meeting must be arranged, it must be not held", ConstraintType.COEXISTENCE);
        sentenceToConstraint.put("the results are not displayed immediately, after the notification is received", ConstraintType.SUCCESSION);
        sentenceToConstraint.put("if something happens, we will not react instantly", ConstraintType.COEXISTENCE);
        sentenceToConstraint.put("if a meeting must be arranged, it must not directly be held", ConstraintType.COEXISTENCE);

        for (Map.Entry<String, ConstraintType> entry: sentenceToConstraint.entrySet()) {
            DeclareConstraint dc = getOnlyConstraintFromSentence(entry.getKey());
            System.out.println(dc.toRuMString() + '\n');
            assertEquals(entry.getValue(), dc.getType());
            assertTrue(dc.getType().equals(ConstraintType.ABSENCE) || dc.getActionB().isNegative());
        }
    }

    @Test
    public void testConstraintWithConditions() {
        DeclareConstraint twoCondConstraint = getOnlyConstraintFromSentence("process starts with making an approval");
        ActivationCondition act1 = ConditionParser.parseActivationCondition("format is not zipped");
        twoCondConstraint.setActivationCondition(act1);
        TimeCondition time1 = ConditionParser.parseTimeCondition("between 112 and 234 hours");
        twoCondConstraint.setTimeCondition(time1);

        DeclareConstraint threeCondConstraint = getOnlyConstraintFromSentence("the results are not displayed immediately, after the notification is received");
        ActivationCondition act2 = ConditionParser.parseActivationCondition("value smaller than five");
        threeCondConstraint.setActivationCondition(act2);
        CorrelationCondition cor = ConditionParser.parseCorrelationCondition("different originalvalue");
        threeCondConstraint.setCorrelationCondition(cor);
        TimeCondition time2 = ConditionParser.parseTimeCondition("at most three seconds");
        threeCondConstraint.setTimeCondition(time2);

        assertEquals("Init[make an approval] |A.format is not zipped |112,234,h", twoCondConstraint.toRuMString());
        assertEquals("Not Succession[receive the notification, display the results] |A.value < 5.0 |different originalvalue |0,3,s", threeCondConstraint.toRuMString());
    }

    @Test
    public void patternBasedExtractor() {
        final TextModel textModel = new TextModel("b can only happen after a");
        DeclareModel declareModel = PatternBasedDeclareConstructor.extractDeclareConstraints(textModel.getText());
        assertEquals(1, declareModel.getConstraints().size());
        assertEquals("Precedence[a, b] | | |", declareModel.getConstraints().get(0).toRuMString());
    }

    @Test
    public void failPrecedenceConstraint() {
        //only one action is originally found, which is turned into NounPhrase actions
        //after changing logic for EXISTENCE and ABSENCE, returns EXISTENCE
        //strangely, changing 'clear' (RB/JJ) to 'cleared' (VBN) fixes the issue, as is seen in constraintTypesCheck
        String text = "once the results are clear the employee is notified";
        DeclareConstraint dc = getOnlyConstraintFromSentence(text);

        assertEquals(ConstraintType.PRECEDENCE, dc.getType());
    }

    private DeclareConstraint getOnlyConstraintFromSentence(String text) {
        DeclareModel declareModel = declareConstructor.convertToDeclareModel(textParser.parseConstraintString(text));
        assertEquals(1, declareModel.getConstraints().size());
        return declareModel.getConstraints().get(0);
    }
}