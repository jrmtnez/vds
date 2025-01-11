package jrmr.vds;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



import jrmr.vds.model.config.Config;
import jrmr.vds.model.db.DatabaseUtils;
import jrmr.vds.model.dnn.io.VSModelUtils;
import jrmr.vds.model.ext.weka.DatasetUtils;
import jrmr.vds.model.ext.weka.ModelEvaluation;
import jrmr.vds.model.ext.word2vec.VectorModel;
import jrmr.vds.model.ext.word2vec.Word2Vec;
import jrmr.vds.model.io.FileUtils;
import jrmr.vds.model.io.PagesMetaHistoryLoader;
import jrmr.vds.model.io.Pan10EditsLoader;
import jrmr.vds.model.io.Pan11EditsLoader;
import jrmr.vds.model.io.PanArticleRevisionsLoader;
import jrmr.vds.model.text.VocabularyExtractor;
import jrmr.vds.model.text.cluster.WordClustering;
import jrmr.vds.model.text.graph.GraphManagementGephi;
import jrmr.vds.model.text.graph.GraphWordListManagement;
import jrmr.vds.model.text.graph.WordCoocurrence;

public class ProcessPool {

	Config config = null;

	public ProcessPool() {
		config = new Config();
	}

	public void loadPan() {
    	loadEdits();
    	loadRevisions();
    	tokenizeRevisions();
    	extractPlainTextOfRevisions();
    	extractWordsDiffsOfRevisions();
    	extractDiffsOfRevisions();
    }

    public void initDatabase() {
        System.out.println("-> Initializing database...");
        DatabaseUtils databaseUtils = new DatabaseUtils();
		databaseUtils.initDatabase();
    }

    public void loadEdits() {
        System.out.println("-> Loading edits...");
        config = new Config();
        if (config.getPanGoldAnnotationsFile().isEmpty()) {
        	Pan11EditsLoader pan11Loader = new Pan11EditsLoader();
        	pan11Loader.load();
    	} else {
        	Pan10EditsLoader pan10Loader = new Pan10EditsLoader();
        	pan10Loader.load();
    	}
    }

    public void loadRevisions() {
        System.out.println("-> Loading revisions...");
    	PanArticleRevisionsLoader panArticleRevisionsLoader = new PanArticleRevisionsLoader();
    	panArticleRevisionsLoader.load();
    }

    public void tokenizeRevisions() {
        System.out.println("-> Processing revisions (tokenize) ...");
        config = new Config();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.openConnection();
		databaseUtils.createTable(databaseUtils.getCreatePanArticleRevStatement(config.getTokenizedPanArticleRevTable(),""));
        databaseUtils.createTokenizedRevisions();
        databaseUtils.closeConnection();
    }

    public void extractPlainTextOfRevisions() {
        System.out.println("-> Processing revisions (extract plain text)...");
        config = new Config();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.openConnection();
		databaseUtils.createTable(databaseUtils.getCreatePanArticleRevStatement(config.getPlainTextPanArticleRevTable(),""));
        databaseUtils.createPlainTextRevisions();
        databaseUtils.closeConnection();
    }

    public void extractWordsDiffsOfRevisions() {
        System.out.println("-> Processing revisions (extract words diff lists)...");
        config = new Config();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.openConnection();
		databaseUtils.createTable(databaseUtils.getCreateWordsDiffTableStatement());
        databaseUtils.updateWordDiffTable(config.getOnlyWithClass().equals("true"));
        databaseUtils.closeConnection();
    }

    public void extractDiffsOfRevisions() {
        System.out.println("-> Processing revisions (extract edits diffs)...");
        config = new Config();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.openConnection();
		databaseUtils.createTable(databaseUtils.getCreateDiffTableStatement());
        databaseUtils.updateDiffTable(config.getOnlyWithClass().equals("true"));
        databaseUtils.closeConnection();
    }

    // ---------------------------

    public void loadPagesMetaHistory() {
    	PagesMetaHistoryLoader pagesMetaHistoryLoader = new PagesMetaHistoryLoader();
    	pagesMetaHistoryLoader.load();
    }

