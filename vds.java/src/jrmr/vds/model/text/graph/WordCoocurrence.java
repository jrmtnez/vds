package jrmr.vds.model.text.graph;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import jrmr.vds.model.db.DatabaseUtils;
import jrmr.vds.model.io.FileUtils;
import jrmr.vds.model.text.VocabularyExtractor;

public class WordCoocurrence {
	
	public String calcRegularCoocurrenceMatrix(int partitionSize, int minLength, int maxNumberOfInstances, boolean withoutNumbers, int initialOffset) {
		
		int regularInstances = 14079;
		if (maxNumberOfInstances != 0) {
			regularInstances = maxNumberOfInstances; 
		}
		
		int numberOfPartitions = regularInstances / partitionSize;
		
		System.out.println("Number of partitions: " + numberOfPartitions);
		
		DatabaseUtils databaseUtils = new DatabaseUtils();		
		
//		String sqlStatement = "SELECT inserted_words_list FROM words_diff_training WHERE revision_class = 'regular'";			
		String sqlStatement = "SELECT inserted_words_list FROM words_diff_training WHERE revision_class = 'regular' ORDER BY sorting_column";			
				
		databaseUtils.openConnection();
		String[] docs = new String[numberOfPartitions];						
		for (int i = 0; i < numberOfPartitions; i++) {
			docs[i] = databaseUtils.getDocFromField(sqlStatement, partitionSize, i * partitionSize + initialOffset);
		}
		databaseUtils.closeConnection();
		
		VocabularyExtractor vocabularyExtractor = new VocabularyExtractor();
    	HashMap<String,Integer> vocMap = vocabularyExtractor.getVocabularyFromField(sqlStatement, minLength, withoutNumbers);
    	
		int vocSize = vocMap.size();
		String[] v = new String[vocSize];		
		int r = 0;
		for (Map.Entry<String, Integer> entry : vocMap.entrySet()) {
			entry.setValue(r);
			v[r] = entry.getKey();			
			r++;						
		}
		
		System.out.println("Vocabulary size: " + vocSize);
		
    	int[][] cm = new int[vocSize][vocSize];
		
		for (int i = 0; i < vocSize; i++) {
			System.out.println("Processing " + (int) ((float) (i + 1) / (float) vocSize * 100) + "%: " + v[i]);
			for (int k = 0; k < docs.length; k++) {				
				HashMap<String,Integer> docVocMap = 
					vocabularyExtractor.getVocabularyCaseSensitive(
//						docs[k].toLowerCase(), // words diff table is non case sensitive
						docs[k],
						minLength, 
						withoutNumbers);
				if (docVocMap.containsKey(v[i])) {								
					String[] docArray = docs[k].split("[ \n\r]");					
					for (int l = 0; l < docArray.length; l++) {						
						if (!docArray[l].equals("") && (vocMap.get(docArray[l]) != null)) {
							int j = vocMap.get(docArray[l]);
							if (j > i) {
								cm[i][j]++;
							}
						}
					}
				}
			}
		}
		
		System.out.println("Processed words: " + vocSize);		
		System.out.println("Exporting to GraphML format...");
		
		String fileName = "model//coocurrence_regular_" + initialOffset; 
		
		saveToGraphML(fileName, cm, v);
		
		return fileName;		
	}
	
	public String calcVandalicCoocurrenceMatrix(
			int partitionSize, 
			int minLength, 
			int maxNumberOfInstances, 
			boolean withoutNumbers, 
			String stopWordsList) {
			
		int vandalicInstances = 921;
		if (maxNumberOfInstances != 0) {
			vandalicInstances = maxNumberOfInstances; 
		}
		
		int numberOfPartitions = vandalicInstances / partitionSize;
		
		System.out.println("Number of partitions: " + numberOfPartitions);
		
		FileUtils fileutils = new  FileUtils();
		//HashMap<String,Integer> stopWords = fileutils.file2Map(stopWordsList + ".txt");
		HashMap<String,Integer> stopWords = fileutils.file2Map(stopWordsList);
		
		DatabaseUtils databaseUtils = new DatabaseUtils();		
//		String sqlStatement = "SELECT inserted_words_list FROM words_diff_training WHERE revision_class = 'vandalism'";
		String sqlStatement = "SELECT inserted_words_list FROM words_diff_training WHERE revision_class = 'vandalism' ORDER BY sorting_column";		
				
		databaseUtils.openConnection();
		String[] docs = new String[numberOfPartitions];						
		for (int i = 0; i < numberOfPartitions; i++) {
			docs[i] = databaseUtils.getDocFromField(sqlStatement, partitionSize, i * partitionSize);
		}
		databaseUtils.closeConnection();
		
		VocabularyExtractor vocabularyExtractor = new VocabularyExtractor();
    	HashMap<String,Integer> vocMap = vocabularyExtractor.getVocabularyFromField(sqlStatement, stopWordsList, minLength, withoutNumbers);
    	
		int vocSize = vocMap.size();
		String[] v = new String[vocSize];		
		int r = 0;
		for (Map.Entry<String, Integer> entry : vocMap.entrySet()) {
			entry.setValue(r);
			v[r] = entry.getKey();			
			r++;						
		}
		
		System.out.println("Vocabulary size: " + vocSize);
		
    	int[][] cm = new int[vocSize][vocSize];
		
		for (int i = 0; i < vocSize; i++) {
			System.out.println("Processing " + (int) ((float) (i + 1) / (float) vocSize * 100) + "%: " + v[i]);
			for (int k = 0; k < docs.length; k++) {				
				HashMap<String,Integer> docVocMap = 
					vocabularyExtractor.getVocabularyCaseSensitive(
//						docs[k].toLowerCase(), // words diff table is non case sensitive 
						docs[k],
						stopWordsList, 
						minLength, 
						withoutNumbers);
				if (docVocMap.containsKey(v[i])) {								
					String[] docArray = docs[k].split("[ \n\r]");					
					for (int l = 0; l < docArray.length; l++) {
						if (!docArray[l].equals("") && !stopWords.containsKey(docArray[l]) && (vocMap.get(docArray[l]) != null)) {
							int j = vocMap.get(docArray[l]);
							if (j > i) {
								cm[i][j]++;
							}
						}
					}
				}
			}
		}
		
		System.out.println("Processed words: " + vocSize);		
		System.out.println("Exporting to GraphML format...");
		
		String fileName = "model//coocurrence_vandalism"; 
		
		saveToGraphML(fileName, cm, v);
		
		return fileName;
	}


