package jrmr.vds.model.features;

import jrmr.vds.model.text.WordList;

public class BaseFrequency extends BaseFeature {
	private WordList wordList;		
	private int totalCount;
	private int wordCount;

	public int getTotalCount() {
		return totalCount;
	}

	public int getWordCount() {
		return wordCount;
	}

	public double calculate(String insertedWords, String wordListFile, boolean caseSensitive) {	
		wordList = new WordList(wordListFile);		
		String[] insertedWordsArray = insertedWords.split("[ \n\r]");
		totalCount = insertedWordsArray.length;
    	wordCount = 0;
		
		for (int i = 0; i < totalCount; i++) {
	    	String word = "";
	    	if (!caseSensitive) {
	    		word = insertedWordsArray[i].toLowerCase();
	    	} else {	    		
	    		word = insertedWordsArray[i];
	    	}
			
			if (wordList.findTerm(word)) {
				wordCount++;

	    	}
		}
		return frequency(wordCount, totalCount);
	}
	
	public double frequency(double wordCount, double totalCount) {
		if (totalCount != 0) {
			return (double) wordCount / (double) totalCount; 
		} else {
			return 0.0;			
		}			
	}
}
