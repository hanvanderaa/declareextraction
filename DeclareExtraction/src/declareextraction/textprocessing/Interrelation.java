package declareextraction.textprocessing;

import declareextraction.constructs.Action;

public class Interrelation {

	public enum RelationType {
		FOLLOWS, XOR, AND;
	}
	
	Action actionA; 
	Action actionB;
	RelationType type;
	
	public Interrelation(Action actionA, Action actionB, RelationType type) {
		super();
		this.actionA = actionA;
		this.actionB = actionB;
		this.type = type;
	}

	public Action getActionA() {
		return actionA;
	}

	public Action getActionB() {
		return actionB;
	}

	public RelationType getType() {
		return type;
	}

	public void swapActions() {
		Action swap = this.actionA;
		this.actionA = this.actionB;
		this.actionB = swap;
		int i = 5;
	}

	@Override
	public String toString() {
		return "Interrelation [A=" + actionA + ", B=" + actionB + ", type=" + type + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionA == null) ? 0 : actionA.hashCode());
		result = prime * result + ((actionB == null) ? 0 : actionB.hashCode());
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
		Interrelation other = (Interrelation) obj;
		if (actionA == null) {
			if (other.actionA != null)
				return false;
		} else if (!actionA.equals(other.actionA))
			return false;
		if (actionB == null) {
			if (other.actionB != null)
				return false;
		} else if (!actionB.equals(other.actionB))
			return false;
		return true;
	}

	public void setActionA(Action actionA) {
		this.actionA = actionA;
		
	}
	
	public void setActionB(Action actionB) {
		this.actionB = actionB;
		
	}
}
