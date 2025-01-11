package jrmr.vds.model.text.cluster;

import java.io.Serializable;
import java.util.ArrayList;

public class WordCluster implements Serializable {		
	private static final long serialVersionUID = -7385700585790496970L;
	public String name;
	public int revisionClassF;
	public int revisionClassT;
	public boolean clusterClass;
	public float[] clusterVector;
	public ArrayList<String> clusteredWords;

	public WordCluster(String name, int vectorSize) {
		this.name = name;
		clusterVector = new float[vectorSize];
		clusteredWords = new ArrayList<String>();

		System.out.println("New cluster: " + name);
	}

	public void addWord(String word, boolean revisionClass, float[] wordVector) {
		if (!clusteredWords.contains(word)) {
			clusteredWords.add(word);        		
		}

		for (int i = 0; i < clusterVector.length; i++) {
			clusterVector[i] = clusterVector[i] + wordVector[i];	
		}

		if (revisionClass) {
			this.revisionClassT++;	
		} else {
			this.revisionClassF++;
		}
		
		clusterClass = (revisionClassT > revisionClassF);
	}        
}