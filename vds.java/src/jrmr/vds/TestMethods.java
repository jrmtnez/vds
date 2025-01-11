package jrmr.vds;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jrmr.vds.model.config.Config;
import jrmr.vds.model.db.DatabaseUtils;
import jrmr.vds.model.dnn.io.VSModelUtils;
import jrmr.vds.model.dnn.io.WordObfuscationUtils;
import jrmr.vds.model.ext.weka.DatasetUtils;
import jrmr.vds.model.ext.weka.J48Classifier;
import jrmr.vds.model.ext.weka.RandomForestClassifier;
import jrmr.vds.model.ext.word2vec.Word2Vec;
import jrmr.vds.model.io.FileUtils;
import jrmr.vds.model.io.WikipediaApiQuerys;
import jrmr.vds.model.pan.PanEdit;
import jrmr.vds.model.text.CompressibilityUtils;
import jrmr.vds.model.text.DiffUtils;
import jrmr.vds.model.text.EntropyUtils;
import jrmr.vds.model.text.PlainTextExtractor;
import jrmr.vds.model.text.Tokenizer;
import jrmr.vds.model.text.VocabularyExtractor;
import jrmr.vds.model.text.cluster.WordClustering;

@SuppressWarnings("unused")
public class TestMethods {

	
	public static void main(String[] args) {
//		TestCalcInsertedAndDeletedWords();
//		TestCalcInsertedText();
		
		ProcessPool processPool;
		FileUtils fileUtils = new FileUtils();
		
		fileUtils.copyFile("vds.properties.training", "vds.properties");
		processPool = new ProcessPool();
//    	processPool.extractWordsDiffsOfRevisions();    	    	
    	processPool.loadRevisions();
		
		System.out.println("Suceed...");
		
	}
	
	
	public void TestVocabularyExtractor() {
		FileUtils fileUtils = new FileUtils();
		String inputText = fileUtils.file2String("test//old_revision_text.txt");
		System.out.println(inputText);
		
		VocabularyExtractor vocabularyExtractor = new VocabularyExtractor();
		HashMap<String,Integer> v = vocabularyExtractor.getVocabularyCaseSensitive(inputText);
		
		System.out.println("Vocabulario:");
		for (Map.Entry<String, Integer> entry : v.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	
	}
	
    public static void TestCalcInsertedAndDeletedWords() {

    	FileUtils fileUtils = new FileUtils();    	
    	Tokenizer tokenizer = new Tokenizer();

    	System.out.println("Tokenizing...");
    	String oldRevisionText = fileUtils.file2String("test//old_revision_text.txt");        
    	String newRevisionText = fileUtils.file2String("test//new_revision_text.txt");
    	String oldTokenizedRevisionText = tokenizer.tokenize(oldRevisionText, "config//wikitokens.txt");
    	String newTokenizedRevisionText = tokenizer.tokenize(newRevisionText, "config//wikitokens.txt");
    	
    	System.out.println("Extracting plain text...");
    	PlainTextExtractor plainTextExtractor = new PlainTextExtractor();
    	String oldPlainTextRevisionText = plainTextExtractor.extractWithLines(oldTokenizedRevisionText, "config//wikitokens.txt", true);
    	String newPlainTextRevisionText = plainTextExtractor.extractWithLines(newTokenizedRevisionText, "config//wikitokens.txt", true);    	
    	
    	System.out.println("Extracting diffs...");
    	DiffUtils diffUtils = new DiffUtils();
//		diffUtils.calcInsertedAndDeletedWords(oldPlainTextRevisionText.toLowerCase(), newPlainTextRevisionText.toLowerCase());				
		diffUtils.calcInsertedAndDeletedWords(oldPlainTextRevisionText, newPlainTextRevisionText);				
		
		System.out.println(diffUtils.getInsertedWordsList()); 
		System.out.println(diffUtils.getInsertedWordsFrequencyList());				
		System.out.println(diffUtils.getDeletedWordsList()); 
		System.out.println(diffUtils.getDeletedWordsFrequencyList());	

    }
    
    
    public static void TestCalcInsertedText()  {
    	
    	FileUtils fileUtils = new FileUtils();    	
    	Tokenizer tokenizer = new Tokenizer();
    	PlainTextExtractor plainTextExtractor = new PlainTextExtractor();
    	DiffUtils diffUtils = new DiffUtils();
    	
    	System.out.println("Tokenizing...");
    	String oldRevisionText = fileUtils.file2String("test//old_revision_text.txt");        
    	String newRevisionText = fileUtils.file2String("test//new_revision_text.txt");
			
		String insertedText = diffUtils.calcInsertedText(oldRevisionText, newRevisionText);					
		String insertedTextTokenized = tokenizer.tokenize(insertedText, "config//wikitokens.txt");
		String insertedTextPlain = plainTextExtractor.extractWithLines(insertedTextTokenized, "config//wikitokens.txt", false);
		
		System.out.println(insertedText);				
		System.out.println(insertedTextTokenized); 
		System.out.println(insertedTextPlain);

    }
	
}
