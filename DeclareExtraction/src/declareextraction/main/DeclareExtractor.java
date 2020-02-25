package declareextraction.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import declareextraction.constructs.Action;
import declareextraction.constructs.DeclareModel;
import declareextraction.constructs.TextModel;
import declareextraction.evaluation.Evaluator;
import declareextraction.io.GSReader;
import declareextraction.io.ResultsWriter;
import declareextraction.textprocessing.DeclareConstructor;
import declareextraction.textprocessing.Interrelation;
import declareextraction.textprocessing.TextParser;

public class DeclareExtractor {

	static final int START = 0;
	static final int END = 108;
	
	
	public static final String CONSTRAINT_FILE = "input/datacollection.csv";
	public static final String RESULTS_FILE = "output/results.csv";
	
	
	List<String> texts;
	List<DeclareModel> gsModels;
	List<DeclareModel> genModels;
	List<Object[]> evalresultsList;
	
	TextParser parser;
	private DeclareConstructor declareConstructor;
	

	public static void main(String[] args) {
		DeclareExtractor t = new DeclareExtractor();
//		if (args.length == 0) {
//			t.runApproachOnCSVFile(CONSTRAINT_FILE);
//		}
//		else {
			String test = "A claim must be created before it is approved";
			String[] input = new String[]{
//					"A claim must be created before it is approved",
//		"If a claim is approved then it must have been created first",
//					"If an order is sent then an order must be received",
//		"Open order must be followed by close order",
//		"Switch on light must be preceded by switch off light",
//					"Switching on the light must be preceded by switching off the light",

//		"Close door must eventually be followed by open door",
		"A meeting must first be arranged and it shall be held afterwards",
					"A meeting must first be arranged and it shall be held after",
		};
			for (int i = 0; i < input.length; i++) {
				t.runSingleConstraint(input[i]);
				System.out.println("\n");
			}
//		}
	}
	
	
	private void runSingleConstraint(String text) {
		parser = new TextParser();
		declareConstructor = new DeclareConstructor();
		
		System.out.println("Parsing case: " + text);
		TextModel textModel = parser.parseConstraintString(text);
		
		System.out.println("Extracted actions:");
		for (Action a : textModel.getActions()) {
			System.out.println(a);
		}
		
		System.out.println("Extracted relations:");
		for (Interrelation rel : textModel.getInterrelations()) {
			System.out.println(rel);
		}
		
		DeclareModel dm = declareConstructor.convertToDeclareModel(textModel);
			dm.addTextModel(textModel);
	}
	
	
	private void runApproachOnCSVFile(String csvInputPath) {
		parser = new TextParser();
		declareConstructor = new DeclareConstructor();
		Evaluator eval = new Evaluator();
		genModels = new ArrayList<DeclareModel>();
		evalresultsList = new ArrayList<Object[]>();
		
				
		loadCases(csvInputPath);
			
		
		int b = 0;
		for (int i = 0; i < texts.size(); i++) {
			
			if (b >= START && b <= END) {
				DeclareModel gsModel = gsModels.get(i);			
				
				if (true) {
//				if (gsModel.getConstraints().size() == 1) {

					String text = texts.get(i);
					System.out.println(i + " Parsing case: " + text);
					TextModel textModel = parser.parseConstraintString(text);

					System.out.println("Extracted actions:");
					for (Action a : textModel.getActions()) {
						System.out.println(a);
					}
					System.out.println("Extracted relations:");
					for (Interrelation rel : textModel.getInterrelations()) {
						System.out.println(rel);
					}

					DeclareModel dm = declareConstructor.convertToDeclareModel(textModel);
					dm.addTextModel(textModel);

					genModels.add(dm);

					Object[] evalres = eval.evaluateCase(dm, gsModel);
					evalresultsList.add(evalres);

				}
			}
			b++;
		}
		eval.printOverallResults();
		ResultsWriter.write(RESULTS_FILE, genModels, gsModels, evalresultsList);
	}
	
	
	private void loadCases(String csvInputPath) {
		gsModels = GSReader.loadGSFile(new File(csvInputPath));
		texts = new ArrayList<String>();
		for (DeclareModel dm : gsModels) {
			texts.add(dm.getText());
		}
	}

}
