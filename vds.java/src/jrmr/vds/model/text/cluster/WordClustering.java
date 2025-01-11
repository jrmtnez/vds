package jrmr.vds.model.text.cluster;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jrmr.vds.model.db.DatabaseUtils;
import jrmr.vds.model.ext.word2vec.VectorModel;
import jrmr.vds.model.text.WordList;

public class WordClustering {

	WordClustersSet wordClustersSet;
	VectorModel vectorModel;
	int textLength;
	int vectorSize;

	public WordClustering() {
		wordClustersSet = new WordClustersSet();
	}

	public void loadVectorModel(String modelFilePath){
		if (vectorModel == null) {
			System.out.println("Loading vector model " + modelFilePath);
			vectorModel = VectorModel.loadFromFile(modelFilePath);
			vectorSize = vectorModel.getVectorSize();
		}
	}    

	public WordCluster getClosestCluster(String word) {

		float minDist = 0.0f;				
		WordCluster selectedCluster = null;
		float[] wordVector = vectorModel.getWordVector(word);

		if (wordVector == null) {
			wordVector = new float[vectorSize];
		}

		for (Map.Entry<String, WordCluster> entry : wordClustersSet.getWordClustersSet()) {

			float[] clusterVector = entry.getValue().clusterVector;
			float dist = 0;
			float dotProduct = 0;
			float normW = 0;
			float normC = 0;
			for (int i = 0; i < clusterVector.length; i++){
				dotProduct += wordVector[i] * clusterVector[i];
				normW += Math.pow(wordVector[i], 2);
				normC += Math.pow(clusterVector[i], 2);
			}
			dist = (float) (dotProduct / (Math.sqrt(normW) * Math.sqrt(normC)));

			if (dist > minDist){
				selectedCluster = entry.getValue();
				minDist = dist;
			}
		}

		return selectedCluster;
	}
	
	public String getClosestClusterName(String word) {
		WordCluster wordCluster = getClosestCluster(word);
		
		if (wordCluster != null) {
			return wordCluster.name;
		} else {
			return null;
		}
	}
	
	// words_in_vandalic_cluster_1
	public boolean getClosestClusterClass1(String word) {
		WordCluster wordCluster = getClosestCluster(word);
		
		if (wordCluster != null) {
			return (wordCluster.revisionClassT - wordCluster.revisionClassF >= 2);
		} else {
			return false;
		}
	}
	
	// words_in_vandalic_cluster_2
	public boolean getClosestClusterClass2(String word) {
		WordCluster wordCluster = getClosestCluster(word);
		
		if (wordCluster != null) {
			return wordCluster.clusterClass;
		} else {
			return false;
		}
	}

	public void clusterWord(String word, boolean revisionClass, boolean isolateClass) {
		float[] wordVector = vectorModel.getWordVector(word);

		String clusterName = word;

		if (wordVector == null) {
			wordVector = new float[vectorSize];
			//clusterName = "oov_words";	
		}

		float minDist = 0.5f;
		String selectedClusterName = null;

		if (wordClustersSet.isEmpty()) {    		

			WordCluster wordCluster = new WordCluster(clusterName, vectorSize);
			wordClustersSet.newCluster(clusterName, wordCluster);   
			wordClustersSet.addWordToCluster(clusterName, word, revisionClass, wordVector);

		} else {

			for (Map.Entry<String, WordCluster> entry : wordClustersSet.getWordClustersSet()) {

				float[] clusterVector = entry.getValue().clusterVector;
				float dist = 0;
				float dotProduct = 0;
				float normW = 0;
				float normC = 0;
				for (int i = 0; i < clusterVector.length; i++){
					dotProduct += wordVector[i] * clusterVector[i];
					normW += Math.pow(wordVector[i], 2);
					normC += Math.pow(clusterVector[i], 2);
				}
				dist = (float) (dotProduct / (Math.sqrt(normW) * Math.sqrt(normC)));

				if (isolateClass) {
					if ((dist > minDist) && (revisionClass == entry.getValue().clusterClass)) {
						selectedClusterName = entry.getKey();
						minDist = dist;
					}					
				} else {
					if (dist > minDist) {
						selectedClusterName = entry.getKey();
						minDist = dist;
					}
				}
			}

			if (selectedClusterName != null) {
				wordClustersSet.addWordToCluster(selectedClusterName, word, revisionClass, wordVector);
			} else {
				WordCluster wordCluster = new WordCluster(clusterName, vectorSize);
				wordClustersSet.newCluster(clusterName, wordCluster); 
				wordClustersSet.addWordToCluster(clusterName, word, revisionClass, wordVector);				
			}  	
		}    	
	}
	
