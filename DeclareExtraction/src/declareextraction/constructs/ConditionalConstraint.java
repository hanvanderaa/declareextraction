package declareextraction.constructs;

public class ConditionalConstraint {

    ConstraintType type;
    String subject;
    ConditionRelation relation;
    Double comparative;

    public ConditionalConstraint(ConstraintType type, String subject, ConditionRelation relation, Double comparative) {
        this.type = type;
        this.subject = subject;
        this.relation = relation;
        this.comparative = comparative;
    }

    public ConstraintType getType() {
        return type;
    }

    public void setType(ConstraintType type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public ConditionRelation getRelation() {
        return relation;
    }

    public void setRelation(ConditionRelation relation) {
        this.relation = relation;
    }

    public Double getComparative() {
        return comparative;
    }

    public void setComparative(Double comparative) {
        this.comparative = comparative;
    }

    @Override
    public String toString() {
        return subject + " " + relation.getConditionSymbol() + " " + comparative;
    }

    //Is ACTIVATION/CORRELATION/TIME somehow used in logic? e.g. ACTIVATION adds A. to subject? RuM examples 'A.Price > 10'
    //'Not' (!) not used in RuM conditions?
    //Handle 'same' and 'different' relations? e.g. 'same Price'

    //Possible case? - Activation price is higher than 5 and target time is greater than activation time
    //Relations between activation and target or just A/T and doubles?
    //Handle multiple conditions (and/or)?
    //Also: handle greater/smaller than or equal to?
    //If so then also automatic conversion from not smaller -> greater or equal to & not smaller or equal -> greater?
}
