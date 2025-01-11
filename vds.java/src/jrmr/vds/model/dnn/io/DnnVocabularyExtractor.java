package jrmr.vds.model.dnn.io;

import java.util.HashMap;
import java.util.Map;

import jrmr.vds.model.io.FileUtils;
import jrmr.vds.model.text.VocabularyExtractor;

public class DnnVocabularyExtractor {
	
    public void extractPlainTextVocabulary() {
    	
    	System.out.println("Extracting vocabulary...");
    	
    	VocabularyExtractor vocabularyExtractor = new VocabularyExtractor();
    	FileUtils fileutils = new  FileUtils();

    	HashMap<String,Integer> allVoc = vocabularyExtractor.getVocabularyFromField(
    			"SELECT revision_text FROM pan_plain_text_article_revision_all");
    	
    	HashMap<String,Integer> filteredVoc = new HashMap<String,Integer>();
    	
    	for (Map.Entry<String, Integer> entry : allVoc.entrySet()) {
    		String word = entry.getKey();
    		
    		if (word.matches("[a-zA-Z]*")) {
    			filteredVoc.put(word, entry.getValue());	
    		}
    		
    	}		
    	
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_1_1.txt",1,1,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_10_1.txt",10,1,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_20_1.txt",20,1,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_50_1.txt",50,1,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_100_1.txt",100,1,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_250_1.txt",250,1,false);    	
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_500_1.txt",500,1,false);    	
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_1000_1.txt",1000,1,false);    	
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_2500_1.txt",2500,1,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_5000_1.txt",5000,1,false);    	
    	
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_1_2.txt",1,2,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_10_2.txt",10,2,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_20_2.txt",20,2,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_50_2.txt",50,2,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_100_2.txt",100,2,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_250_2.txt",250,2,false);    	
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_500_2.txt",500,2,false);    	
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_1000_2.txt",1000,2,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_2500_2.txt",2500,2,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_5000_2.txt",5000,2,false);    	
    	
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_1_3.txt",1,3,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_10_3.txt",10,3,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_20_3.txt",20,3,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_50_3.txt",50,3,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_100_3.txt",100,3,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_250_3.txt",250,3,false);    	
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_500_3.txt",500,3,false);    	
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_1000_3.txt",1000,3,false);    	
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_2500_3.txt",2500,3,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_5000_3.txt",5000,3,false);    	

    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_1_4.txt",1,4,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_10_4.txt",10,4,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_20_4.txt",20,4,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_50_4.txt",50,4,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_100_4.txt",100,4,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_250_4.txt",250,4,false);    	
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_500_4.txt",500,4,false);    	
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_1000_4.txt",1000,4,false);    	
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_2500_4.txt",2500,4,false);
    	fileutils.map2File(filteredVoc, "modeldnn\\plain_text_vocabulary_5000_4.txt",5000,4,false);    	

    }   
}
