package declareextraction.textprocessing;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import declareextraction.constructs.Action;
import declareextraction.constructs.NounPhrase;
import declareextraction.constructs.TextModel;
import declareextraction.utils.WordClasses;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

public class TextParser {


	private StanfordCoreNLP pipeline;
	private LexicalizedParser lp;
	private TokenizerFactory<CoreLabel> tokenizerFactory;
	
	
	public TextParser() {
		initialize();
	}
	
	private void initialize() {
		System.out.println("Loading NLP tools.");
		
	    String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	    lp = LexicalizedParser.loadModel(parserModel);
		tokenizerFactory =	PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		
	    Properties props;
	    props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos,lemma");
	    this.pipeline = new StanfordCoreNLP(props);

		System.out.println("Finished loading NLP tools.");
	}
	
	
	public TextModel parseConstraintString(String s) {
		
		TextModel model = new TextModel(s);
		extractNounPhrases(model);
		extractActivities(model);
		stanfordParsing(model);
		nounbasedProcessing(model);
		postRelationProcessing(model);
		
		return model;
	}

	private void extractNounPhrases(TextModel model) {
		Annotation sfparse = new Annotation(model.getText());
		this.pipeline.annotate(sfparse);

		edu.stanford.nlp.process.Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(model.getText()));

		List<CoreLabel> rawWords2 = tok.tokenize();
		Tree parse = lp.apply(rawWords2);
		