    public void generatePanFeatures(int featureGroup) {
        System.out.println("-> Processing features...");
        config = new Config(); // reload configuration
    	boolean onlyWithClass = config.getOnlyWithClass().equals("true");
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.openConnection();
        databaseUtils.createTable(databaseUtils.getCreateFeatureStatement());
        databaseUtils.calcFeaturesFromEdits(true,0,onlyWithClass,featureGroup);
        databaseUtils.closeConnection();
    }

    public void calcPanFeatures(int editId) {
    	System.out.println("-> Processing features...");
    	config = new Config();
    	boolean onlyWithClass = config.getOnlyWithClass().equals("true");
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.openConnection();
        databaseUtils.calcFeaturesFromEdits(false, editId, onlyWithClass, 0);
        databaseUtils.closeConnection();
    }

    public void exportPanFeaturesToWeka(File file) {
        System.out.println("-> Exporting features to Weka format (" + file + ")...");
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.openConnection();
        databaseUtils.writeArff(file);
        databaseUtils.closeConnection();
    }

    public void createUnifiedPlainTextOfRevisions() {
        System.out.println("-> Processing revisions (merging plain text on all table)...");
        config = new Config();
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.openConnection();
		databaseUtils.createTable(databaseUtils.getCreatePanArticleRevStatement(config.getPlainTextPanArticleRevTable(),"all"));
        databaseUtils.createUnifiedPlainTextOfRevisions();
        databaseUtils.closeConnection();
    }

    public void buildWordVectorModel(String vectorModelFile) {
    	Word2Vec word2Vec = new Word2Vec();
    	word2Vec.train();
    	File file = new File(vectorModelFile);
    	word2Vec.saveModel(file);
    }

    public void testWordSimilarity(String vectorModelFile, String word) {
    	Word2Vec word2Vec = new Word2Vec();
    	word2Vec.testVector(vectorModelFile, word);
    }

    public void extractDiffInsertedWords() {

    	System.out.println("Extracting diff words...");

    	config = new Config();

    	VocabularyExtractor vocabularyExtractor = new VocabularyExtractor();
    	FileUtils fileutils = new  FileUtils();

    	HashMap<String,Integer> vandalismVoc = vocabularyExtractor.getVocabularyFromField(
    			"SELECT inserted_words_list FROM " + config.getPanWordsDiffTable() + "_training " +
    			"WHERE revision_class = 'vandalism'");
    	fileutils.map2File(vandalismVoc, "model//inserted_words_vandalic.txt",1,1,false);

    	HashMap<String,Integer> regularVoc = vocabularyExtractor.getVocabularyFromField(
    			"SELECT inserted_words_list FROM " + config.getPanWordsDiffTable() + "_training " +
    			"WHERE revision_class = 'regular'");
    	fileutils.map2File(regularVoc, "model//inserted_words_regular.txt",1,1,false);

    	HashMap<String,Integer> diffVoc = new HashMap<String,Integer>();
    	for (Map.Entry<String, Integer> entry : vandalismVoc.entrySet()) {
    		if (!regularVoc.containsKey(entry.getKey())) {
    			diffVoc.put(entry.getKey(), entry.getValue());
    		}
    	}
    	fileutils.map2File(diffVoc, "model//inserted_words_diff.txt",2,1,false);
    }

    public void findDiffRelatedWords(String vectorModelFile) {

    	System.out.println("Finding related diff words...");

    	VectorModel vectorModel = VectorModel.loadFromFile(vectorModelFile);

    	FileUtils fileutils = new  FileUtils();
    	HashMap<String,Integer> diffVoc = fileutils.file2Map("model//inserted_words_diff.txt");
    	HashMap<String,Integer> diffRelatedVoc = new HashMap<String,Integer>();

    	int i = 0;
    	int size = diffVoc.entrySet().size();
    	for (Map.Entry<String, Integer> entry : diffVoc.entrySet()) {
    		String word = entry.getKey();
    		int wordCount = entry.getValue();

    		i++;
    		System.out.println("=> " + word + " " + i + "/" +  size);

        	if (!isANumber(word)) {
        		if (!diffRelatedVoc.containsKey(word)) {
        			diffRelatedVoc.put(word, wordCount);
        		}

                Set<VectorModel.WordScore> result = Collections.emptySet();
                result = vectorModel.similar(word);

                for (VectorModel.WordScore we : result) {
                	if (we.score >= 0.5) {
                		System.out.println("===> " + we.name + ", score = " + we.score);
                		if (!diffRelatedVoc.containsKey(we.name)) {
                			diffRelatedVoc.put(we.name, 1);
                		}
                	}
                }
    		}
    	}

    	fileutils.map2File(diffRelatedVoc, "model//inserted_words_diff_related.txt",1,1,false);
    }

