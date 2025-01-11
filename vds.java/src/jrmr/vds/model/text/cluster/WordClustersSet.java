package jrmr.vds.model.text.cluster;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WordClustersSet implements Serializable {

	private static final long serialVersionUID = 5640280008056952248L;
	private Map<String, WordCluster> wordClustersMap = new HashMap<String, WordCluster>();
	
	public boolean isEmpty() {
		return wordClustersMap.isEmpty();
	}
	
	public void newCluster(String clusterName, WordCluster wordCluster) {
		wordClustersMap.put(clusterName, wordCluster);
	}
	
	public void addWordToCluster(String clusterName, String word, boolean revisionClass, float[] wordVector) {
		wordClustersMap.get(clusterName).addWord(word, revisionClass, wordVector);
	}

	public Set<Map.Entry<String, WordCluster>> getWordClustersSet() {
		return  wordClustersMap.entrySet();
	}	

	public void printCluster(String clusterName) {		
		WordCluster wordCluster = wordClustersMap.get(clusterName);
		System.out.println("Cluster name: " + wordCluster.name);
		System.out.println("=> number of F: " + wordCluster.revisionClassF);
		System.out.println("=> number of T: " + wordCluster.revisionClassT);
		System.out.println("=> Clustered words: ");
		for (String clusteredWord : wordCluster.clusteredWords) {
			System.out.println("   => " + clusteredWord);
		}    			
	}	
	
	public void printClusters() {

		for (Map.Entry<String, WordCluster> entry : wordClustersMap.entrySet()) {
			if (entry.getValue().revisionClassF > entry.getValue().revisionClassT) {
				System.out.println("Cluster name: " + entry.getKey());
				System.out.println("=> Number of F: " + entry.getValue().revisionClassF);
				System.out.println("=> Number of T: " + entry.getValue().revisionClassT);
				System.out.println("=> Class: " + entry.getValue().clusterClass);
				System.out.println("=> Clustered words: ");
				for (String clusteredWord : entry.getValue().clusteredWords) {
					System.out.println("   => " + clusteredWord);
				}    			
			}    		
		}

		for (Map.Entry<String, WordCluster> entry : wordClustersMap.entrySet()) {
			if (entry.getValue().revisionClassF <= entry.getValue().revisionClassT) {
			//if ((entry.getValue().revisionClassF < entry.getValue().revisionClassT) &&
			//		entry.getValue().revisionClassT > 1 &&
			//		entry.getValue().clusteredWords.size() > 0) {				
				System.out.println("Cluster name: " + entry.getKey());
				System.out.println("=> Number of F: " + entry.getValue().revisionClassF);
				System.out.println("=> Number of T: " + entry.getValue().revisionClassT);
				System.out.println("=> Class: " + entry.getValue().clusterClass);				
				System.out.println("=> Clustered words: ");
				for (String clusteredWord : entry.getValue().clusteredWords) {
					System.out.println("   => " + clusteredWord);
				}
			}
		}
		
		System.out.println();
		System.out.println("Number of clusters: " + wordClustersMap.entrySet().size());
	}	
}
