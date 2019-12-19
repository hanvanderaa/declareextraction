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

		for (Interrelation rel : textModel.getInterrelations()) {
			DeclareConstraint constraint = transformRelationToConstraint(rel);
			if (constraint != null) {
				declareModel.addConstraint(constraint);
				System.out.println("Constraint identified: " + constraint);
			}
		}
		return declareModel;
	}

	private DeclareConstraint transformRelationToConstraint(Interrelation rel) {
		// transform into meta constraint (init/end)
		if (isMetaAction(rel.getActionA()) || isMetaAction(rel.getActionB())) {
			return transformToMetaConstraint(rel);
		}

		// transform into binary constraint (response/precedence/succession)
		if (rel.getType() == Interrelation.RelationType.FOLLOWS) {
			return transformToBinaryConstraint(rel);
		}
		return null;
	}

	private DeclareConstraint transformToMetaConstraint(Interrelation rel) {
		System.out.println("Identified meta-relation: " + rel);
		// case: check if action A indicates process start
		if (WordClasses.isStartVerb(rel.getActionA().getVerb())) {
			ConstraintType constraintType = ConstraintType.INIT;
			return new DeclareConstraint(constraintType, rel.getActionB());
		}
		// case: check if action B indicates process start
		if (WordClasses.isStartVerb(rel.getActionB().getVerb())) {
			ConstraintType constraintType = ConstraintType.INIT;
			return  new DeclareConstraint(constraintType, rel.getActionA());
		}
		// case: check if action A indicates process end
		if (WordClasses.isEndVerb(rel.getActionA().getVerb())) {
			ConstraintType constraintType = ConstraintType.END;
			return new DeclareConstraint(constraintType, rel.getActionB());
		}
		// case: check if action B indicates process end
		if (WordClasses.isEndVerb(rel.getActionB().getVerb())) {
			ConstraintType constraintType = ConstraintType.END;
			return new DeclareConstraint(constraintType, rel.getActionA());
		}
		return null;
	}

	private DeclareConstraint transformToBinaryConstraint(Interrelation rel) {
		// determine binary constraint type
		ConstraintType constraintType = null;
		boolean aMand = WordClasses.isMandatory(rel.getActionA().getModal());
		boolean bMand = WordClasses.isMandatory(rel.getActionB().getModal());
		if (!bMand) {
			constraintType = ConstraintType.PRECEDENCE;
		} if (aMand && bMand) {
			constraintType = ConstraintType.SUCCESSION;
		} if (!aMand && bMand) {
			constraintType = ConstraintType.RESPONSE;
		}

		DeclareConstraint constraint = new DeclareConstraint(constraintType, rel.getActionA(), rel.getActionB());

		// check if constraint should be negated
		if (constraint.getActionA().isNegative() || constraint.getActionB().isNegative()) {
			constraint.setNegative();
		}
		return constraint;
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
