package declareextraction.utils;

import java.util.Arrays;

public class WordClasses {

	public static String[] MANDATORY_MODALS = new String[]{"must", "will", "would", "shall", "should", "require", "have to"};
	public static String[] OPTIONAL_MODALS = new String[]{"can", "could", "may", "might"};
	public static String[] START_VERBS = new String[]{"start", "begin"};
	public static String[] END_VERBS = new String[]{"end", "finish"};
	public static String[] PROCESS_OBJECTS = new String[]{"process", "the process", "workflow", "instance", "case"};
	public static String[] FLOW_VERBS = new String[]{"require", "need", "precede", "succeed", "follow", "occur", "happen", "take place"};
	public static String[] REVERSE_CLAUSES = new String[]{"first", "before", "earlier"};
	public static String[] REVERSE_MARKERS = new String[]{"after", "later"};
	public static String[] IMMEDIATE_ADVERBS = new String[]{"immediately", "instantly", "directly", "promptly"}; //Temporal expressions expressing immediacy
	public static String[] RESERVED_WORDS = new String[]{"then", "after", "later"};
	public static String[] EXECUTION_LIMITS = new String[]{"only once", "at most once", "not more than once", "one time", "fewer than twice", "less than twice"};


	public static boolean isMandatory(String modal) {
		return Arrays.asList(MANDATORY_MODALS).contains(modal.toLowerCase());
	}
	
	public static boolean isOptional(String modal) {
		return Arrays.asList(OPTIONAL_MODALS).contains(modal.toLowerCase());
	}
	
	public static boolean isModal(String word) {
		return (isOptional(word) || isMandatory(word));
	}
	
	public static boolean isProcessObject(String object) {
		return Arrays.asList(PROCESS_OBJECTS).contains(object.toLowerCase());
	}
	
	public static boolean isFlowVerb(String object) {
		return Arrays.asList(FLOW_VERBS).contains(object.toLowerCase());
	}
	
	public static boolean isStartVerb(String verb) {
		return Arrays.asList(START_VERBS).contains(verb.toLowerCase());
	}
	
	public static boolean isEndVerb(String verb) {
		return Arrays.asList(END_VERBS).contains(verb.toLowerCase());
	}
	
	public static boolean isReverseClause(String word) {
		return Arrays.asList(REVERSE_CLAUSES).contains(word.toLowerCase());
	}

	public static boolean isReservedWord(String word) {
		return Arrays.asList(RESERVED_WORDS).contains(word.toLowerCase());
	}
	
	public static boolean isReverseMarker(String word) {
		return Arrays.asList(REVERSE_MARKERS).contains(word.toLowerCase());
	}

	public static boolean isImmediate(String word) {
		return Arrays.asList(IMMEDIATE_ADVERBS).contains(word.toLowerCase());
	}

    public static boolean hasExecutionLimit(String text) {
		for (String term : EXECUTION_LIMITS) {
			if (text.toLowerCase().contains(term) || text.toLowerCase().contains("not") && text.toLowerCase().contains("more than once")) {
				return true;
			}
		}
		return false;
    }
}
