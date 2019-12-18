package declareextraction.constructs;

public class NounPhrase {

	private String text;
	private int startIndex;
	private int endIndex;
	
	public NounPhrase(String text, int startIndex, int endIndex) {
		super();
		this.text = text;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	public NounPhrase() {
		this.text = "";
		this.startIndex = -1;
		this.endIndex = -1;
	}
	public String getText() {
		return text;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public int getEndIndex() {
		return endIndex;
	}
	
	public boolean inSpan(int index) {
		return (startIndex <= index && index <= endIndex);
	}
	
	public String toString() {
		return text;
	}
	
	public boolean isEmpty() {
		return text.isEmpty();
	}
	
	
}
