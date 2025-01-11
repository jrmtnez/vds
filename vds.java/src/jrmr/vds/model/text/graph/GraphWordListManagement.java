package jrmr.vds.model.text.graph;

import java.util.HashMap;
import java.util.Map;

import jrmr.vds.model.io.FileUtils;

public class GraphWordListManagement {
	
	public void generateGraphWordListGephi(int vandalicListSize, boolean withoutNumbers, boolean buildRegularGraph) {
    	WordCoocurrence wordCoocurrence = new WordCoocurrence();;
    	GraphManagementGephi graphManagement = new GraphManagementGephi();;
    	String regularFileName = "model//coocurrence_regular";    	
    	String[] regularFileNameArray = new String[4];
    	String vandalicFileNamePageRank;
    	String vandalicFileNameDegree;
    	String vandalicFileNameEigenvector;    	
    	String vandalicFileNameAuthority;    	
    	
    	regularFileNameArray[0] = generateGraphRegularWordListGephi(4000, withoutNumbers, 0, buildRegularGraph, true);    	
    	regularFileNameArray[1] = generateGraphRegularWordListGephi(4000, withoutNumbers, 4000, buildRegularGraph, true);
    	regularFileNameArray[2] = generateGraphRegularWordListGephi(4000, withoutNumbers, 8000, buildRegularGraph, true);
    	regularFileNameArray[3] = generateGraphRegularWordListGephi(2079, withoutNumbers, 12000, buildRegularGraph, true);    	
    	
//    	regularFileNameArray[0] = "model//coocurrence_regular_0";    	
//    	regularFileNameArray[1] = "model//coocurrence_regular_4000";
//    	regularFileNameArray[2] = "model//coocurrence_regular_8000";
//    	regularFileNameArray[3] = "model//coocurrence_regular_12000";    	
    	    	    	    	    	
    	mergeRegularWordList(regularFileNameArray, regularFileName, "_pagerank.txt");
    	vandalicFileNamePageRank = wordCoocurrence.calcVandalicCoocurrenceMatrix(10, 3, 0, withoutNumbers, regularFileName + "_pagerank.txt");
    	graphManagement.pageRankWordList(vandalicFileNamePageRank, 0.0, vandalicListSize);
    	
    	mergeRegularWordList(regularFileNameArray, regularFileName, "_degree.txt");
    	vandalicFileNameDegree = wordCoocurrence.calcVandalicCoocurrenceMatrix(10, 3, 0, withoutNumbers, regularFileName + "_degree.txt");
    	graphManagement.degreeWordList(vandalicFileNameDegree, 0.0, vandalicListSize);
    	
    	mergeRegularWordList(regularFileNameArray, regularFileName, "_eigenvector.txt");    	
    	vandalicFileNameEigenvector = wordCoocurrence.calcVandalicCoocurrenceMatrix(10, 3, 0, withoutNumbers, regularFileName + "_eigenvector.txt");
    	graphManagement.eigenvectorCentralityWordList(vandalicFileNameEigenvector, 0.0, vandalicListSize);
    	
    	mergeRegularWordList(regularFileNameArray, regularFileName, "_authority.txt");    	
    	vandalicFileNameAuthority = wordCoocurrence.calcVandalicCoocurrenceMatrix(10, 3, 0, withoutNumbers, regularFileName + "_authority.txt");
    	graphManagement.authorityWordList(vandalicFileNameAuthority, 0f, vandalicListSize);
	}
	
	public String generateGraphRegularWordListGephi(int maxRegularInstancies, boolean withoutNumbers, 
		int initialOffset, boolean buildGraph, boolean buildLists) {
		
    	GraphManagementGephi graphManagement = new GraphManagementGephi();
    	String regularFileName;
    	int listSize = 5000;
    	
    	if (buildGraph)
    		regularFileName = new WordCoocurrence().calcRegularCoocurrenceMatrix(10, 3, maxRegularInstancies, withoutNumbers, initialOffset);
    	else
    		regularFileName = "model//coocurrence_regular_" + initialOffset; 
    	   	
    	if (buildLists) {
	    	graphManagement = new GraphManagementGephi();
	    	graphManagement.degreeWordList(regularFileName, 0.0, listSize);   	
	    	graphManagement.eigenvectorCentralityWordList(regularFileName, 0.0, listSize);
	    	graphManagement.pageRankWordList(regularFileName, 0.0, listSize);
	    	graphManagement.authorityWordList(regularFileName, 0f, listSize);   	
    	}
    	
    	return regularFileName;
	}
	
