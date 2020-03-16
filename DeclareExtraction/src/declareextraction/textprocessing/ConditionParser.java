package declareextraction.textprocessing;

import declareextraction.constructs.condition.*;
import declareextraction.constructs.condition.ActivationNumCondition.ConditionRelation;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionParser {

    private final static String andOrSeparator = "((?<=( (and|or) (?!equal to)))|(?=( (and|or) (?!equal to))))";
    private final static Pattern activatCat = Pattern.compile("^(.+?)( is(?: not)?(?: in| equal to)?|(?: not)?(?: in) )(.+)$");
    private final static Pattern activatNum = Pattern.compile("^(.+?)( (?:is )?(?:(greater|higher|more|smaller|lower|less) )than (?:or equal to )?)(.+)$");
    private final static Pattern timePattern1 = Pattern.compile("^(?:between (.*) and|in|no later than|at most) (.*) (days|hours|minutes|seconds)$");
    private final static Pattern timePattern2 = Pattern.compile("^(?:(?:not before|no earlier than) (.+?)(?: )?(days|hours|minutes|seconds)?) and (?:(?:within|not after|no later than) (.+?)(?: )?(days|hours|minutes|seconds)?)$");
    private final static Pattern corrPattern = Pattern.compile("^(.*) is (same|different)$");

    public static String parseCondition(String text, Condition.ConditionType type) {
        switch (type) {
            case ACTIVATION:
                return parseActivationConditions(text);
            case CORRELATION:
                return parseActivationOrCorrelationConditions(text);
            case TIME:
                Condition c = parseTimeCondition(text);
                return c == null ? null : c.toRuMString();
            default:
                return null;
        }
    }

    public static String parseActivationConditions(String text) {
        List<String> conditions = Arrays.asList(text.toLowerCase().split(andOrSeparator));
        Iterator<String> iter = conditions.iterator();
        StringBuilder result = new StringBuilder();
        while (iter.hasNext()) {
            String next = iter.next();
            if (next.equals(" and ") || next.equals(" or ")) {
                result.append(next);
                continue;
            }

            Condition c = parseActivationCondition(next);
            if (c != null) {
                result.append(c.toRuMString());
            }
        }

        if (result.length() > 0) {
            return result.toString();
        }

        return null;
    }

    public static ActivationCondition parseActivationCondition(String text) {
        Matcher m;
        if ((m = activatNum.matcher(text)).find()) {
            String field = String.join("_", m.group(1).split("\\s+"));
            boolean orEquals = m.group(2).contains("equal");
            boolean greater = m.group(3).equals("greater") || m.group(3).equals("higher") || m.group(3).equals("more");

            ConditionRelation rel;
            if (greater) {
                rel = orEquals ? ConditionRelation.GREATER_EQUAL : ConditionRelation.GREATER;
            } else {
                rel = orEquals ? ConditionRelation.LOWER_EQUAL : ConditionRelation.LOWER;
            }
            return actConditionFromBasics(field, rel, m.group(4).trim());
        } else if ((m = activatCat.matcher(text)).find()) {
            String field = String.join("_", m.group(1).split("\\s+"));
            boolean falseCond = m.group(2).contains("not");

            if (m.group(2).contains("in")) {
                return new ActivationCatCondition(field, falseCond, Arrays.asList(m.group(3).trim().split("\\s+")));
            } else {
                return actConditionFromBasics(field, falseCond ? ConditionRelation.NOT_EQUAL : ConditionRelation.EQUALS, m.group(3).trim());
            }

        }

        return null;
    }

    public static TimeCondition parseTimeCondition(String text) {
        Matcher m;
        if ((m = timePattern1.matcher(text)).find()) {
            Double start = m.group(1) == null ? 0D : parseDouble(m.group(1));
            Double end = parseDouble(m.group(2));
            return timeConditionFromBasics(start, end, m.group(3));
        } else if ((m = timePattern2.matcher(text)).find()) {
            Double start = parseDouble(m.group(1));
            Double end = parseDouble(m.group(3));
            if (m.group(2) != null) {
                return timeConditionFromBasics(start, end, m.group(2));
            } else if (m.groupCount() > 3) {
                return timeConditionFromBasics(start, end, m.group(4));
            }
        }

        return null;
    }

    public static CorrelationCondition parseCorrelationCondition(String text) {
        Matcher m;
        if ((m = corrPattern.matcher(text)).find()) {
            String field = String.join("_", m.group(1).split("\\s+"));
            if (m.group(2).equals("same")) {
                return new CorrelationCondition(false, field);
            } else {
                return new CorrelationCondition(true, field);
            }
        }

        return null;
    }

    public static String parseActivationOrCorrelationConditions(String text) {
        List<String> conditions = Arrays.asList(text.toLowerCase().split(andOrSeparator));
        Iterator<String> iter = conditions.iterator();
        StringBuilder result = new StringBuilder();
        while (iter.hasNext()) {
            String next = iter.next();
            if (next.equals(" and ") || next.equals(" or ")) {
                result.append(next);
                continue;
            }

            Condition c = activationOrCorrelation(next);
            if (c != null) {
                result.append(c.toRuMString());
            }
        }

        if (result.length() > 0) {
            return result.toString();
        }

        return null;
    }

    public static Condition activationOrCorrelation(String text) {
        Condition c = parseCorrelationCondition(text);
        return c == null ? parseActivationCondition(text) : c;
    }

    private static ActivationCondition actConditionFromBasics(String field, ConditionRelation relation, String value) {
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

    private static TimeCondition timeConditionFromBasics(Double start, Double end, String timeUnit) {
        if (start != null && end != null) {
            switch (timeUnit) {
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