	// words_in_vandalic_cluster_2
	public void clusterVandalicWordList(String wordListName) {
    	WordList wordList = new WordList(wordListName);    	
    	HashMap<String,Integer> wordListMap = wordList.getList();
    	
      	for (Map.Entry<String, Integer> entry : wordListMap.entrySet()) {
      		clusterWord(entry.getKey(), true, true);
    	}	
	}
	
	// words_in_vandalic_cluster_2
	public void groupRegularWordsFromField(String sqlStatement, String wordListName) {	
		
		WordList wordList = new WordList(wordListName);  
		
		textLength = 0;

		DatabaseUtils databaseUtils = new DatabaseUtils();		
		Connection con = databaseUtils.openConnection2();
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sqlStatement);
			rs = pst.executeQuery();

			while (rs.next()) {							

				int editId= rs.getInt(1);							
				String inputText = rs.getString(3);
				String wordFrequencyList = rs.getString(4);
				
				System.out.println("Edit Id: " + editId);
				 		
				String[] tokenizedText = inputText.split("[ \n\r]");
				String[] tokenizedWordFrequencyList = wordFrequencyList.split("[ \n\r]");
				textLength = textLength + tokenizedText.length;
				
				for (int i = 0; i < tokenizedText.length; i++) {					
					String token = tokenizedText[i].toLowerCase();
					
					if (!wordList.findTerm(token)) {						
						if (!tokenizedWordFrequencyList[i].equals("")) {
							clusterWord(token, false, true);						
						}
					}
				}
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}			
	}
	
	// words_in_vandalic_cluster_1
	public void groupWordsFromField(String sqlStatement, boolean calculateWithFrequency) {	
				
		textLength = 0;

		DatabaseUtils databaseUtils = new DatabaseUtils();		
		Connection con = databaseUtils.openConnection2();
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sqlStatement);
			rs = pst.executeQuery();

			while (rs.next()) {							

				int editId= rs.getInt(1);				
				String revisionClassText = rs.getString(2);				
				String inputText = rs.getString(3);
				String wordFrequencyList = rs.getString(4);
				
				System.out.println("Edit Id: " + editId);
				
				boolean revisionClass = (revisionClassText.equals("vandalism"));  		
				String[] tokenizedText = inputText.split("[ \n\r]");
				String[] tokenizedWordFrequencyList = wordFrequencyList.split("[ \n\r]");
				textLength = textLength + tokenizedText.length;
				
				for (int i = 0; i < tokenizedText.length; i++) {					
					String token = tokenizedText[i].toLowerCase();
					if (!tokenizedWordFrequencyList[i].equals("")) {
						int frequency = Integer.parseInt(tokenizedWordFrequencyList[i]);
						if (calculateWithFrequency) {
							for (int j = 0; j < frequency; j++) {
								clusterWord(token, revisionClass, false);
							}
						} else {
							clusterWord(token, revisionClass, false);						
						}
					}
				}
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
	}

	// words_in_vandalic_cluster_1
	public void createWordClusters1(boolean calculateWithFrequency) {	
		System.out.printf("Building word cluster 1...");
		
		String sqlStatement = "SELECT edit_id, revision_class, inserted_words_list, inserted_words_frequency_list FROM words_diff_training";    	
//		String sqlStatement = 
//			"SELECT edit_id, revision_class, inserted_words_list, inserted_words_frequency_list " + 
//			"FROM words_diff_training WHERE edit_id = '340' OR edit_id = '1361'";
		groupWordsFromField(sqlStatement, calculateWithFrequency);    			
	}
	
	// words_in_vandalic_cluster_2
	public void createWordClusters2() {		
		System.out.printf("Building word cluster 2...");
		
		String sqlStatement = "SELECT edit_id, revision_class, inserted_words_list, inserted_words_frequency_list FROM words_diff_training";    	
//		String sqlStatement = 
//			"SELECT edit_id, revision_class, inserted_words_list, inserted_words_frequency_list " + 
//			"FROM words_diff_training WHERE edit_id = '340' OR edit_id = '1361'";
		clusterVandalicWordList("model\\inserted_words_diff.txt");
		groupRegularWordsFromField(sqlStatement, "model\\inserted_words_diff.txt");    			
	}

	public void saveWordClusters(String fileName) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(fileName);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(wordClustersSet);
			objectOutputStream.close();
			fileOutputStream.close();
			System.out.println("Word clusters saved in " + fileName);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void loadWordClusters(String fileName) {
		wordClustersSet = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(fileName);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			wordClustersSet = (WordClustersSet) objectInputStream.readObject();
			objectInputStream.close();
			fileInputStream.close();
		} catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void printCluster(String clusterName) {	
		wordClustersSet.printCluster(clusterName);
	}

	public void printClusters() {
		wordClustersSet.printClusters();
	}
}
