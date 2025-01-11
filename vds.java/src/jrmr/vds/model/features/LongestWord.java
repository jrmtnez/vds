package jrmr.vds.model.features;

public class LongestWord extends BaseFeature {
	public int calculate(String insertedText) {			
		int longestWordLength = 0;		
		String[] insertedTextArray = insertedText.split("[ \n\r]");		
		for (int i = 0; i < insertedTextArray.length; i++) {
			if (insertedTextArray[i].length() > longestWordLength) {
				longestWordLength = insertedTextArray[i].length();
			}		
		}				
		return longestWordLength;	
	}
}
