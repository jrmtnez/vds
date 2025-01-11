package jrmr.vds.model.features;

public class DigitRatio extends BaseFeature {	
	public double calculate(String caseSensitiveConcatenatedInsertedWords) {
		int digitCount = 0;
		int allCharactersCount = 0;
		for (int i = 0; i < caseSensitiveConcatenatedInsertedWords.length(); i++) {	
			
			Character c  = caseSensitiveConcatenatedInsertedWords.charAt(i);		    
		    if (!Character.isWhitespace(c)) {
		    	allCharactersCount++;
			    if (Character.isDigit(c)) {
			    	digitCount++;
			    }
		    } 
		}						
		return (double) (digitCount + 1) / (double) (allCharactersCount + 1); 		
	}
}
