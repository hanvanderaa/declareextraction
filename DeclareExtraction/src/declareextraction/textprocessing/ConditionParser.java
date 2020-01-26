package declareextraction.textprocessing;

import declareextraction.constructs.condition.*;
import declareextraction.constructs.condition.ActivationNumCondition.ConditionRelation;

import java.util.*;

public class ConditionParser {

    public static Condition parseCondition(String text, Condition.ConditionType type) {
        switch (type) {
            case ACTIVATION:
                return parseActivationCondition(text);
            case CORRELATION:
                return parseCorrelationCondition(text);
            case TIME:
                return parseTimeCondition(text);
            default:
                return null;
        }
    }

    public static Condition parseActivationCondition(String text) {
        List<String> words = Arrays.asList(text.toLowerCase().split(" "));
        if (words.size() >= 3) {
            try {
                Iterator<String> iter = words.iterator();
                String field = iter.next();
                String next = iter.next();

                switch (next) {
                    case "not":
                        if (!iter.next().equals("in")) {
                            break;
                        }
                    case "in":
                        boolean falseCond = next.equals("not");
                        List<String> cats = new ArrayList<>();
                        iter.forEachRemaining(cats::add);
                        return new ActivationCatCondition(field, falseCond, cats);
                    case "is":
                        next = iter.next();
                        if (next.equals("not")) {
                            return actConditionFromBasics(field, ConditionRelation.NOT_EQUAL, iter.next());
                        } else {
                            return actConditionFromBasics(field, ConditionRelation.EQUALS, next);
                        }
                    case "higher":
                    case "greater":
                    case "more": {
                        if (iter.next().equals("than")) {
                            next = iter.next();
                            if (next.equals("or")) {
                                if (iter.next().equals("equal") && iter.next().equals("to")) {
                                    return actConditionFromBasics(field, ConditionRelation.GREATER_EQUAL, iter.next());
                                }
                            } else {
                                return actConditionFromBasics(field, ConditionRelation.GREATER, next);
                            }
                        }
                        break;
                    }
                    case "lower":
                    case "less":
                    case "smaller": {
                        if (iter.next().equals("than")) {
                            next = iter.next();
                            if (next.equals("or")) {
                                if (iter.next().equals("equal") && iter.next().equals("to")) {
                                    return actConditionFromBasics(field, ConditionRelation.LOWER_EQUAL, iter.next());
                                }
                            } else {
                                return actConditionFromBasics(field, ConditionRelation.LOWER, next);
                            }
                        }
                        break;
                    }
                }
            } catch (NoSuchElementException ignored) {
            }
        }

        return null;
    }

    public static Condition parseTimeCondition(String text) {
        List<String> words = Arrays.asList(text.toLowerCase().split(" "));
        if (words.size() == 5) {
            Double start = parseDouble(words.get(1));
            Double end = parseDouble(words.get(3));
            if (start != null && end != null) {
                switch (words.get(4)) {
                    case "seconds":
                        return new TimeCondition(start.intValue(), end.intValue(), TimeCondition.TimeUnit.SECOND);
                    case "minutes":
                        return new TimeCondition(start.intValue(), end.intValue(), TimeCondition.TimeUnit.MINUTE);
                    case "hours":
                        return new TimeCondition(start.intValue(), end.intValue(), TimeCondition.TimeUnit.HOUR);
                    case "days":
                        return new TimeCondition(start.intValue(), end.intValue(), TimeCondition.TimeUnit.DAY);
                }
            }
        }
        return null;
    }

    public static Condition parseCorrelationCondition(String text) {
        List<String> words = Arrays.asList(text.toLowerCase().split(" "));
        if (words.size() == 2) {
            if (words.get(0).equals("same")) {
                return new CorrelationCondition(false, words.get(1));
            } else if (words.get(0).equals("different")) {
                return new CorrelationCondition(true, words.get(1));
            }
        }
        return null;
    }

    private static Condition actConditionFromBasics(String field, ConditionRelation relation, String value) {
        Double valueDouble = parseDouble(value);
        if (valueDouble != null) {
            return new ActivationNumCondition(field, relation, valueDouble);
        } else if (relation == ConditionRelation.EQUALS) {
            return new ActivationCatCondition(field, false, Collections.singletonList(value));
        } else if (relation == ConditionRelation.NOT_EQUAL) {
            return new ActivationCatCondition(field, true, Collections.singletonList(value));
        }
        return null;
    }

    private static Double parseDouble(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            //Google Speech API should parse most of the integers/doubles for us, but smaller ints might be left as is
            switch (text) {
                case "one":
                    return 1D;
                case "two":
                    return 2D;
                case "three":
                    return 3D;
                case "four":
                    return 4D;
                case "five":
                    return 5D;
                case "six":
                    return 6D;
                case "seven":
                    return 7D;
                case "eight":
                    return 8D;
                case "nine":
                    return 9D;
            }
        }
        return null;
    }
}