	public void generateGraphWordListJung(int vandalicListSize, boolean withoutNumbers, boolean buildRegularGraph) {
    	WordCoocurrence wordCoocurrence = new WordCoocurrence();;
    	GraphManagementJung graphManagement = new GraphManagementJung();;
    	String regularFileName = "model//coocurrence_regular";    	
    	String[] regularFileNameArray = new String[4];
    	String vandalicFileNamePageRank;
    	String vandalicFileNameDegree;
    	String vandalicFileNameEigenvector;    	
    	String vandalicFileNameAuthority;   
    	String vandalicFileNameBetweenness;
  	
    	regularFileNameArray[0] = generateGraphRegularWordListJung(4000, withoutNumbers, 0, buildRegularGraph);    	
    	regularFileNameArray[1] = generateGraphRegularWordListJung(4000, withoutNumbers, 4000, buildRegularGraph);
    	regularFileNameArray[2] = generateGraphRegularWordListJung(4000, withoutNumbers, 8000, buildRegularGraph);
    	regularFileNameArray[3] = generateGraphRegularWordListJung(2079, withoutNumbers, 12000, buildRegularGraph);    	
    	
//    	regularFileNameArray[0] = "model//coocurrence_regular_0";    	
//    	regularFileNameArray[1] = "model//coocurrence_regular_4000";
//    	regularFileNameArray[2] = "model//coocurrence_regular_8000";
//    	regularFileNameArray[3] = "model//coocurrence_regular_12000";    	
    	    	    	    	    	
    	mergeRegularWordList(regularFileNameArray, regularFileName, "_pagerank.txt");
    	vandalicFileNamePageRank = wordCoocurrence.calcVandalicCoocurrenceMatrix(10, 3, 0, withoutNumbers, regularFileName + "_pagerank.txt");
    	graphManagement.pageRankWordList(vandalicFileNamePageRank, 0.0, vandalicListSize);
    	
    	mergeRegularWordList(regularFileNameArray, regularFileName, "_degree.txt");
    	vandalicFileNameDegree = wordCoocurrence.calcVandalicCoocurrenceMatrix(10, 3, 0, withoutNumbers, regularFileName + "_degree.txt");
    	graphManagement.degreeWordList(vandalicFileNameDegree, 0, vandalicListSize);
    	
    	mergeRegularWordList(regularFileNameArray, regularFileName, "_eigenvector.txt");    	
    	vandalicFileNameEigenvector = wordCoocurrence.calcVandalicCoocurrenceMatrix(10, 3, 0, withoutNumbers, regularFileName + "_eigenvector.txt");
    	graphManagement.eigenvectorCentralityWordList(vandalicFileNameEigenvector, 0.0, vandalicListSize);
    	
    	mergeRegularWordList(regularFileNameArray, regularFileName, "_authority.txt");    	
    	vandalicFileNameAuthority = wordCoocurrence.calcVandalicCoocurrenceMatrix(10, 3, 0, withoutNumbers, regularFileName + "_authority.txt");
    	graphManagement.authorityWordList(vandalicFileNameAuthority, 0.0, vandalicListSize);

    	mergeRegularWordList(regularFileNameArray, regularFileName, "_betweenness.txt");    	
    	vandalicFileNameBetweenness = wordCoocurrence.calcVandalicCoocurrenceMatrix(10, 3, 0, withoutNumbers, regularFileName + "_betweenness.txt");
    	graphManagement.betweennessCentralityWordList(vandalicFileNameBetweenness, 0.0, vandalicListSize);
	}
	
	public String generateGraphRegularWordListJung(int maxRegularInstancies, boolean withoutNumbers, int initialOffset, boolean buildGraph) {
    	GraphManagementJung graphManagement = new GraphManagementJung();
    	String regularFileName;
    	int listSize = 5000;
    	
    	if (buildGraph)
    		regularFileName = new WordCoocurrence().calcRegularCoocurrenceMatrix(10, 3, maxRegularInstancies, withoutNumbers, initialOffset);
    	else
    		regularFileName = "model//coocurrence_regular_" + initialOffset; 
    	
    	graphManagement.pageRankWordList(regularFileName, 0.0, listSize);
    	graphManagement.degreeWordList(regularFileName, 0, listSize);
    	graphManagement.eigenvectorCentralityWordList(regularFileName, 0.0, listSize);
    	graphManagement.authorityWordList(regularFileName, 0.0, listSize);
    	graphManagement.betweennessCentralityWordList(regularFileName, 0.0, 1000);
    	
    	return regularFileName;
	}
	
	public void mergeRegularWordList(String[] fileNameArray, String outputFileName, String suffix) {
		
		FileUtils fileutils = new  FileUtils();
		HashMap<String,Integer> unifiedList = new HashMap<String,Integer>();
		
		for (int i = 0; i < fileNameArray.length; i++) {
			HashMap<String,Integer> regularList = fileutils.file2Map(fileNameArray[i] + suffix);
			for (Map.Entry<String, Integer> entry : regularList.entrySet()) {
				if (!unifiedList.containsKey(entry.getKey())) {
					unifiedList.put(entry.getKey(), 0);
				}				
			}					
		}
		
		fileutils.map2File(unifiedList, outputFileName + suffix, 0, 1, false);		
	}
	
}
