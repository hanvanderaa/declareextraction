package declareextraction.utils;

public class CSVOutUtils {

	public static final char separator = ';';
	
	public static final String[] MTP_CSV_OVERVIEW_HEADER = 
			new String[]{"modelname", "# activities", "# sentences"};
	

	
	private static final String dts(int i) {
		return String.valueOf(i);
	}
	
	private static final String dts(double d) {
		String result = String.format("%.3f", d);
		return result.replace('.', ',');
	}
	
}
