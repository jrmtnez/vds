package jrmr.vds.model.features;

public class NonAlphanumericRatio extends BaseFeature {
	public double calculate(String caseSensitiveConcatenatedInsertedWords) {
		int nonAlphanumericCount = 0;
		int allCharactersCount = 0;		
		for (int i = 0; i < caseSensitiveConcatenatedInsertedWords.length(); i++) {	
			
			Character c  = caseSensitiveConcatenatedInsertedWords.charAt(i);		    
		    if (!Character.isWhitespace(c)) {
		    	allCharactersCount++;
			    if (!Character.isLetterOrDigit(c)) {
			    	nonAlphanumericCount++;
			    }	
		    } 
		}						
		return (double) (nonAlphanumericCount + 1) / (double) (allCharactersCount + 1); 		
	}
}
