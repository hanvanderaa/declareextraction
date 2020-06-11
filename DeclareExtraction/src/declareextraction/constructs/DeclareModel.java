package declareextraction.constructs;

import java.util.ArrayList;
import java.util.List;

public class DeclareModel {

	String text;
	TextModel sentenceModel;
	int collection;
	int ID;
	
	List<DeclareConstraint> constraints;
	
	public DeclareModel(String text) {
		this.text = text;
		constraints = new ArrayList<>();
	}
	
	public DeclareModel(int collection, int ID, String text) {
		this.text = text;
		this.collection = collection;
		this.ID = ID;
		constraints = new ArrayList<>();
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
	
	public int getID() {
		return ID;
	}
	
	public int getCollection() {
		return collection;
	}


	public int getSize() {
		int res = 0;
		for (DeclareConstraint c : constraints) {
			res += c.size();
		}
		return res;
	}

	public boolean isEmpty() {
		return constraints.isEmpty();
	}
	
	
}