	public Boolean isANumber(String word) {
		try {
			@SuppressWarnings("unused")
			float number = Float.parseFloat(word);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public void textModel2BinModel() {
		textModel2BinModel("model//GoogleNews-vectors-negative300.txt", "model//GoogleNews-vectors-negative300.bin");
	}

	public void textModel2BinModel(String textModelFile, String binModelFile) {

		System.out.println("Loading text model...");

    	VectorModel vectorModel = VectorModel.loadFromTextFile(textModelFile);

		System.out.println("Saving bin model...");

    	File file = new File(binModelFile);
    	vectorModel.saveModel(file);

		System.out.println("Done");
	}

	public void buildWordClusters1(String model) {
    	WordClustering wordClustering = new WordClustering();
    	wordClustering.loadVectorModel(model);
    	wordClustering.createWordClusters1(true);
    	wordClustering.saveWordClusters("model//wordclusters1.bin");
	}

	public void buildWordClusters2(String model) {
    	WordClustering wordClustering = new WordClustering();
    	wordClustering.loadVectorModel(model);
    	wordClustering.createWordClusters2();
    	wordClustering.saveWordClusters("model//wordclusters2.bin");
	}

    public void generateGraphWordLists(int implementation, boolean buildRegularGraph) {

    	GraphWordListManagement graphWordListManagement = new GraphWordListManagement();

    	switch (implementation) {
    		case 1:
    			graphWordListManagement.generateGraphWordListGephi(100, true, buildRegularGraph);
    			break;
    		case 2:
    			graphWordListManagement.generateGraphWordListJung(100, true, buildRegularGraph);
                break;
    	}
    }

    public void evaluateFeatures() {
    	ModelEvaluation modelEvaluation = new ModelEvaluation();
   		modelEvaluation.evaluateFeatures();
    }


    // --- script based execution ---

    @SuppressWarnings("unused")
	public void generateRegularGraph(String graphNo) {

    	String regularFileName;
		GraphWordListManagement graphWordListManagement = new GraphWordListManagement();

    	switch (graphNo) {
    		case "build_rg1" :
        		regularFileName = graphWordListManagement.generateGraphRegularWordListGephi(4000, true, 0, true, false);
    			break;
    		case "build_rg2" :
        		regularFileName  = graphWordListManagement.generateGraphRegularWordListGephi(4000, true, 4000, true, false);
    			break;
    		case "build_rg3" :
        		regularFileName  = graphWordListManagement.generateGraphRegularWordListGephi(4000, true, 8000, true, false);
    			break;
    		case "build_rg4" :
        		regularFileName  = graphWordListManagement.generateGraphRegularWordListGephi(2079, true, 12000, true, false);
    			break;
    	}
    }

    public void generateRanking(String initialOffset, String algorithm) {

		int listSize = 5000;
		String regularFileName = "";

    	switch (initialOffset) {
		case "rg1" :
			regularFileName = "model//coocurrence_regular_0";
			break;
		case "rg2" :
			regularFileName = "model//coocurrence_regular_4000";
			break;
		case "rg3" :
			regularFileName = "model//coocurrence_regular_8000";
			break;
		case "rg4" :
			regularFileName = "model//coocurrence_regular_12000";
			break;
    	}

		GraphManagementGephi graphManagement = new GraphManagementGephi();
    	switch (algorithm) {
    		case "Degree" :
    			System.out.println(algorithm);
    			graphManagement.degreeWordList(regularFileName, 0.0, listSize);
    			break;
    		case "Eigenvector Centrality" :
    			System.out.println(algorithm);
    			graphManagement.eigenvectorCentralityWordList(regularFileName, 0.0, listSize);
    			break;
    		case "PageRank" :
    			System.out.println(algorithm);
    	    	graphManagement.pageRankWordList(regularFileName, 0.0, listSize);
    			break;
    		case "HITS" :
    			System.out.println(algorithm);
    	    	graphManagement.authorityWordList(regularFileName, 0f, listSize);
    			break;
    	}
    }

    public void generateVandalicLists() {

    	String regularFileName = "model//coocurrence_regular";
    	String[] regularFileNameArray = new String[4];

    	regularFileNameArray[0] = "model/coocurrence_regular_0";
    	regularFileNameArray[1] = "model//coocurrence_regular_4000";
    	regularFileNameArray[2] = "model//coocurrence_regular_8000";
    	regularFileNameArray[3] = "model//coocurrence_regular_12000";

    	GraphWordListManagement graphWordListManagement = new GraphWordListManagement();
    	graphWordListManagement.mergeRegularWordList(regularFileNameArray, regularFileName, "_degree.txt");
    	graphWordListManagement.mergeRegularWordList(regularFileNameArray, regularFileName, "_eigenvector.txt");
    	graphWordListManagement.mergeRegularWordList(regularFileNameArray, regularFileName, "_pagerank.txt");
    	graphWordListManagement.mergeRegularWordList(regularFileNameArray, regularFileName, "_authority.txt");


    	GraphManagementGephi graphManagement = new GraphManagementGephi();
    	WordCoocurrence wordCoocurrence = new WordCoocurrence();;
    	String vandalicFileNamePageRank;
    	String vandalicFileNameDegree;
    	String vandalicFileNameEigenvector;
    	String vandalicFileNameAuthority;
    	int vandalicListSize = 100;
    	boolean withoutNumbers = true;

    	vandalicFileNamePageRank = wordCoocurrence.calcVandalicCoocurrenceMatrix(10, 3, 0, withoutNumbers, regularFileName + "_pagerank.txt");
    	graphManagement.pageRankWordList(vandalicFileNamePageRank, 0.0, vandalicListSize);

    	vandalicFileNameDegree = wordCoocurrence.calcVandalicCoocurrenceMatrix(10, 3, 0, withoutNumbers, regularFileName + "_degree.txt");
    	graphManagement.degreeWordList(vandalicFileNameDegree, 0.0, vandalicListSize);

    	vandalicFileNameEigenvector = wordCoocurrence.calcVandalicCoocurrenceMatrix(10, 3, 0, withoutNumbers, regularFileName + "_eigenvector.txt");
    	graphManagement.eigenvectorCentralityWordList(vandalicFileNameEigenvector, 0.0, vandalicListSize);

    	vandalicFileNameAuthority = wordCoocurrence.calcVandalicCoocurrenceMatrix(10, 3, 0, withoutNumbers, regularFileName + "_authority.txt");
    	graphManagement.authorityWordList(vandalicFileNameAuthority, 0f, vandalicListSize);
    }

    public void createDNNVEModel() {
		VSModelUtils vSModelUtils = new VSModelUtils();
		vSModelUtils.extractInsertedDeletedWordsVocabulary();
		vSModelUtils.writeDiffInsertedDeletedVEModel();
    }

    public void python2Table() {
		DatasetUtils datasetUtils = new DatasetUtils();
		datasetUtils.python2Table("..//..//vds.python//sda_train25.arff", "..//..//vds.python//corpus//inserted_deleted_vemodel_training_editid.txt");
		datasetUtils.python2Table("..//..//vds.python//sda_test25.arff", "..//..//vds.python//corpus//inserted_deleted_vemodel_test_editid.txt");
    }

    public void joinFeatures() {
        System.out.println("-> Joining feature tables...");
        DatabaseUtils databaseUtils = new DatabaseUtils();
        databaseUtils.openConnection();
		databaseUtils.createTable(databaseUtils.getCreateTrainingFeatureViewStatement());
		databaseUtils.createTable(databaseUtils.getCreateTestFeatureViewStatement());
        databaseUtils.closeConnection();
    }


}