		for (Tree tree : parse) {
			if (tree.label().toString().equals("NP")) {
				StringBuilder sb = new StringBuilder();
				for (Tree leaf : tree.getLeaves()) {
					sb.append(leaf.label().toString() + " ");
				}
				int startIndex = Collections.indexOfSubList(parse.getLeaves(), tree.getLeaves());
				NounPhrase np = new NounPhrase(sb.toString().trim(), startIndex + 1, startIndex + tree.getLeaves().size());
				
				if (!isPartOfLargerNP(np, model.getNounPhrases())) {
					model.addNounPhrase(np);
				}
			}
		}
}
	
	private boolean isPartOfLargerNP(NounPhrase np, List<NounPhrase> nps) {
		for (NounPhrase np2 : nps) {
			if (np2.inSpan(np.getStartIndex())) {
				return true;
			}
		}
		return false;
	}
	

	private void extractActivities(TextModel model) {
		Annotation sfparse = new Annotation(model.getText());
		this.pipeline.annotate(sfparse);

		List<String> lemmas = new ArrayList<>();
		List<String> poss = new ArrayList<>();
		List<CoreMap> sentences = sfparse.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				lemmas.add(token.get(LemmaAnnotation.class));
				poss.add(token.get(PartOfSpeechAnnotation.class));
			}
			for (int i = 0; i < lemmas.size(); i++) {
				String pos = poss.get(i);
				String lemma = lemmas.get(i);
				if (pos.startsWith("VB")) {
					if (!lemma.equals("be") && !lemma.equals("have")) {
						Action action = new Action(lemma, i + 1);
						if (WordClasses.isFlowVerb(lemma)) {
							action.setToFlowAction();
						}
						model.addAction(action);
					}
				}
			}
		}
		System.out.println("sentence: " + model.getText());
		//System.out.println(lemmas);
		//System.out.println(poss);
	}
	
	private void stanfordParsing(TextModel model) {
		Annotation sfparse = new Annotation(model.getText());
		this.pipeline.annotate(sfparse);

		List<String> lemmas = new ArrayList<String>();
		List<CoreMap> sentences = sfparse.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				lemmas.add(token.get(LemmaAnnotation.class));
			}
		}		

		edu.stanford.nlp.process.Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(model.getText()));

		List<CoreLabel> rawWords2 = tok.tokenize();
		Tree parse = lp.apply(rawWords2);

		TreebankLanguagePack tlp = lp.getOp().langpack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		for (TypedDependency td : gs.typedDependenciesCCprocessed()) {
			String reln = td.reln().getShortName();
			Action govA = model.getActionByVerbID(td.gov().index());
			model.getActionByVerbID(td.dep().index());

			if (govA != null) {
				switch (reln) {
					case "nsubjpass":
					case "dobj":
						govA.setObject(model.getNounPhraseByIndex(td.dep().index()));
						break;
					case "nsubj":
						govA.setSubject(model.getNounPhraseByIndex(td.dep().index()));
						break;
					case "aux":
						govA.setModal(td.dep().originalText());
						break;
					case "neg":
						govA.setNegative(true);
						break;
					case "mark":
						govA.setMarker(td.dep().originalText());
						break;
					case "advmod":
						String originalText = td.dep().originalText();
						if(WordClasses.isImmediate(originalText)) {
							govA.setImmediate(true);
						}
						govA.setClause(originalText);
						break;
					case "conj":
						Action actB = model.getActionByVerbID(td.dep().index());
						if (actB != null) {
							govA.addConjunction(actB);
							actB.addConjunction(govA);
						}
						break;
				}
			}
		}

		for (TypedDependency td : gs.typedDependenciesCCprocessed()) {
			Action govAct = model.getActionByVerbID(td.gov().index()); 
			Action depAct = model.getActionByVerbID(td.dep().index());
			String reln = td.reln().getShortName();
			Action actA;
			Action actB;
			Set<Action> aActions = new HashSet<Action>();
			Set<Action> bActions = new HashSet<Action>();
			if (reln.equals("advcl") && govAct !=null && depAct != null) {
				if (govAct.getVerbID() < depAct.getVerbID()) {
					actA = govAct;
					actB = depAct;
				} else {
					actA = depAct;
					actB = govAct;
				}
				aActions.add(actA);
				aActions.addAll(actA.getConjunctions());
				bActions.add(actB);
				bActions.addAll(actB.getConjunctions());
				if (actA != null) {

					for (Action aAct : aActions) {
						for (Action bAct : bActions) {
							Interrelation rel = new Interrelation(aAct, bAct, Interrelation.RelationType.FOLLOWS);
							if (aAct.getObject().isEmpty()) {
								Action neighbor = findNeighborAction(model, aAct, bActions);
								mergeIncompleteActions(aAct, neighbor);
							}
							if (!model.containsInterrelation(rel)) {
								model.addInterrelation(rel);
							}
						}
					}
				}
			}
		}

	}
	
	private void postRelationProcessing(TextModel model) {

		// check if relations should be reverted
		for (Interrelation rel : model.getInterrelations()) {
			if (reversedCondition(rel.getActionB())) {
				Action currA = rel.getActionA();
				Action currB = rel.getActionB();
				rel.setActionA(currB);
				rel.setActionB(currA);
			}
		}
				
		// create default relations
		if (model.getInterrelations().isEmpty()) {
			if (model.getActions().size() > 1) {
				Action actA = model.getActions().get(0);
				Action actB = model.getActions().get(model.getActions().size() - 1);
				if (actA.getObject() == null) {
					Action neighbor = findNeighborAction(model, actA, new HashSet<Action>());
					mergeIncompleteActions(actA, neighbor);
				}
				Interrelation rel = new Interrelation(actA, actB, Interrelation.RelationType.FOLLOWS);
				model.addInterrelation(rel);
			}
		}
		

	}
	
	
	private void nounbasedProcessing(TextModel model) {
		Iterator<Action> iter = model.getActions().iterator();
		List<Action> newActions = new ArrayList<Action>();
		List<Action> removedActions = new ArrayList<Action>();
		while (iter.hasNext()) {
			Action action = iter.next();
			if (action.isFlowAction()) {
				String clause = "";
				//TODO: this is actually part of a more generic word class
				if (action.getVerb().startsWith("preced")) {
					clause = "first";
				}
				if (!action.getSubject().isEmpty()) {
					Action subAct = new Action(action.getSubject().toString(), -1);
					subAct.setClause(clause);
					newActions.add(subAct);
					for (Interrelation rel : model.getInterrelations()) {
						if (rel.getActionA().equals(action)) {
							rel.setActionA(subAct);
						}
						if (rel.getActionB().equals(action)) {
							rel.setActionB(subAct);
						}
					}
				}
				if (!action.getObject().isEmpty()) {
					Action objAct = new Action(action.getObject().toString(), -1);
					objAct.setClause(clause);
					newActions.add(objAct);
					for (Interrelation rel : model.getInterrelations()) {
						if (rel.getActionA().equals(action)) {
							rel.setActionA(objAct);
						}
						if (rel.getActionB().equals(action)) {
							rel.setActionB(objAct);
						}
					}
				}
				iter.remove();
				removedActions.add(action);
				
			}
		}
		for (Action newAction : newActions) {
			model.addAction(newAction);
		}

		if (model.getActions().size() == 1 && !removedActions.isEmpty()) {
			Action verbAction = model.getActions().remove(0);
			for (NounPhrase np : model.getNounPhrases()) {
				if (verbAction == null || np.getStartIndex() < verbAction.getVerbID()) {
					model.addAction(new Action(np.toString(), np.getStartIndex()));
				} else {
					model.addAction(verbAction);
					verbAction = null;
				}
			}
		}

		if (model.getActions().isEmpty()) {
			for (NounPhrase np : model.getNounPhrases()) {
				model.addAction(new Action(np.toString(), np.getStartIndex()));
			}
		}
		
		if (!newActions.isEmpty()) {
			//System.out.println(model.getText());
			//System.out.println(model.getActions());
			for (Interrelation rel : model.getInterrelations()) {
				System.out.println(rel);
			}
		}
	}
	
	private void mergeIncompleteActions(Action nullAction, Action neighbor) {
		if (neighbor != null) {
			System.out.println("merging activities: " + nullAction + " and: " + neighbor);

			String nullverb = nullAction.getVerb();
			nullAction.setObject(neighbor.getObject());
			if (nullverb.equals("be") || nullverb.equals("have") || nullverb.equals("have to")) {
				nullAction.setVerb(neighbor.getVerb(), neighbor.getVerbID());
			}
			if (WordClasses.isModal(nullverb)) {
				nullAction.setModal(nullverb);
				nullAction.setVerb(neighbor.getVerb(), neighbor.getVerbID());
			}

			System.out.println("Merged activity: " + nullAction);
		}
	}
	
	private Action findNeighborAction(TextModel model, Action nullAction, Set<Action> bActions) {
		Action closest = null;
		int mindist = Integer.MAX_VALUE;
		for (Action candidate : model.getActions()) {
			if (candidate != nullAction && !bActions.contains(candidate)) {
				int dist = Math.abs(  candidate.getVerbID() - nullAction.getVerbID());
				if (dist < mindist) {
					closest = candidate;
					mindist = dist;
				}
			}
		}
		return closest;
	}
	
	private boolean reversedCondition(Action actB) {
		// check for reverse order indicators, e.g. "must be created first"
		if (actB.hasClause() && WordClasses.isReverseClause(actB.getClause())) {
			return true;
		}
		if (actB.hasMarker() && WordClasses.isReverseMarker(actB.getMarker())) {
			return true;
		}
		
		return false;
	}
}
