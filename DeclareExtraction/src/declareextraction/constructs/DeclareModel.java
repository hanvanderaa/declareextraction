package declareextraction.constructs;

import java.util.ArrayList;
import java.util.List;

public class DeclareModel {

	String text;
	TextModel sentenceModel;
	
	List<DeclareConstraint> constraints;
	
	
	public DeclareModel(String text) {
		this.text = text;
		constraints = new ArrayList<DeclareConstraint>();
	}
		

	public String getText() {
		return text;
	}

	public void addTextModel(TextModel sm) {
		this.sentenceModel = sm;
	}
	
	public TextModel getSentenceModel() {
		return sentenceModel;
	}

	public List<DeclareConstraint> getConstraints() {
		return constraints;
	}

	public void addConstraint(DeclareConstraint c) {
		constraints.add(c);
	}


	public int getSize() {
		int res = 0;
		for (DeclareConstraint c : constraints) {
			res += c.size();
		}
		return res;
	}
	
	
}
