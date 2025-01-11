package jrmr.vds.model.features;

import jrmr.vds.model.text.WordList;

public class BaseImpact extends BaseFeature {
	private WordList wordList;
	private int wordOldCount;
	private int wordNewCount;	

	public int getWordOldCount() {
		return wordOldCount;
	}

	public int getWordNewCount() {
		return wordNewCount;
	}

	public double calculate(String oldTokenizedRevisionText, String newTokenizedRevisionText, 
			String wordListFile, boolean caseSensitive) {
		
		wordList = new WordList(wordListFile);		
		
		int totalOldCount = 0;    	
    	wordOldCount = 0;
		if (oldTokenizedRevisionText != null) {			
			String[] oldTokenizedRevisionTextArray = oldTokenizedRevisionText.split("[ \n\r]");
			totalOldCount = oldTokenizedRevisionTextArray.length;    			
			for (int i = 0; i < totalOldCount; i++) {
				String word = "";
		    	if (caseSensitive) {
		    		word = oldTokenizedRevisionTextArray[i].toLowerCase();
		    	} else {	    		
		    		word = oldTokenizedRevisionTextArray[i];
		    	}
				
				if (wordList.findTerm(word)) {
					wordOldCount++;
		    	}
			}			
		}
		
		String[] newTokenizedRevisionTextArray = newTokenizedRevisionText.split("[ \n\r]");
		int totalNewCount = newTokenizedRevisionTextArray.length;    	
    	wordNewCount = 0;	
		for (int i = 0; i < totalNewCount; i++) {
			String word = "";
	    	if (caseSensitive) {
	    		word = newTokenizedRevisionTextArray[i].toLowerCase();
	    	} else {	    		
	    		word = newTokenizedRevisionTextArray[i];
	    	}
			
			if (wordList.findTerm(word)) {
				wordNewCount++;
	    	}
		}
		return impact(wordOldCount, wordNewCount);
	}
	
	public double impact(double wordOldCount, double wordNewCount) {
		double impact = 0.0;		
		if ((wordOldCount == 0) && (wordNewCount == 0)) {
			impact = 0.5;
		} else {
			impact = wordOldCount / (wordOldCount + wordNewCount);
		}			
		return impact;
	}
}
