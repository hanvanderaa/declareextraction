package declareextraction.textprocessing;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import declareextraction.constructs.Action;
import declareextraction.constructs.ConstraintType;
import declareextraction.constructs.DeclareConstraint;
import declareextraction.constructs.DeclareModel;
import declareextraction.constructs.TextModel;
import declareextraction.utils.WordClasses;

public class DeclareConstructor {

	private TextParser parser;
	private boolean suppressOutput = false;
	private PrintStream ogOut = System.out;
	private PrintStream ogErr = System.err;

	public DeclareConstructor() {
		parser = new TextParser();
	}

	public void suppressConsoleOutput() {
		this.suppressOutput = true;
	}

	public DeclareModel convertToDeclareModel(String text) {
		if (suppressOutput) {
			this.redirectSysOutAndErr();
		}
		// first try pattern based matching
		DeclareModel declareModel = PatternBasedDeclareConstructor.extractDeclareConstraints(text);
		if (!declareModel.isEmpty()) {
			System.out.println("pattern matching solution found");
			if (suppressOutput) {
				this.returnSysOutAndErr();
			}
			return declareModel;
		}
		// else use parser
		System.out.println("no matching pattern found, using text parser for constraint extraction");
		TextModel textModel = parser.parseConstraintString(text);
		declareModel = convertToDeclareModel(textModel);

		if (suppressOutput) {
			this.returnSysOutAndErr();
		}
		return declareModel;
	}

	private DeclareModel convertTextModelToDeclare(TextModel textModel) {
		DeclareModel declareModel = new DeclareModel(textModel.getText());

		if (textModel.getInterrelations().isEmpty() && textModel.getActions().size() == 1) {
			Action action = textModel.getActions().get(0);
			if (action.getVerb().contains("and") && WordClasses.hasCoOccurrence(textModel.getText())) {
				for (DeclareConstraint constraint : transformToCoexistenceConstraint(textModel, textModel.getActions().get(0))) {
					declareModel.addConstraint(constraint);
					System.out.println("Constraint identified: " + constraint);
				}
			} else {
				DeclareConstraint constraint = transformToUnaryConstraint(textModel, textModel.getActions().get(0));
				declareModel.addConstraint(constraint);
				System.out.println("Constraint identified: " + constraint);
			}
		}

		for (Interrelation rel : textModel.getInterrelations()) {
			DeclareConstraint constraint = transformRelationToConstraint(textModel, rel);
			if (constraint != null) {
				declareModel.addConstraint(constraint);
				System.out.println("Constraint identified: " + constraint);
			}
		}
		return declareModel;
	}

	@Deprecated
	// use convertToDeclareModel(String text) instead;
	public DeclareModel convertToDeclareModel(TextModel textModel) {
		DeclareModel declareModel = PatternBasedDeclareConstructor.extractDeclareConstraints(textModel.getText());
		if (!declareModel.isEmpty()) {
			return declareModel;
		}
		// else use parser
		declareModel = convertTextModelToDeclare(textModel);
		return declareModel;
	}

	private DeclareConstraint transformToUnaryConstraint(TextModel textModel, Action action) {
		if (WordClasses.hasExecutionLimit(textModel.getText())) {
			if (action.isNegative()) {
				action.setNegative(false);
			}
			return new DeclareConstraint(ConstraintType.ATMOSTONCE, action);
		}
		if (action.isNegative()) {
			return new DeclareConstraint(ConstraintType.ABSENCE, action);
		} else {
			return new DeclareConstraint(ConstraintType.EXISTENCE, action);
		}
	}

	private List<DeclareConstraint> transformToCoexistenceConstraint(TextModel textModel, Action action) {
		String[] verbs = action.getVerb().split("and");
		List<Action> actions = new ArrayList<>();
		for (String verb : verbs) {
				Action newAct = new Action(verb.trim().toLowerCase());
				newAct.setObject(action.getObject());
				actions.add(newAct);
		}
		List<DeclareConstraint> constraints = new ArrayList<>();
		for (int i = 0; i < actions.size() - 1; i++) {
			DeclareConstraint constraint = new DeclareConstraint(ConstraintType.COEXISTENCE, actions.get(i), actions.get(i+1));
			constraints.add(constraint);
		}
		return constraints;
	}

	private DeclareConstraint transformRelationToConstraint(TextModel textModel, Interrelation rel) {
		// transform into meta constraint (init/end)
		if (isMetaAction(rel.getActionA()) || isMetaAction(rel.getActionB())) {
			return transformToMetaConstraint(rel);
		}

		// transform into binary constraint (response/precedence/succession)
		if (rel.getType() == Interrelation.RelationType.FOLLOWS) {
			return transformToBinaryConstraint(textModel, rel);
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

	private DeclareConstraint transformToBinaryConstraint(TextModel textModel, Interrelation rel) {
		// determine binary constraint type
		ConstraintType constraintType;
		boolean aMand = WordClasses.isMandatory(rel.getActionA().getModal());
		boolean bMand = WordClasses.isMandatory(rel.getActionB().getModal());
		boolean bImm = rel.getActionB().isImmediate();

		if (!bMand) {
			constraintType = bImm ? ConstraintType.CHAIN_PRECEDENCE : ConstraintType.PRECEDENCE;
		} else if (aMand) {
			constraintType = bImm ? ConstraintType.CHAIN_SUCCESSION : ConstraintType.SUCCESSION;
		} else {
			if (WordClasses.hasOrderIndicator(textModel.getText())) {
				constraintType = bImm ? ConstraintType.CHAIN_RESPONSE : ConstraintType.RESPONSE;
			} else {
				constraintType = ConstraintType.RESPONDED_EXISTENCE;
			}
		}
		resolveAnaphoras(rel.getActionA(), rel.getActionB());
		boolean negative = false;
		if (rel.getActionB().isNegative()) {
			negative = true;
			if (WordClasses.hasOrderIndicator(textModel.getText())) {
				constraintType = ConstraintType.SUCCESSION;
			} else {
				constraintType = ConstraintType.COEXISTENCE;
			}
		}
		DeclareConstraint constraint = new DeclareConstraint(constraintType, rel.getActionA(), rel.getActionB());
		if (negative) {
			constraint.setNegative();
		}
		return constraint;
	}



	private void resolveAnaphoras(Action actA, Action actB) {
		// TODO: this is only very basic
		if (actA.getObject().getText().equals("it") && !actB.getObject().getText().isEmpty()) {
			actA.setObject(actB.getObject());
		}
		if (actB.getObject().getText().equals("it") && !actA.getObject().getText().isEmpty()) {
			actB.setObject(actA.getObject());
		}
	}

	private boolean isMetaAction(Action action) {
		String joint = action.getObject().getText() + " " + action.getSubject().getText();

		for (String processObject : WordClasses.PROCESS_OBJECTS) {
			if (joint.contains(processObject)) {
				return true;
			}
		}
		return false;
	}

	private void redirectSysOutAndErr() {
		ogOut = System.out;
		ogErr = System.err;
		PrintStream voidStream = new PrintStream(new OutputStream() {
			public void write(int b) {

			}
		});
		System.setOut(voidStream);
		System.setErr(voidStream);
	}

	private void returnSysOutAndErr() {
		System.setOut(ogOut);
		System.setErr(ogErr);
	}
}
