package declareextraction.textprocessing;

import declareextraction.constructs.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.Map.Entry;

public class PatternBasedDeclareConstructor {

	private static Pattern[] existPatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actA>.*) eventually (occurs|happens|exists|takes place)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) (should|must|shall) eventually (occur|happen|exist|take place)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) (occurs|happens|exists|takes place) at least once(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) (should|must|shall) (occur|happen|exist|take place) at least once(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) always (occurs|happens|exists|takes place)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) (should|must|shall) always (occur|happen|exist|take place)(?: in a (trace|case))?$")
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
			Pattern.compile("^(?:activity )?(?<actA>.*) and (?:activity )?(?<actB>.*) always (coexist|occur together|co-occur)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) always (coexists|co-occurs) with (?:activity )?(?<actB>.*)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) always occurs(?: together) with (?:activity )?(?<actB>.*)(?: in a (trace|case))?$"),
	};

	private static Pattern[] notCoexistencePatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actA>.*) and (?:activity )?(?<actB>.*)(?: can)? never (coexist|occur together|co-occur)(?: in the same trace|in a case|in a trace)?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) never (coexists|occurs together|co-occurs|occurs) with (?:activity )?(?<actB>.*)? (?: in the same trace|in a case|in a trace)?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) can never (coexist|occur together|co-occur|occur) with (?:activity )?(?<actB>.*)? (?: in the same trace|in a case|in a trace)?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) and (?:activity )?(?<actB>.*) cannot (coexist|occur together|co-occur)(?: in the same trace|in a case|in a trace)?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) cannot (coexist|occur together|co-occur|occur) with (?:activity )?(?<actB>.*)? (?: in the same trace|in a case|in a trace)?$"),
	};

	private static Pattern[] notPrecedencePatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actB>.*) (should not|must not|cannot) be preceded by (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) is never preceded by (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
	};

	private static Pattern[] notResponsePatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actA>.*) (is never|cannot|should not|must not) be eventually followed by (?:activity )?(?<actB>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) never follows (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$")
	};

	private static Pattern[] chainPrecedencePatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actB>.*) (should|must)(?: always)? be immediately preceded by (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) is(?: always)? immediately preceded by (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) (should|must)(?: always)?  immediately be preceded by (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) can(?: only)? be immediately executed after (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) can(?: only)? immediately be executed after (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) can(?: only)? (occur|happen|exist|take place) immediately after (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) can(?: only)? (occur|happen|exist|take place) immediately after the occurrence of (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$")
	};

	private static Pattern[] chainResponsePatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actA>.*) is(?: always)? immediately followed by (?:activity )?(?<actB>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) (should|must) be(?: always)? immediately followed by (?:activity )?(?<actB>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(if|when|in case) (?:activity )?(?<actA>.*) (occurs|happens|exists|takes place) then (?:activity )?(?<actB>.*) " +
					"must immediately (occur|happen|exist|take place) (after|afterwards|later)(?: in a (trace|case))?$"),
			Pattern.compile("^(if|when|in case) (?:activity )?(?<actA>.*) (occurs|happens|exists|takes place) (?:activity )?(?<actB>.*) " +
					"must (occur|happen|exist|take place) immediately (after|afterwards|later)(?: in a (trace|case))?$"),
	};

	private static Pattern[] choicePatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actA>.*) or ^(?:activity )?(?<actB>.*) eventually (occurs|happens|exists|takes place)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) or ^(?:activity )?(?<actB>.*) (should|must|shall) eventually (occur|happen|exist|take place)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) or ^(?:activity )?(?<actB>.*) (occurs|happens|exists|takes place) at least once(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) or ^(?:activity )?(?<actB>.*) (should|must|shall) (occur|happen|exist|take place) at least once(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) or ^(?:activity )?(?<actB>.*) always (occurs|happens|exists|takes place)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) or ^(?:activity )?(?<actB>.*) (should|must|shall) always (occur|happen|exist|take place)(?: in a (trace|case))?$")
	};

	private static Pattern[] exclusiveChoicePatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actA>.*) or ^(?:activity )?(?<actB>.*) eventually (occurs|happens|exists|takes place) but (not together|they cannot occur together)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) or ^(?:activity )?(?<actB>.*) (should|must|shall) eventually (occur|happen|exist|take place) but (not together|they cannot occur together)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) or ^(?:activity )?(?<actB>.*) (occurs|happens|exists|takes place) at least once but (not together|they cannot occur together)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) or ^(?:activity )?(?<actB>.*) (should|must|shall) (occur|happen|exist|take place) at least once but (not together|they cannot occur together)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) or ^(?:activity )?(?<actB>.*) always (occurs|happens|exists|takes place) but (not together|they cannot occur together)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) or ^(?:activity )?(?<actB>.*) (should|must|shall) always (occur|happen|exist|take place) but (not together|they cannot occur together)(?: in a (trace|case))?$")
	};

	private static Pattern[] notChainPrecedencePatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actB>.*) (should|must) (never|not) be immediately preceded by (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) is (never|not) immediately preceded by (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) (should|must) (never|not) immediately be preceded by (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) cannot be immediately executed after (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) cannot immediately be executed after (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) cannot (occur|happen|exist|take place) immediately after (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) is not allowed to (occur|happen|exist|take place) immediately after (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actB>.*) cannot (occur|happen|exist|take place) immediately after the occurrence of (?:activity )?(?<actA>.*?)(?: in a (trace|case))?$")
	};

	private static Pattern[] notChainResponsePatterns = new Pattern[]{
			Pattern.compile("^(?:activity )?(?<actA>.*) is (never|not) immediately followed by (?:activity )?(?<actB>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(?:activity )?(?<actA>.*) (should|must) (never|not) be immediately followed by (?:activity )?(?<actB>.*?)(?: in a (trace|case))?$"),
			Pattern.compile("^(if|when|in case) (?:activity )?(?<actA>.*) never (occurs|happens|exists|takes place) then (?:activity )?(?<actB>.*) " +
					"must (never|not) immediately (occur|happen|exist|take place) (after|afterwards|later)(?: in a (trace|case))?$"),
			Pattern.compile("^(if|when|in case) (?:activity )?(?<actA>.*) (occurs|happens|exists|takes place) (?:activity )?(?<actB>.*) " +
					"must (never|not) (occur|happen|exist|take place) immediately (after|afterwards|later)(?: in a (trace|case))?$"),
	};

	static Map<Pattern[], ConstraintType> binaryPatterns = new HashMap<Pattern[], ConstraintType>() {{
		put(precedencePatterns, ConstraintType.PRECEDENCE);
		put(responsePatterns, ConstraintType.RESPONSE);
		put(successionPatterns, ConstraintType.SUCCESSION);
		put(coexistencePatterns, ConstraintType.COEXISTENCE);
		put(notCoexistencePatterns, ConstraintType.COEXISTENCE);
		put(notPrecedencePatterns, ConstraintType.PRECEDENCE);
		put(notResponsePatterns, ConstraintType.RESPONSE);
		put(chainPrecedencePatterns, ConstraintType.CHAIN_PRECEDENCE);
		put(chainResponsePatterns, ConstraintType.CHAIN_RESPONSE);
		put(notChainPrecedencePatterns, ConstraintType.CHAIN_PRECEDENCE);
		put(notChainResponsePatterns, ConstraintType.CHAIN_RESPONSE);
	}};

	static List<Pattern[]> negatedPatterns = new ArrayList<Pattern[]>() {{
		add(notCoexistencePatterns);
		add(notPrecedencePatterns);
		add(notResponsePatterns);
		add(notChainPrecedencePatterns);
		add(notChainResponsePatterns);
	}};


	public static DeclareModel extractDeclareConstraints(String text) {
		System.out.println("\nParsing: " + text);
		DeclareModel model = new DeclareModel(text);
		DeclareConstraint constraint = extractConstraint(text);
		if (constraint != null) {
			System.out.println("Extracted constraint: " + constraint);
			model.addConstraint(constraint);
		} else {
			System.out.println("Pattern matching failed");
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
					DeclareConstraint constraint = new DeclareConstraint(type, actA, actB);
					if (negatedPatterns.contains(patterns)) {
						constraint.setNegative();
					}
					return constraint;
				}
			}
		}

		return null;
	}

	public static void main(String[] args) {
		String[] cases = new String[]{
				"The process starts when the customer arrives."
		};
		for (int i = 0; i < cases.length; i++) {
			extractDeclareConstraints(cases[i]);
		}

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
