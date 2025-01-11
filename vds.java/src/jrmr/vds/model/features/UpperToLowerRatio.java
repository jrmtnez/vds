package jrmr.vds.model.features;

public class UpperToLowerRatio extends BaseFeature {
	public double calculate(String caseSensitiveConcatenatedInsertedWords) {
		int uppercaseCount = 0;
		int lowercaseCount = 0;
		for (int i = 0; i < caseSensitiveConcatenatedInsertedWords.length(); i++) {		    
			Character c  = caseSensitiveConcatenatedInsertedWords.charAt(i);		    
		    if (!Character.isWhitespace(c)) {
				if (Character.isUpperCase(c)) {
			    	uppercaseCount++;
			    } else {
			    	lowercaseCount++;
			    }
		    } 
		}						
		return (double) (uppercaseCount + 1) / (double) (lowercaseCount + 1); 		
	}
}
