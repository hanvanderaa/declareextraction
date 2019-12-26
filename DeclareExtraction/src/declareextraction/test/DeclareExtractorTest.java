package declareextraction.test;

import declareextraction.constructs.*;
import declareextraction.textprocessing.DeclareConstructor;
import declareextraction.textprocessing.TextParser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    public void singleConstraintNegative(){
        String text = "the customer does not enter after the invoice is not paid";
        DeclareModel dm = declareConstructor.convertToDeclareModel(textParser.parseConstraintString(text));
        assertEquals(1, dm.getConstraints().size());

        String text2 = "if the invoice is not paid the customer does not enter";
        DeclareModel dm2 = declareConstructor.convertToDeclareModel(textParser.parseConstraintString(text2));
        assertEquals(1, dm2.getConstraints().size());

        assertEquals(dm.getConstraints().get(0).toRuMString(), dm2.getConstraints().get(0).toRuMString());

        DeclareConstraint dc = new DeclareConstraint(ConstraintType.PRECEDENCE, new Action("not pay the invoice"), new Action("the customer not enter"), true);
        assertEquals(dm.getConstraints().get(0).toRuMString(), dc.toRuMString());
    }
}