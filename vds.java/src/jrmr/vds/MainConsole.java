package jrmr.vds;

import java.io.File;

import jrmr.vds.model.config.Config;
import jrmr.vds.model.io.FileUtils;

public class MainConsole {
	
    private static ProcessPool processPool;
    
	public static void main(String[] args) {
    	Config config = new Config();
    	FileUtils fileUtils = new FileUtils();
    	processPool = new ProcessPool();		
		   	
    	String arg1 = args[0];
    	String arg2 = "";
    	if (args.length > 1) {
    		arg2 = args[1];
    	}    	    	    	
    	
    	System.out.println("Processing option " + arg1 + " " + arg2);
    	
    	
    	if (arg1.equals("init_db")) {    		
    		initDatabase();
    	}
    	
		fileUtils.copyFile("vds.properties.training", "vds.properties");						

    	if (arg1.equals("load_pan")) {    				
			loadEdits();	
			loadRevisions();
    	}
		
    	if (arg1.equals("process_pan")) {    				
			tokenizeRevisions();		
			extractPlainTextOfRevisions();		
			extractDiffsOfRevisions();
			extractWordsDiffsOfRevisions();
    	}
		
		fileUtils.copyFile("vds.properties.test", "vds.properties");
		
    	if (arg1.equals("load_pan")) {    				
			loadEdits();
			loadRevisions();
    	}
		
    	if (arg1.equals("process_pan")) {    				
			tokenizeRevisions();
			extractPlainTextOfRevisions();		
			extractDiffsOfRevisions();
			extractWordsDiffsOfRevisions();
    	}
    			
		// train + test sets
		String vectorModelFile = "model//model_" + config.getWvTrainMethod() + "_" + config.getWvWindowSize() + "_" + config.getWvVectorSize() + ".bin"; 
		
    	if (arg1.equals("build_wvm")) {    				
			createUnifiedPlainTextOfRevisions();
			buildWordVectorModel(vectorModelFile);    	    	
    	}
		    	
//    	vectorModelFile = "..//..//vds.data//word-vector models//model_Skip Gram_8_400_sentence_200000.bin";
    	
    	if (arg1.equals("extract_sim")) {    				
    		extractWordSimilarities(vectorModelFile);
    	}
    	
    	if (arg1.equals("build_rg1") || arg1.equals("build_rg2") || arg1.equals("build_rg3") || arg1.equals("build_rg4")) {
    		processPool.generateRegularGraph(arg1);	
    	}
    	
    	if (arg1.equals("rg1") || arg1.equals("rg2") || arg1.equals("rg3") || arg1.equals("rg4")) {    
    		processPool.generateRanking(arg1, arg2);
    	}
    	
    	if (arg1.equals("build_vg")) {    
    		processPool.generateVandalicLists();
    	}
				
    	if (arg1.equals("training_features0")) {    		
			fileUtils.copyFile("vds.properties.training", "vds.properties");		
			generatePanFeatures(0);	
    	}
    	
    	if (arg1.equals("test_features0")) {    			
			fileUtils.copyFile("vds.properties.test", "vds.properties");		
			generatePanFeatures(0);
    	}
    	
    	if (arg1.equals("training_features1")) {    		
			fileUtils.copyFile("vds.properties.training", "vds.properties");		
			generatePanFeatures(1);	
    	}
    	
    	if (arg1.equals("test_features1")) {    			
			fileUtils.copyFile("vds.properties.test", "vds.properties");		
			generatePanFeatures(1);
    	}

    	if (arg1.equals("training_features2")) {    		
			fileUtils.copyFile("vds.properties.training", "vds.properties");		
			generatePanFeatures(2);	
    	}
    	
    	if (arg1.equals("test_features2")) {    			
			fileUtils.copyFile("vds.properties.test", "vds.properties");		
			generatePanFeatures(2);
    	}    	
    	
    	if (arg1.equals("training_features3")) {    		
			fileUtils.copyFile("vds.properties.training", "vds.properties");		
			generatePanFeatures(3);	
    	}
    	
    	if (arg1.equals("test_features3")) {    			
			fileUtils.copyFile("vds.properties.test", "vds.properties");		
			generatePanFeatures(3);
    	}
    	
    	if (arg1.equals("dnn_vemodel")) {
    		createDNNVEModel();
    	}
    	
    	if (arg1.equals("python2table")) {    		
    		python2Table();
    	}

    	if (arg1.equals("join_features")) {
    		joinFeatures();
    	}
    	
    	if (arg1.equals("export2weka")) { 
			fileUtils.copyFile("vds.properties.training", "vds.properties");		
			exportPanFeaturesToWeka();		
			fileUtils.copyFile("vds.properties.test", "vds.properties");
			exportPanFeaturesToWeka();
    	}
    	
    	if (arg1.equals("evaluate_features")) { 
    		evaluateFeatures();
    	}
    	
    	if (arg1.equals("test")) { 
    		testMethod();
    	}			
    			
		System.out.println("Suceed...");
	}
	

