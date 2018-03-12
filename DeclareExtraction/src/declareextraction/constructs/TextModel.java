package declareextraction.constructs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import declareextraction.textprocessing.Interrelation;

public class TextModel {

	private String text;
	private List<NounPhrase> nounPhrases;
	private List<Action> actions;
	private List<Interrelation> interrelations;
	private Map<Integer, Action> actionsMap;
	
	
	
	public TextModel(String text) {
		this.text = text;
		nounPhrases = new ArrayList<NounPhrase>();
		actions = new ArrayList<Action>();
		actionsMap = new HashMap<Integer, Action>();
		interrelations = new ArrayList<Interrelation>();
	}
	
	
	public void addNounPhrase(NounPhrase np) {
		nounPhrases.add(np);
	}
	
	public void addAction(Action a) {
		actions.add(a);
		actionsMap.put(a.getVerbID(), a);
	}
	
	public void addActionID(Action a, int index) {
		actionsMap.put(index,  a);
	}
	
	public List<Action> getActions() {
		return actions;
	}
	
	public Action getActionByVerbID(int index) {
		return actionsMap.get(index);
	}
	

	
	public NounPhrase getNounPhraseByIndex(int index) {
		for (NounPhrase np : nounPhrases) {
			if (np.inSpan(index)) {
				return np;
			}
		}
		return null;
	}
	
	public void addInterrelation(Interrelation rel) {
		interrelations.add(rel);
	}
	
	public String getText() {
		return text;
	}
	
	public List<Action> preceedingActions(Action a) {
		return actions.subList(0, actions.indexOf(a));
	}
	
	public List<Action> succeedingActions(Action a) {
		return actions.subList(actions.indexOf(a), actions.size() - 1);
	}


	public List<Interrelation> getInterrelations() {
		return interrelations;
	}
	
	public boolean containsInterrelation(Interrelation rel) {
		return interrelations.contains(rel);
	}


	public List<NounPhrase> getNounPhrases() {
		return nounPhrases;
	}
	
	
	
}
