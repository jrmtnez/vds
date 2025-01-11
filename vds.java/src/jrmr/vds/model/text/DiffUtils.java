package jrmr.vds.model.text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jrmr.vds.model.ext.diff.diff_match_patch;
import jrmr.vds.model.ext.diff.diff_match_patch.Diff;
import jrmr.vds.model.ext.diff.diff_match_patch.LinesToCharsResult;
import jrmr.vds.model.ext.diff.diff_match_patch.Operation;

public class DiffUtils {

	private String concatenatedInsertedWords;
	private double averageTermFrequency;	
	private String insertedWordsList;
	private String insertedWordsFrequencyList;
	private String deletedWordsList;
	private String deletedWordsFrequencyList;


	public String getConcatenatedInsertedWords() {
		return concatenatedInsertedWords;
	}

	public double getAverageTermFrequency() {
		return averageTermFrequency;
	}

	public String getInsertedWordsList() {
		return insertedWordsList;
	}

	public String getInsertedWordsFrequencyList() {
		return insertedWordsFrequencyList;
	}
	
	public String getDeletedWordsList() {
		return deletedWordsList;
	}

	public String getDeletedWordsFrequencyList() {
		return deletedWordsFrequencyList;
	}

	public String calcInsertedText(String oldText, String newText) {

		diff_match_patch dmp = new diff_match_patch();

		dmp.Patch_Margin = 0;
		dmp.Match_Distance = 500;
		dmp.Diff_EditCost = 6;

		String insertedText = "";

		dmp = new diff_match_patch();
		LinesToCharsResult a = dmp.diff_linesToChars(oldText,newText);

		String lineText1 = a.chars1;
		String lineText2 = a.chars2;
		List<String> lineArray = a.lineArray;

		LinkedList<Diff> diffs = dmp.diff_main(lineText1, lineText2, false);

		dmp.diff_charsToLines(diffs, lineArray);

		for (Diff d : diffs) {
			if (d.operation == Operation.INSERT) {
				insertedText = insertedText + d.text;
			}
		}

		return insertedText;
	}

	public void calcInsertedAndDeletedWords(String oldText, String newText) {

		// extract weighted vocabularies
		VocabularyExtractor vocabularyExtractor = new VocabularyExtractor();

		HashMap<String,Integer> newV = vocabularyExtractor.getVocabularyCaseSensitive(newText);		
		HashMap<String,Integer> oldV = null;

		if (!oldText.isEmpty()) {
			oldV = vocabularyExtractor.getVocabularyCaseSensitive(oldText);
		}

		// create weighted diff vocabulary for inserted words
		HashMap<String,Integer> diffInsV = new HashMap<String,Integer>();							
		Iterator<Entry<String, Integer>> itIns = newV.entrySet().iterator();
		while (itIns.hasNext()) 
		{
			Map.Entry<String,Integer> newPair = (Map.Entry<String,Integer>)itIns.next();
			String word = newPair.getKey();
			int newCount = newPair.getValue();
			
			if (oldText.isEmpty()) {
				diffInsV.put(word, newCount);
			} else {				
				if (!oldV.containsKey(word))  {	    		    		
					diffInsV.put(word, newCount);	    		
				} else	{
					int oldCount = oldV.get(word).intValue();
					if (newCount > oldCount) {
						diffInsV.put(word, newCount - oldCount);	    			
					}
				}	    	
			}
		}

		concatenatedInsertedWords = "";
		averageTermFrequency = 0.0;
		double accumTermFrequency = 0.0;
		insertedWordsList = "";
		insertedWordsFrequencyList = "";
		int newNumberOfTokens = newText.split("[ \n\r]").length;
		int insertedWordsSize = 0;

		Iterator<Entry<String, Integer>> itIns2 = diffInsV.entrySet().iterator();
		while (itIns2.hasNext()) 
		{    	
			Map.Entry<String,Integer> diffPair = (Map.Entry<String,Integer>)itIns2.next();

			String word = diffPair.getKey();
			int wordCount = diffPair.getValue();
			
			insertedWordsSize = insertedWordsSize + wordCount;

			for (int i = 0; i < wordCount; i++) {
				if (concatenatedInsertedWords.equals("")) {
					concatenatedInsertedWords = word;	    			
				} else {    			
					concatenatedInsertedWords = word + " " + concatenatedInsertedWords;
				}	    		
			}	    	

			if (insertedWordsList.equals("")) {
				insertedWordsList = word;
				insertedWordsFrequencyList = String.valueOf(wordCount);
			} else {
				insertedWordsList = word + " " + insertedWordsList;
				insertedWordsFrequencyList = String.valueOf(wordCount) + " " + insertedWordsFrequencyList;
			}

			// accumulated frequency of inserted words in new text 
			accumTermFrequency = accumTermFrequency + (double)newV.get(word).intValue() / (double)newNumberOfTokens;	    		    	
		}	    
		
		//if (diffInsV.size() > 0) {
		//	averageTermFrequency = accumTermFrequency / (double)diffInsV.size();	    	
		//}		
		if (insertedWordsSize > 0) {
			averageTermFrequency = accumTermFrequency / (double)insertedWordsSize;	    	
		}		

		// create weighted difference vocabulary for deleted words
		deletedWordsList = "";
		deletedWordsFrequencyList = "";
		
		if (!oldText.isEmpty()) {

			HashMap<String,Integer> diffDelV = new HashMap<String,Integer>();							

			Iterator<Entry<String, Integer>> itDel = oldV.entrySet().iterator();
			while (itDel.hasNext()) 
			{				
				Map.Entry<String,Integer> oldPair = (Map.Entry<String,Integer>)itDel.next();
				String word = oldPair.getKey();
				int oldCount = oldPair.getValue();

				if (!newV.containsKey(word))  {	    		    		
					diffDelV.put(word, oldCount);
				} else	{					
					int newCount = newV.get(word).intValue();
					if (oldCount > newCount) {
						diffDelV.put(word, oldCount - newCount);
					}
				}
			}

			Iterator<Entry<String, Integer>> itDel2 = diffDelV.entrySet().iterator();
			while (itDel2.hasNext()) 
			{    	
				Map.Entry<String,Integer> diffPair = (Map.Entry<String,Integer>)itDel2.next();

				String word = diffPair.getKey();
				int wordCount = diffPair.getValue();
				
				if (deletedWordsList.equals("")) {
					deletedWordsList = word;
					deletedWordsFrequencyList = String.valueOf(wordCount);
				} else {
					deletedWordsList = word + " " + deletedWordsList;
					deletedWordsFrequencyList = String.valueOf(wordCount) + " " + deletedWordsFrequencyList;
				}
			}	    
		}
	}	
}
