package declareextraction.textprocessing;

import declareextraction.constructs.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.Map.Entry;

public class PatternBasedDeclareConstructor {



	private static Pattern[] existPatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actA>.*) eventually (occurs|happens|exists|takes place)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) (occurs|happens|exists|takes place) at least once(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) always (occurs|happens|exists|takes place)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) must (occur|happen|exist|take place)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) must (occur|happen|exist|take place) at least once(?: in a (trace|case))?$")
	};

	private static Pattern[] absencePatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actA>.*) (cannot|must not|may not|does not) (occur|happen|exist|take place)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) never (occurs|happens|exists|takes place)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) is always missing(?: in a (trace|case))?$"),
	};


	static Map<Pattern[], ConstraintType> unaryPatterns = new HashMap<Pattern[], ConstraintType>() {{
		put(existPatterns, ConstraintType.EXISTENCE);
		put(absencePatterns, ConstraintType.ABSENCE);
	}};


	public static void main(String[] args) {
//		extractDeclareConstraints("activity registering must happen at least once in a trace");
//		extractDeclareConstraints("registering must not happen in a trace");
//		extractDeclareConstraints("registering cannot happen in a trace");
//		c.extractDeclareConstraints("the amount is greater than 5");
		DeclareModel model;
		DeclareConstructor dc = new DeclareConstructor();
//		dc.suppressConsoleOutput();

		model = dc.convertToDeclareModel("activity registering must happen at least once in a trace");
		System.out.println(model);
		model =dc.convertToDeclareModel("An order must be received, before it can be shipped");
		System.out.println(model);
	}


	public static DeclareModel extractDeclareConstraints(String text) {
		System.out.println("\nParsing: " + text);
		DeclareModel model = new DeclareModel(text);
		DeclareConstraint constraint = extractConstraint(text);
		if (constraint != null) {
//			System.out.println("Extracted constraint: " + constraint);
			model.addConstraint(constraint);
		}
		return model;
	}


	private static DeclareConstraint extractConstraint(String text) {
		Matcher m;
		for (Pattern[] patterns : unaryPatterns.keySet()) {
			for (Pattern p : patterns) {
				if ((m = p.matcher(text)).find()) {
					ConstraintType type = unaryPatterns.get(patterns);
					Action act = new Action(m.group("actA"), 0);
					return new DeclareConstraint(type, act);
				}
			}
		}
		return null;
	}



//	public DeclareModel convertToDeclareModel(TextModel textModel) {
//		DeclareModel declareModel = new DeclareModel(textModel.getText());
//
//		if (textModel.getInterrelations().isEmpty() && textModel.getActions().size() == 1) {
//			Action action = textModel.getActions().get(0);
//			if (action.getVerb().contains("and") && WordClasses.hasCoOccurrence(textModel.getText())) {
//				for (DeclareConstraint constraint : transformToCoexistenceConstraint(textModel, textModel.getActions().get(0))) {
//					declareModel.addConstraint(constraint);
//					System.out.println("Constraint identified: " + constraint);
//				}
//			} else {
//				DeclareConstraint constraint = transformToUnaryConstraint(textModel, textModel.getActions().get(0));
//				declareModel.addConstraint(constraint);
//				System.out.println("Constraint identified: " + constraint);
//			}
//		}
//
//		for (Interrelation rel : textModel.getInterrelations()) {
//			DeclareConstraint constraint = transformRelationToConstraint(textModel, rel);
//			if (constraint != null) {
//				declareModel.addConstraint(constraint);
//				System.out.println("Constraint identified: " + constraint);
//			}
//		}
//		return declareModel;
//	}


}