	public static void initDatabase() {
    	processPool.initDatabase();
    }
	
	public static void loadEdits() {
    	processPool.loadEdits();
    }
    
    public static void loadRevisions() {
    	processPool.loadRevisions();
    }

    public static void tokenizeRevisions() {
    	processPool.tokenizeRevisions();
    }
    
    public static void extractPlainTextOfRevisions() {
    	processPool = new ProcessPool();
    	processPool.extractPlainTextOfRevisions();
    }
    
    public static void extractWordsDiffsOfRevisions() {
    	processPool = new ProcessPool();
    	processPool.extractWordsDiffsOfRevisions();    	    	
    }

    public static void extractDiffsOfRevisions() {
    	processPool = new ProcessPool();
    	processPool.extractDiffsOfRevisions();    	    	
    }    
    
    public static void createUnifiedPlainTextOfRevisions() {
    	processPool = new ProcessPool();
    	processPool.createUnifiedPlainTextOfRevisions();    	    	
    }  
    
    
    //----------------
    
    public static void loadPagesMetaHistory() {
    	processPool.loadPagesMetaHistory();
    }    
    
    public static void generatePanFeatures(int featureGroup) {
    	processPool.generatePanFeatures(featureGroup);
    }    
    
    public static void exportPanFeaturesToWeka() {  
    	Config config = new Config();
    	
		File file = new File("model//features_all_" + config.getPanCorpus() + ".arff");
		processPool.exportPanFeaturesToWeka(file);
    }
    
    public static void buildWordVectorModel(String vectorModelFile) {
    	processPool = new ProcessPool();
    	processPool.buildWordVectorModel(vectorModelFile);
    }
    
    
    public static void extractRelatedWordsDiffsOfRevisions(String vectorModelFile) {
    	processPool = new ProcessPool();
    	processPool.findDiffRelatedWords(vectorModelFile);    	
    }
    
    public static void extractWordSimilarities(String vectorModelFile) {
    	System.out.println("Using word-vector model: " + vectorModelFile);
    	processPool = new ProcessPool();
    	processPool.extractDiffInsertedWords();
    	processPool.findDiffRelatedWords(vectorModelFile);  
    	processPool.buildWordClusters1(vectorModelFile);
    	processPool.buildWordClusters2(vectorModelFile);    	
    }
    
    public static void testWordSimilarity(String vectorModelFile, String word) {
    	processPool = new ProcessPool();	
    	processPool.testWordSimilarity(vectorModelFile, word);
    }
    
    public static void createDNNVEModel() {
    	processPool = new ProcessPool();	
    	processPool.createDNNVEModel();
    }
    
    public static void python2Table() {
    	processPool = new ProcessPool();	
    	processPool.python2Table();
    }
    
    public static void joinFeatures() {
    	processPool = new ProcessPool();	
    	processPool.joinFeatures();
    }
    
    
    public static void evaluateFeatures() {
    	processPool = new ProcessPool();	
    	processPool.evaluateFeatures();    	
    }
    
    public static void testMethod() {
    	
    	
   }  
}
