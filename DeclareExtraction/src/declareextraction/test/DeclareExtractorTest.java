package declareextraction.test;

import declareextraction.constructs.*;
import declareextraction.textprocessing.DeclareConstructor;
import declareextraction.textprocessing.TextParser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeclareExtractorTest {
    private TextParser textParser;
    private DeclareConstructor declareConstructor;

    @Test
    public void singleConstraintEncodingTest() {
        textParser = new TextParser();
        declareConstructor = new DeclareConstructor();
        String text = "The process starts when the customer arrives.";
        TextModel textModel = textParser.parseConstraintString(text);
        DeclareModel dm = declareConstructor.convertToDeclareModel(textModel);

        assertEquals(1, dm.getConstraints().size());

        DeclareConstraint dc = new DeclareConstraint(ConstraintType.INIT, new Action("the customer arrive"));
        DeclareConstraint dcEncoded = dm.getConstraints().get(0);
        assertEquals(dc.toString(), dcEncoded.toString());
    }
}