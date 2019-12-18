package declareextraction.io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVWriter;

import declareextraction.constructs.DeclareConstraint;
import declareextraction.constructs.DeclareModel;
import declareextraction.evaluation.Evaluator;
import declareextraction.utils.CSVOutUtils;

public class ResultsWriter {

	public static void write(String path, List<DeclareModel> generatedModels, List<DeclareModel> gsModels, List<Object[]> evalresults) {
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(path), CSVOutUtils.separator);
			String[] header = new String[]{"ID", "Description", "GsSlots", "gen slots", "Correct slots", "Precision", 
					"Recall", "# generated cons", "# gs cons", "GS type", "type error", "Actions", "Cons1", "gsCons1", "Cons2", "gsCons2","Cons3", "gsCons3"};
			
			writer.writeNext(header);
			
			for (int i = 0; i < generatedModels.size(); i++) {
				DeclareModel m = generatedModels.get(i);
				DeclareModel gsModel = gsModels.get(i);
				
				Object[] evalres = evalresults.get(i);
				Map<DeclareConstraint, DeclareConstraint> matches = (Map<DeclareConstraint, DeclareConstraint>) evalres[2];

				List<Object> rowList = new ArrayList<Object>();
				
				rowList.add(i);
				rowList.add(m.getText());
				rowList.add(gsModel.getSize());
				rowList.add(m.getSize());
				rowList.add(correctSlots(matches));
				rowList.add(evalres[0]);
				rowList.add(evalres[1]);
				rowList.add(m.getConstraints().size());
				rowList.add(gsModel.getConstraints().size());
				rowList.add(gsModel.getConstraints().get(0).getType());
				rowList.add(!correctType(matches));
				rowList.add(m.getSentenceModel().getActions().toString());
				
				
				for (DeclareConstraint c : matches.keySet()) {
					rowList.add(c.toString());
					DeclareConstraint match = matches.get(c);
					if (match != null) {
						rowList.add(matches.get(c).toString());
					} else {
						rowList.add("no match");
					}
				}
				
				String[] row = new String[rowList.size()];
				for (int j = 0; j < row.length; j++) {
					row[j] = String.valueOf(rowList.get(j));
				}
				writer.writeNext(row);
				
				
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static int correctSlots(Map<DeclareConstraint, DeclareConstraint> matches) {
		Evaluator eval = new Evaluator();
		
		int res = 0;
		for (DeclareConstraint c : matches.keySet()) {
			res += eval.quantifyMatch(c, matches.get(c));
		}
		return res;
	}
	
	private static boolean correctType(Map<DeclareConstraint, DeclareConstraint> matches) {
		for (DeclareConstraint c : matches.keySet()) {
			if (matches.get(c) == null || c.getType() == matches.get(c).getType()) {
				return true;
			}
		}
		return false;
	}

}
