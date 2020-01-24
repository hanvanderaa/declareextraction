package declareextraction.constructs.condition;

import java.util.ArrayList;
import java.util.List;

public class ActivationCatConstraint extends ConditionalConstraint {

    private String field;
    private boolean isNegative;
    private List<String> categories;

    public ActivationCatConstraint(String field, boolean isNegative, List<String> categories) {
        this.field = field;
        this.isNegative = isNegative;
        this.categories = new ArrayList<>();
        this.categories.addAll(categories);
    }

    @Override
    public String toRuMString() {
        StringBuilder sb = new StringBuilder().append("A.").append(field);

        if (categories.size() == 1) {
            sb.append(isNegative ? " is not " : " is ");
            sb.append(String.join(", ", categories));
        } else {
            //TODO: this should cover the case of 0 categories, but also throw an error in logic
            sb.append(isNegative ? " not in (" : " in (");
            sb.append(String.join(", ", categories));
            sb.append(')');
        }

        return sb.toString();
    }
}
