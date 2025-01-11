package jrmr.vds;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jrmr.vds.model.ext.word2vec.Word2Vec;
import jrmr.vds.model.ext.word2vec.WordSimilarity;
import jrmr.vds.view.ConfigController;
import jrmr.vds.view.SimilarityListController;
import jrmr.vds.view.SqlGridController;
import jrmr.vds.view.RootController;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private RootController rootController;
    private ProcessPool processPool;
    private Word2Vec word2Vec;

    private String TITLE = "A Vandalism Detection System";
    private String previousVectorModelFile = "";
	
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {            	
    	
    	this.primaryStage = primaryStage;
        this.primaryStage.setTitle(TITLE);
        
        this.primaryStage.getIcons().add(new Image("file:resources/icons/ic_public_black_18dp.png"));        
        this.primaryStage.getIcons().add(new Image("file:resources/icons/ic_public_black_24dp.png"));
        this.primaryStage.getIcons().add(new Image("file:resources/icons/ic_public_black_36dp.png"));
        this.primaryStage.getIcons().add(new Image("file:resources/icons/ic_public_black_48dp.png"));
        
        initRootLayout();
    }
       
    public void initRootLayout() {
        try {
        	
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Root.fxml"));
            rootLayout = (BorderPane) loader.load();
                                  
            Scene scene = new Scene(rootLayout);
           
            primaryStage.setScene(scene);
            //primaryStage.setMaximized(true);

            primaryStage.show();
            
            rootController = loader.getController();
            rootController.setMainApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showConfig() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Config.fxml"));
            TitledPane config = (TitledPane) loader.load();
            
            ConfigController configController = loader.getController();
            configController.setRootController(rootController);
                        
            //config.autosize();
            rootLayout.setCenter(config); 
          
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showSqlGrid(String paneTitle, String sqlStatement1, String sqlStatement2) {
        try {       	
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/SqlGrid.fxml"));
            TitledPane sqlGrid = (TitledPane) loader.load();
            
            SqlGridController sqlGridController = loader.getController();            
            sqlGridController.buildData(paneTitle, sqlStatement1, sqlStatement2);
            
            rootLayout.setCenter(sqlGrid); 
          
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showSimilarityList(String paneTitle, String vectorModelFile, String word) {
    	
        try {       	
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/SimilarityList.fxml"));
            TitledPane similarityList = (TitledPane) loader.load();
            
            SimilarityListController similarityListController = loader.getController();   
            
            if (word2Vec == null || !previousVectorModelFile.equals(vectorModelFile)) {
            	word2Vec = new Word2Vec();
            	previousVectorModelFile = vectorModelFile;
            }            	   

            ObservableList<WordSimilarity> wordSimilarity = word2Vec.getSimilarityList(vectorModelFile,	word);	

                        
            similarityListController.buildData(paneTitle, wordSimilarity);
            
            rootLayout.setCenter(similarityList); 
          
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void closeApp() {
    	primaryStage.close();
    }

    public void clearCenterPane() {
    	rootLayout.setCenter(null);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }    
    
    // main processes
      
    public void loadPan() {
    	processPool = new ProcessPool();    	
    	processPool.loadPan();
    }
    
    public void loadEdits() {
    	processPool = new ProcessPool();    	
    	processPool.loadEdits();
    }

    public void loadRevisions() {
    	processPool = new ProcessPool();    	
    	processPool.loadRevisions();
    }
    
    public void tokenizeRevisions() {
    	processPool = new ProcessPool();    	
    	processPool.tokenizeRevisions();
    }
    
    public void extractPlainTextOfRevisions() {
    	processPool = new ProcessPool();    	
    	processPool.extractPlainTextOfRevisions();
    }
    
    public void extractWordsDiffsOfRevisions() {
    	processPool = new ProcessPool();    	
    	processPool.extractWordsDiffsOfRevisions();
    }
    
    public void extractDiffsOfRevisions() {
    	processPool = new ProcessPool();    	
    	processPool.extractDiffsOfRevisions();
    }

    public void loadPagesMetaHistory() {
    	processPool = new ProcessPool();    	
    	processPool.loadPagesMetaHistory();
    }    
    
    public void generatePanFeatures() {
    	processPool = new ProcessPool();
    	processPool.generatePanFeatures(0);
    	processPool.joinFeatures();
    }    
    
    public boolean exportPanFeaturesToWeka() {    	    	
    	processPool = new ProcessPool();
    	
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Weka ARFF files (*.arff)", "*.arff");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showSaveDialog(primaryStage);
		
		if(file != null) {
			processPool.exportPanFeaturesToWeka(file);
			return true;
		} else {
			return false;
		}
    }
    
    public String selectModelFile() {    	    	
    	processPool = new ProcessPool();
    	
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("vector model files (*.bin)", "*.bin");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(primaryStage);
		
		return file.getAbsolutePath();
    }

    
    public void buildWordVectorModel(String vectorModelFile) {
    	processPool = new ProcessPool();
    	processPool.createUnifiedPlainTextOfRevisions();    	    	
    	processPool.buildWordVectorModel(vectorModelFile);
    }
        
    public void testWordSimilarity(String vectorModelFile, String word) {
    	processPool = new ProcessPool();	
    	processPool.testWordSimilarity(vectorModelFile, word);
    }
    
    public void extractWordSimilarities(String vectorModelFile) {
    	processPool = new ProcessPool();
    	processPool.extractDiffInsertedWords();
    	processPool.findDiffRelatedWords(vectorModelFile);  
    	processPool.buildWordClusters1(vectorModelFile);
    	processPool.buildWordClusters2(vectorModelFile);    	
    }

    public void generateGraphWordLists(String rankingAlgorithm, String rankingRegularVandalic) {
    	processPool = new ProcessPool();
    	
    	switch (rankingRegularVandalic) {
	    	case "Regular Graph 1" :
	    		processPool.generateRegularGraph("build_rg1");
	    		break;
	    	case "Regular Graph 2" :
	    		processPool.generateRegularGraph("build_rg2");
	    		break;
	    	case "Regular Graph 3" :
	    		processPool.generateRegularGraph("build_rg3");
	    		break;
	    	case "Regular Graph 4" :
	    		processPool.generateRegularGraph("build_rg4");
	    		break;
	    	case "Regular Ranking 1" :
	    		processPool.generateRanking("rg1", rankingAlgorithm);
	    		break;
	    	case "Regular Ranking 2" :
	    		processPool.generateRanking("rg2", rankingAlgorithm);
	    		break;
	    	case "Regular Ranking 3" :
	    		processPool.generateRanking("rg3", rankingAlgorithm);
	    		break;
	    	case "Regular Ranking 4" :
	    		processPool.generateRanking("rg4", rankingAlgorithm);
	    		break;
	    	case "Vandalic Graph and Ranking" :
	    		processPool.generateVandalicLists();
	    		break;
    	};
    }
        
    public void createDNNVEModel() {
    	processPool = new ProcessPool();	
    	processPool.createDNNVEModel();
    }
    
    public void python2Table() {    	
    	processPool = new ProcessPool();	
    	processPool.python2Table();
    }
    
    
    public void evaluateFeatures() {  
		processPool = new ProcessPool();
		processPool.evaluateFeatures();				
    }
    
    public void testMethod() {
    	TestMethods testMethods = new TestMethods();    	
    	testMethods.TestVocabularyExtractor();
   }
}