	public void calcTinyCoocurrenceMatrix() {
		String[] docs = new String[6];
	
		docs[0] = "A B C";
		docs[1] = "A C D";
		docs[2] = "A E F";
		docs[3] = "A B G";
		docs[4] = "A H I";
		docs[5] = "A B J";
		
		String unifiedDoc = docs[0] + " " + docs[1] + " " + docs[2] + " " + docs[3] + " " + docs[4] + " " + docs[5];
		
		VocabularyExtractor vocabularyExtractor = new VocabularyExtractor();
		HashMap<String,Integer> vocMap = vocabularyExtractor.getVocabularyCaseSensitive(unifiedDoc);			
		int vocSize = vocMap.size();
		String[] v = new String[vocSize];
		
		System.out.print("Vocabulary: ");
		int r = 0;
		for (Map.Entry<String, Integer> entry : vocMap.entrySet()) {
			entry.setValue(r);
			v[r] = entry.getKey();			
			r++;					
			System.out.print(entry.getKey() + " ");			
		}
		System.out.println();
		System.out.println();
				
		int[][] cm = new int[vocSize][vocSize];
		
		for (int i = 0; i < vocSize; i++) {
			for (int k = 0; k < docs.length; k++) {				
				HashMap<String,Integer> docVocMap = vocabularyExtractor.getVocabularyCaseSensitive(docs[k]);
				if (docVocMap.containsKey(v[i])) {									
					String[] docArray = docs[k].split("[ \n\r]");					
					for (int l = 0; l < docArray.length; l++) {
						int j = vocMap.get(docArray[l]);
						if (j > i) {
							cm[i][j]++;
						}
					}
				}
			}
		}
		
		printMatrix(cm, v);
		
		saveToGraphML("tiny", cm, v);
	}
	
	public void printMatrix(int[][] m, String[] v) {

		System.out.print("  ");
		for (int i = 0; i < v.length; i++) {
			System.out.print(v[i] + " ");
		}
		System.out.println();

		for (int i = 0; i < v.length; i++) {
			System.out.print(v[i] + " ");
			for (int j = 0; j < v.length; j++) {
				if (m[i][j] == 0) {
					System.out.print("  ");
				} else {
					System.out.print(m[i][j] + " ");					
				}	
			}
			System.out.println();
		}
		System.out.println();
	}
			
	public void saveToGraphML(String graphName, int[][] m, String[] v) {		
		try {
			OutputStream outputStream;
			outputStream = new FileOutputStream(graphName + ".graphml");

			Writer outputWriter = new OutputStreamWriter(outputStream,Charset.forName("UTF-8"));
			outputWriter = new BufferedWriter(outputWriter);
						
			outputWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			outputWriter.write("  <graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n");  
			outputWriter.write("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
			outputWriter.write("  xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns \n");
			outputWriter.write("  http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\"\n");
			outputWriter.write(">\n");						
			outputWriter.write("    <key id=\"weight\" for=\"edge\" attr.name=\"weight\" attr.type=\"int\"/>\n");
			outputWriter.write("    <key id=\"label\" for=\"node\" attr.name=\"label\" attr.type=\"string\"/>\n");			
			outputWriter.write("    <graph id=\"" + graphName + "\" edgedefault=\"undirected\">\n");
			
			for (int i = 0; i < v.length; i++) {
				outputWriter.write("        <node id=\"" + i + "\">\n");
				outputWriter.write("            <data key=\"label\">" + v[i] + "</data>\n");				
				outputWriter.write("        </node>\n");				
			}
			
			for (int i = 0; i < v.length; i++) {
				for (int j = 0; j < v.length; j++) {
					if (m[i][j] != 0) {
						outputWriter.write("        <edge  source=\"" + i + "\" target=\"" + j + "\">\n");
						outputWriter.write("            <data key=\"weight\">" + m[i][j] + "</data>\n");						
						outputWriter.write("        </edge>\n");
					}	
				}
			}
			
			outputWriter.write("    </graph>\n");
			outputWriter.write("</graphml>\n");
			
			outputWriter.flush();
			outputWriter.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}		
	}	
}

