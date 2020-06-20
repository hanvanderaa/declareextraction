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

	private static Pattern[] atMostOncePatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actA>.*) (occurs|happens|exists|takes place) at most once(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) can (occur|happen|exist|take place) at most once(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) (cannot|must not|may not|does not) (occur|happen|exist|take place) more than once(?: in a (trace|case))?$"),
	};

	static Map<Pattern[], ConstraintType> unaryPatterns = new HashMap<Pattern[], ConstraintType>() {{
		put(existPatterns, ConstraintType.EXISTENCE);
		put(absencePatterns, ConstraintType.ABSENCE);
		put(atMostOncePatterns, ConstraintType.ATMOSTONCE);
	}};


	private static Pattern[] precedencePatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actB>.*) (should|must)(?: always)? be preceded by (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) is(?: always)? preceded by (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) can(?: only)? be executed after (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) can(?: only)? (occur|happen|exist|take place) after (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) can(?: only)? (occur|happen|exist|take place) after the occurrence of (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$")
	};

	private static Pattern[] responsePatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actA>.*) is(?: always)? eventually followed by (?:activity )?(?<actB>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) (should|must) be(?: always)? eventually followed by (?:activity )?(?<actB>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(if|when|in case) (?:activity )?(?<actA>.*) (occurs|happens|exists|takes place) then (?:activity )?(?<actB>.*) " +
					"must (occur|happen|exist|take place) (after|afterwards|later)(?: in a (trace|case))?$"),
			Pattern.compile("^(if|when|in case) (?:activity )?(?<actA>.*) (occurs|happens|exists|takes place) (?:activity )?(?<actB>.*) " +
					"must (occur|happen|exist|take place) (after|afterwards|later)(?: in a (trace|case))?$"),
	};

	private static Pattern[] successionPatterns = new Pattern[]{
//			Pattern.compile("^(?:activity )?(?<actB>.*) is(?: always)? preceded by (?:activity )?(?<actA>.*)(?: in a (trace|case))? and $"),
	};

	private static Pattern[] coexistencePatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actA>.*) and (?:activity )?(?<actB>.*) always (coexist|occur together)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) always (coexists|co-occurs) with (?:activity )?(?<actB>.*)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) always occurs(?: together) with (?:activity )?(?<actB>.*)(?: in a (trace|case))?$"),
	};



	static Map<Pattern[], ConstraintType> binaryPatterns = new HashMap<Pattern[], ConstraintType>() {{
		put(precedencePatterns, ConstraintType.PRECEDENCE);
		put(responsePatterns, ConstraintType.RESPONSE);
		put(successionPatterns, ConstraintType.SUCCESSION);
		put(coexistencePatterns, ConstraintType.COEXISTENCE);
	}};


	public static DeclareModel extractDeclareConstraints(String text) {
		System.out.println("\nParsing: " + text);
		DeclareModel model = new DeclareModel(text);
		DeclareConstraint constraint = extractConstraint(text);
		if (constraint != null) {
			System.out.println("Extracted constraint: " + constraint);
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

		for (Pattern[] patterns : binaryPatterns.keySet()) {
			for (Pattern p : patterns) {
				if ((m = p.matcher(text)).find()) {
					ConstraintType type = binaryPatterns.get(patterns);
					Action actA = new Action(m.group("actA"), 0);
					Action actB = new Action(m.group("actB"), 1);
					return new DeclareConstraint(type, actA, actB);
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
