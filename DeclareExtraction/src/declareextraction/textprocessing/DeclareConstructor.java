package declareextraction.textprocessing;

import java.util.Arrays;

import declareextraction.constructs.Action;
import declareextraction.constructs.ConstraintType;
import declareextraction.constructs.DeclareConstraint;
import declareextraction.constructs.DeclareModel;
import declareextraction.constructs.TextModel;
import declareextraction.utils.WordClasses;

public class DeclareConstructor {

public DeclareModel convertToDeclareModel(TextModel textModel) {
		
		DeclareModel declareModel = new DeclareModel(textModel.getText());
		boolean isMeta = false;
		for (Interrelation rel : textModel.getInterrelations()) {
		
			if (isMetaAction(rel.getActionA())) {
				System.out.println("Identified meta-relation: " + rel);
				if (WordClasses.isStartVerb(rel.getActionA().getVerb())) {
					ConstraintType ct = ConstraintType.INIT;
					DeclareConstraint c = new DeclareConstraint(ct, rel.getActionB());
					declareModel.addConstraint(c);
					System.out.println("FOUND: " + c);
					isMeta = true;
				}
				if (WordClasses.isEndVerb(rel.getActionA().getVerb())) {
					ConstraintType ct = ConstraintType.END;
					DeclareConstraint c = new DeclareConstraint(ct, rel.getActionB());
					declareModel.addConstraint(c);
					System.out.println("FOUND: " + c);
					isMeta = true;
				}
				if (WordClasses.isEndVerb(rel.getActionB().getVerb())) {
					ConstraintType ct = ConstraintType.END;
					DeclareConstraint c = new DeclareConstraint(ct, rel.getActionA());
					declareModel.addConstraint(c);
					System.out.println("FOUND: " + c);
					isMeta = true;
				}
				
			}
			if (!isMeta && rel.getType() == Interrelation.RelationType.FOLLOWS) {
				ConstraintType ct = null;
				boolean aMand = WordClasses.isMandatory(rel.getActionA().getModal());
				boolean bMand = WordClasses.isMandatory(rel.getActionB().getModal());
				
				if (!bMand) {
					ct = ConstraintType.PRECEDENCE;
				}
				if (aMand && bMand) {
					ct = ConstraintType.SUCCESSION;
				} if (!aMand && bMand) {
					ct = ConstraintType.RESPONSE;
				}
								
				DeclareConstraint c = new DeclareConstraint(ct, rel.getActionA(), rel.getActionB());
				
				if (c.getActionA().isNegative() || c.getActionB().isNegative()) {
					c.setNegative();
				}
				
				declareModel.addConstraint(c);
				System.out.println("Generated constraint: " + c);
			}
		}

		return declareModel;
	}
	
	private boolean isMetaAction(Action action) {
		
		String joint = action.getObject().getText() + " " + action.getSubject().getText();
		
		for (String processObject : Arrays.asList(WordClasses.PROCESS_OBJECTS)) {
			if (joint.contains(processObject)) {
				return true;
			}
		}
		return false;
	}
	
}
