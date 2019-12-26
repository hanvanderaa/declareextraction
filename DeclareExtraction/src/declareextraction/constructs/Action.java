package declareextraction.constructs;

import java.util.HashSet;
import java.util.Set;

public class Action {

	private String modal;
	private String verb;
	private int verbID;
	private NounPhrase object;
	private NounPhrase subject;
	private String marker;
	private String clause;
	private String gstext;
	private Set<Action> conjunctions;
	private boolean pastParticiple;
	private boolean isNegative;
	private boolean isImmediate;
	private boolean isFlowAction;
	
	public Action(String verb, int verbID) {
		this.verb = verb;
		this.verbID = verbID;
		this.conjunctions = new HashSet<>();
		this.pastParticiple = false;
		this.isFlowAction = false;
	}

	
	public Action(String verb, String verbIDstr) {
		this(verb, Integer.parseInt(verbIDstr));
	}
	
	public Action(String gstext) {
		this.gstext = gstext.toLowerCase();
	}

	public void setModal(String modal) {
		this.modal = modal;
	}
	
	public String getModal() {
		if (modal == null) {
			return "";
		}
		return modal;
	}
	
	public String getVerb() {
		return verb;
	}

	public int getVerbID() {
		return verbID;
	}

	public NounPhrase getObject() {
		if (object == null) {
			return new NounPhrase();
		}
		return object;
	}

	public void setObject(NounPhrase object) {
		this.object = object;
	}

	public void setVerb(String verb, int verbID) {
		this.verb = verb;
		this.verbID = verbID;
	}
	
	
	public void setToFlowAction() {
		isFlowAction = true;
	}
	
	public boolean isFlowAction() {
		return isFlowAction;
	}

	public NounPhrase getSubject() {
		if (subject == null) {
			return new NounPhrase();
		}
		return subject;
	}

	public void setSubject(NounPhrase subject) {
		this.subject = subject;
	}
	
	public void setMarker(String marker) {
		this.marker = marker;
	}
	
	public String getMarker() {
		return marker;
	}
	
	public boolean hasMarker() {
		return marker != null;
	}

	
	public String getClause() {
		return clause;
	}
	
	public boolean hasClause() {
		return clause != null;
	}

	public void setClause(String clause) {
		this.clause = clause;
	}
	
	public void addConjunction(Action a) {
		conjunctions.add(a);
	}
	
	public Set<Action> getConjunctions() {
		return conjunctions;
	}
	
	public void setPastParticiple(boolean bool) {
		this.pastParticiple = bool;
	}
	
	public boolean isPastParticiple() {
		return pastParticiple;
	}

	public boolean isNegative() {
		return isNegative;
	}

	public void setNegative(boolean isNegative) {
		this.isNegative = isNegative;
	}

	public boolean isImmediate() {
		return isImmediate;
	}

	public void setImmediate(boolean isImmediate){
		this.isImmediate = isImmediate;
	}

	public String actionStr() {
		if (gstext != null) {
			return gstext;
		}
		StringBuilder sb = new StringBuilder();
		if (subject != null) {
			sb.append(subject).append(" ");
		}
//		if (modal != null) {
//			sb.append(modal + " ");
//		}
		if (isNegative) {
			sb.append("not ");
		}

		sb.append(verb);
		if (object != null && !object.getText().trim().isEmpty()) {
			sb.append(" ").append(object);
		}
		if (isImmediate) {
			sb.append(" immediately");
		}

		return sb.toString().toLowerCase();
	}

	public String baseStr() {
		//used for actionB for NOT and CHAIN relations, as those action parts are already part of the constraint type
		if (gstext != null) {
			return gstext;
		}

		StringBuilder sb = new StringBuilder();
		if (subject != null) {
			sb.append(subject).append(" ");
		}

		sb.append(verb);

		if (object != null && !object.getText().trim().isEmpty()) {
			sb.append(" ").append(object);
		}

		return sb.toString().toLowerCase();
	}
	
	public String toString() {
		return "Action: " + actionStr();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result + ((verb == null) ? 0 : verb.hashCode());
		result = prime * result + verbID;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Action other = (Action) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (verb == null) {
			if (other.verb != null)
				return false;
		} else if (!verb.equals(other.verb))
			return false;
		return verbID == other.verbID;
	}



	
	
	
	
}
