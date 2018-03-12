package declareextraction.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

import declareextraction.constructs.Action;
import declareextraction.constructs.ConstraintType;
import declareextraction.constructs.DeclareConstraint;
import declareextraction.constructs.DeclareModel;

public class GSReader {


	public static List<DeclareModel> loadGSFile(File csvFile) {
		System.out.println("reading input file: " + csvFile.getAbsolutePath());
		if (csvFile.exists()) {
			try {
				List<DeclareModel> gsModels = new ArrayList<DeclareModel>();
				
				CSVReader reader = new CSVReader(new FileReader(csvFile.getAbsolutePath()), ';');
				reader.readNext(); // skip table header
				String[] nextLine;

				while ((nextLine = reader.readNext()) != null && nextLine.length > 1 && 
						!nextLine[0].isEmpty()) {
					DeclareModel gsModel = new DeclareModel(nextLine[4]);
					
					int gsconstraints = Integer.parseInt(nextLine[5]);
					boolean isNegative = nextLine[3].equalsIgnoreCase("true");
					for (int i = 0; i < gsconstraints; i++) {
						DeclareConstraint gsc;
						ConstraintType type = ConstraintType.getType(nextLine[7 + i * 3]);
						Action actionA = new Action(nextLine[8 + i * 3]);
						if (nextLine[9 + i * 3] == null || nextLine[9 + i * 3].isEmpty()) {
							gsc = new DeclareConstraint(type, actionA);
						} else {
							Action actionB = new Action(nextLine[9 + i * 3]);
							gsc = new DeclareConstraint(type, actionA, actionB);
						}
						if (isNegative) {
							gsc.setNegative();
						}
						gsModel.addConstraint(gsc);
					}
					gsModels.add(gsModel);
				}

				reader.close();
				return gsModels;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
