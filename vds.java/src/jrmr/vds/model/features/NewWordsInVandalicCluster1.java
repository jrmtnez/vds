package jrmr.vds.model.features;

import jrmr.vds.model.text.cluster.WordClustering;

public class NewWordsInVandalicCluster1 extends BaseFeature {
	public int calculate(String insertedWords, WordClustering wordClustering, boolean caseSensitive) {	
		String[] insertedWordsArray = insertedWords.split("[ \n\r]");
		int totalCount = insertedWordsArray.length;
    	int wordCount = 0;
		
		for (int i = 0; i < totalCount; i++) {
	    	String word = "";
	    	if (caseSensitive) {
	    		word = insertedWordsArray[i].toLowerCase();
	    	} else {	    		
	    		word = insertedWordsArray[i];
	    	}
			
	    	if (wordClustering.getClosestClusterClass1(word)) {
				wordCount++;
	    	}
		}
		return wordCount;
	}
}
