package jrmr.vds.view;


import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jrmr.vds.MainApp;
import jrmr.vds.model.config.Config;

public class RootController {
	
	@FXML	
	private MenuItem closeMenuItem;
	
	@FXML	
	private MenuItem clearMenuItem;
	
	@FXML	
	private MenuItem configMenuItem;
	
	@FXML	
	private MenuItem aboutMenuItem;
	
	@FXML
    private Label statusBar;
	
	@FXML
    private Button loadPanCorpus;
	
	@FXML
    private Label loadCorpusStatus;

	@FXML
    private Button loadPanEdits;

	@FXML
    private Label loadEditsStatus;

	@FXML
    private Button loadPanRevisions;
	
	@FXML
    private Label loadRevisionsStatus;
	
	@FXML
    private Button tokenizePanRevisions;

	@FXML
    private Label tokenizeRevisionsStatus;
	
	@FXML
    private Button extractPlainTextOfPanRevisions;
	
	@FXML
    private Label extractPlainTextOfRevisionsStatus;
	
	@FXML
    private Button extractWordsDiffOfPanRevisions;

	@FXML
    private Label extractWordsDiffOfPanRevisionsStatus;
	
	@FXML
    private Button extractDiffOfPanRevisions;

	@FXML
    private Label extractDiffOfPanRevisionsStatus;
	
	@FXML
    private Button loadWpPages;
	
	@FXML
    private Label loadPagesStatus;

	@FXML
    private Button generatePanFeatures;

	@FXML
    private Label generateFeaturesStatus;
	
	@FXML
    private Button buildWordVectorModel;

	@FXML
    private Label buildWordVectorModelStatus;
	
	@FXML
    private Button extractWordSimilarities;

	@FXML
    private Label extractWordSimilaritiesStatus;

	@FXML
    private Button showPanEdits;
	
	@FXML
    private Button showPanRevisions;
	
	@FXML
    private Button showTokenizedPanRevisions;
	
	@FXML
    private Button showPlainTextPanRevisions;
	
	@FXML
    private Button showWordsDiffPanRevisions;
	
	@FXML
    private Button showDiffPanRevisions;
	
	@FXML
    private Button showWpPages;
	
	@FXML
    private Button showWordSimilarity;
	
	@FXML
    private Button showPanFeatures;
	
	@FXML
    private TextField windowSize;
	
	@FXML
    private TextField vectorSize;
	
	@FXML
    private ChoiceBox<String> wvTrainMethod;

	
	@FXML
    private TextField wordToTest;

	@FXML
    private TextField rowsFilter;
	
	@FXML
    private TextField vectorModelFile;	
			
	@FXML
    private Button exportPanFeaturesToWeka;
	
	@FXML
    private Button selectModelFileName;

	@FXML
    private TextField rowsFilter2;

	@FXML
    private Label generateGraphWordListsStatus;
	
	@FXML
    private Button generateGraphWordLists;

	@FXML
    private ChoiceBox<String> rankingAlgorithm;
	
	@FXML
    private ChoiceBox<String> rankingGraphNo;

	@FXML
    private ChoiceBox<String> rankingRegularVandalic;
	
	@FXML
    private Button generateSDAVEModel;
	
	@FXML
    private Label generateSDAVEModelStatus;

	@FXML
    private Button importSDAFeatures;

	@FXML	
	private Label importSDAFeaturesStatus;
	
	@FXML
    private Button evaluateFeatures;

	@FXML
    private Label evaluateFeaturesStatus;
	
	
	private MainApp mainApp;
	private ConcurrentService concurrentService;
	
	private String rankingAlgorithmValue = "";
	private String rankingRegularVandalicValue = "";
	
	
	public RootController() {
	}

	@FXML
    private void initialize() {
		
		Config config = new Config();
		
		vectorModelFile.setText(config.getWordVectorModelFile());
		
		wvTrainMethod.setItems(FXCollections.observableArrayList("Continuous Bag of Words", "Skip Gram"));	
		wvTrainMethod.setValue(config.getWvTrainMethod());
		windowSize.setText(String.valueOf(config.getWvWindowSize()));
		vectorSize.setText(String.valueOf(config.getWvVectorSize()));
			
		wvTrainMethod.getSelectionModel().selectedItemProperty().addListener(new
			ChangeListener<String>() {
				@SuppressWarnings("rawtypes")
				public void changed(ObservableValue ov, String value, String new_value) {
				    config.setWvTrainMethod(new_value);
				    config.saveProperties();
				}
			});
		
		rankingAlgorithm.setItems(FXCollections.observableArrayList("","Degree","Eigenvector Centrality","PageRank","HITS"));
		rankingAlgorithm.setValue("All");

		rankingAlgorithm.getSelectionModel().selectedItemProperty().addListener(new
				ChangeListener<String>() {
					@SuppressWarnings("rawtypes")
					public void changed(ObservableValue ov, String value, String new_value) {
						generateGraphWordListsStatus.setText("");
						rankingAlgorithmValue = new_value;
					}
				});
		
		rankingRegularVandalic.setItems(FXCollections.observableArrayList(
				"", "Regular Graph 1", "Regular Graph 2", "Regular Graph 3", "Regular Graph 4", 
				"Regular Ranking 1", "Regular Ranking 2", "Regular Ranking 3", "Regular Ranking 4",
				"Vandalic Graph and Ranking"));
		rankingRegularVandalic.setValue("");

		rankingRegularVandalic.getSelectionModel().selectedItemProperty().addListener(new
				ChangeListener<String>() {
					@SuppressWarnings("rawtypes")
					public void changed(ObservableValue ov, String value, String new_value) {
						generateGraphWordListsStatus.setText("");
						rankingRegularVandalicValue = new_value;
					}
				});
		
		windowSize.textProperty().addListener((observable, oldValue, newValue) -> {
		    config.setWvWindowSize(Integer.parseInt(windowSize.getText()));
		    config.saveProperties();
		});
		
		vectorSize.textProperty().addListener((observable, oldValue, newValue) -> {
		    config.setWvVectorSize(Integer.parseInt(vectorSize.getText()));
		    config.saveProperties();
		});
		
		concurrentService = new ConcurrentService();
		
        concurrentService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
            	
            	switch (concurrentService.getTaskNo()) {
            		case 1 :
            			loadCorpusStatus.setText("Succeeded...");
            			break;
            		case 2 :
            			loadPagesStatus.setText("Succeeded...");
            			break;
            		case 3 :
            			generateFeaturesStatus.setText("Succeeded...");
            			break;
            		case 4 :
            			loadEditsStatus.setText("Succeeded...");
            			break;   
            		case 5 :
            			loadRevisionsStatus.setText("Succeeded...");
            			break;   
            		case 6 :
            			tokenizeRevisionsStatus.setText("Succeeded...");
            			break;   
            		case 7 :
            			extractPlainTextOfRevisionsStatus.setText("Succeeded...");
            			break;   
            		case 8 :
            			buildWordVectorModelStatus.setText("Succeeded...");
            			break;
            		case 9 :
            			extractWordsDiffOfPanRevisionsStatus.setText("Succeeded...");
            			break;
            		case 10 :
            			extractWordSimilaritiesStatus.setText("Succeeded...");
            			break;    
            		case 11 :
            			extractDiffOfPanRevisionsStatus.setText("Succeeded...");
            			break;
            		case 12 :
            			generateGraphWordListsStatus.setText("Succeeded...");
            			break;
            		case 13 :
            			generateSDAVEModelStatus.setText("Succeeded...");
            			break;
            		case 14 :
            			evaluateFeaturesStatus.setText("Succeeded...");
            			break;
            	}
            	            	
            	statusBar.setText("Ready");
            	concurrentService.reset();
            }
        });
 
        concurrentService.setOnRunning(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
            	
            	switch (concurrentService.getTaskNo()) {
        		case 1 :
        			loadCorpusStatus.setText("Running...");
        			break;
        		case 2 :
        			loadPagesStatus.setText("Running...");
        			break;   
        		case 3 :
        			generateFeaturesStatus.setText("Running...");
        			break;  
        		case 4 :
        			loadEditsStatus.setText("Running...");
        			break;   
        		case 5 :
        			loadRevisionsStatus.setText("Running...");
        			break;   
        		case 6 :
        			tokenizeRevisionsStatus.setText("Running...");
        			break;     
          		case 7 :
          			extractPlainTextOfRevisionsStatus.setText("Running...");
        			break;   
          		case 8 :
          			buildWordVectorModelStatus.setText("Running...");
        			break;
          		case 9 :
          			extractWordsDiffOfPanRevisionsStatus.setText("Running...");
        			break;
          		case 10 :
          			extractWordSimilaritiesStatus.setText("Running...");
        			break;     
         		case 11 :
          			extractDiffOfPanRevisionsStatus.setText("Running...");
        			break;
         		case 12 :
         			generateGraphWordListsStatus.setText("Running...");
        			break;
        		case 13 :
        			generateSDAVEModelStatus.setText("Running...");
        			break;        			
        		case 14 :
        			evaluateFeaturesStatus.setText("Running...");
        			break;        			
            	}            	
            }
        });
 
        concurrentService.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
            	switch (concurrentService.getTaskNo()) {
        		case 1 :
        			loadCorpusStatus.setText("Failed...");
        			break;
        		case 2 :
        			loadPagesStatus.setText("Failed...");
        			break;
         		case 3 :
        			generateFeaturesStatus.setText("Failed...");
        			break;   
        		case 4 :
        			loadEditsStatus.setText("Failed...");
        			break;   
        		case 5 :
        			loadRevisionsStatus.setText("Failed...");
        			break;   
        		case 6 :
        			tokenizeRevisionsStatus.setText("Failed...");
        			break;      
        		case 7 :
        			extractPlainTextOfRevisionsStatus.setText("Failed...");
        			break;
        		case 8 :
        			buildWordVectorModelStatus.setText("Failed...");
        			break;
        		case 9 :
        			extractWordsDiffOfPanRevisionsStatus.setText("Failed...");
        			break;
        		case 10 :
        			extractWordSimilaritiesStatus.setText("Failed...");
        			break;  
        		case 11 :
        			extractDiffOfPanRevisionsStatus.setText("Failed...");
        			break;
        		case 12 :
        			generateGraphWordListsStatus.setText("Failed...");
        			break;
        		case 13 :
        			generateSDAVEModelStatus.setText("Failed...");
        			break;        			        			
        		case 14 :
        			evaluateFeaturesStatus.setText("Failed...");
        			break;        			        			
            	}
            }
        });
		
		clearMenuItem.setOnAction((event) -> {
			mainApp.clearCenterPane();
			statusBar.setText("Ready");
		});
		
		closeMenuItem.setOnAction((event) -> {
			mainApp.closeApp();
		});
		
		configMenuItem.setOnAction((event) -> {
			mainApp.showConfig();
		});
		
		aboutMenuItem.setOnAction((event) -> {
			try {
				
		        FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(MainApp.class.getResource("view/About.fxml"));
		        AnchorPane aboutLayout = (AnchorPane) loader.load();
				Stage dialog = new Stage();
				dialog.initStyle(StageStyle.UTILITY);
				dialog.initModality(Modality.APPLICATION_MODAL);
				dialog.setResizable(false);
				Scene scene = new Scene(aboutLayout);
				dialog.setScene(scene);
				dialog.show();
				
			} catch (Exception e) {
				e.printStackTrace();
			}		
		});
		
		loadPanCorpus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	
               	Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setTitle("Confirmation Dialog");
            	alert.setHeaderText("This proccess can take a long time.");
            	alert.setContentText("Are you sure?");

            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == ButtonType.OK) {              		
                	if (concurrentService.getState() == State.READY) {
                    	concurrentService.setTaskNo(1);
                        concurrentService.start();            		
                	}
            	}
            }
        });		
		
//		loadWpPages.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent t) {
//               	
//            	Alert alert = new Alert(AlertType.CONFIRMATION);
//            	alert.setTitle("Confirmation Dialog");
//            	alert.setHeaderText("This proccess can take a long time.");
//            	alert.setContentText("Are you sure?");
//
//            	Optional<ButtonType> result = alert.showAndWait();
//            	if (result.get() == ButtonType.OK) {              		
//                	if (concurrentService.getState() == State.READY) {            	
//                		concurrentService.setTaskNo(2);
//                		concurrentService.start();
//                	}
//            	}            	
//            }
//        });
		
		generatePanFeatures.setOnAction(new EventHandler<ActionEvent>() {			
            @Override
            public void handle(ActionEvent t) {
            	
            	Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setTitle("Confirmation Dialog");
            	alert.setHeaderText("This proccess can take a long time.");
            	alert.setContentText("Are you sure?");

            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == ButtonType.OK) {              		
                	if (concurrentService.getState() == State.READY) {            	
            			concurrentService.setTaskNo(3);
                		concurrentService.start();
                	}
            	}
            }
        });
		
		loadPanEdits.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	
               	Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setTitle("Confirmation Dialog");
            	alert.setHeaderText("This proccess can take a long time.");
            	alert.setContentText("Are you sure?");

            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == ButtonType.OK) {              		
                	if (concurrentService.getState() == State.READY) {
                    	concurrentService.setTaskNo(4);
                        concurrentService.start();            		
                	}
            	}
            }
        });		
		
		loadPanRevisions.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	
               	Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setTitle("Confirmation Dialog");
            	alert.setHeaderText("This proccess can take a long time.");
            	alert.setContentText("Are you sure?");

            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == ButtonType.OK) {              		
                	if (concurrentService.getState() == State.READY) {
                    	concurrentService.setTaskNo(5);
                        concurrentService.start();            		
                	}
            	}
            }
        });		
		
		tokenizePanRevisions.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	
               	Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setTitle("Confirmation Dialog");
            	alert.setHeaderText("This proccess can take a long time.");
            	alert.setContentText("Are you sure?");

            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == ButtonType.OK) {              		
                	if (concurrentService.getState() == State.READY) {
                    	concurrentService.setTaskNo(6);
                        concurrentService.start();            		
                	}
            	}
            }
        });		
		
		extractPlainTextOfPanRevisions.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	
               	Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setTitle("Confirmation Dialog");
            	alert.setHeaderText("This proccess can take a long time.");
            	alert.setContentText("Are you sure?");

            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == ButtonType.OK) {              		
                	if (concurrentService.getState() == State.READY) {
                    	concurrentService.setTaskNo(7);
                        concurrentService.start();            		
                	}
            	}
            }
        });		
		
		buildWordVectorModel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	
               	Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setTitle("Confirmation Dialog");
            	alert.setHeaderText("This proccess can take a long time.");
            	alert.setContentText("Are you sure?");

            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == ButtonType.OK) {              		
                	if (concurrentService.getState() == State.READY) {
                    	concurrentService.setTaskNo(8);
                        concurrentService.start();            		
                	}
            	}
            }
        });	
		
		extractWordsDiffOfPanRevisions.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	
               	Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setTitle("Confirmation Dialog");
            	alert.setHeaderText("This proccess can take a long time.");
            	alert.setContentText("Are you sure?");

            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == ButtonType.OK) {              		
                	if (concurrentService.getState() == State.READY) {
                    	concurrentService.setTaskNo(9);
                        concurrentService.start();            		
                	}
            	}
            }
        });	
		
		extractWordSimilarities.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	
               	Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setTitle("Confirmation Dialog");
            	alert.setHeaderText("This proccess can take a long time.");
            	alert.setContentText("Are you sure?");

            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == ButtonType.OK) {              		
                	if (concurrentService.getState() == State.READY) {
                    	concurrentService.setTaskNo(10);
                        concurrentService.start();            		
                	}
            	}
            }
        });	
		
		extractDiffOfPanRevisions.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	
               	Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setTitle("Confirmation Dialog");
            	alert.setHeaderText("This proccess can take a long time.");
            	alert.setContentText("Are you sure?");

            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == ButtonType.OK) {              		
                	if (concurrentService.getState() == State.READY) {
                    	concurrentService.setTaskNo(11);
                        concurrentService.start();            		
                	}
            	}
            }
        });	
		
		generateGraphWordLists.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	
               	Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setTitle("Confirmation Dialog");
            	alert.setHeaderText("This proccess can take a long time.");
            	alert.setContentText("Are you sure?");

            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == ButtonType.OK) {              		
                	if (concurrentService.getState() == State.READY) {
                    	concurrentService.setTaskNo(12);
                        concurrentService.start();            		
                	}
            	}
            }
        });			


		generateSDAVEModel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	
               	Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setTitle("Confirmation Dialog");
            	alert.setHeaderText("This proccess can take a long time.");
            	alert.setContentText("Are you sure?");

            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == ButtonType.OK) {              		
                	if (concurrentService.getState() == State.READY) {
                    	concurrentService.setTaskNo(13);
                        concurrentService.start();            		
                	}
            	}
            }
        });	
		
		importSDAFeatures.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	importSDAFeaturesStatus.setText("Running...");
            	mainApp.python2Table();
            	importSDAFeaturesStatus.setText("Suceed...");
            }
        });	
		
		evaluateFeatures.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
            	
               	Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setTitle("Confirmation Dialog");
            	alert.setHeaderText("This proccess can take a long time.");
            	alert.setContentText("Are you sure?");

            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == ButtonType.OK) {              		
                	if (concurrentService.getState() == State.READY) {
                    	concurrentService.setTaskNo(14);
                        concurrentService.start();            		
                	}
            	}
            }
        });	
		
		showWordSimilarity.setOnAction((event) -> {
			statusBar.setText("Showing Word Similarities...");
			mainApp.showSimilarityList("Word Similarities List", vectorModelFile.getText(), wordToTest.getText());
		});
		
		showPanEdits.setOnAction((event) -> {
			statusBar.setText("Showing edits...");
			mainApp.showSqlGrid("PAN Edits","SELECT * FROM " + 
				config.getPanEditTable() + "_" + config.getPanCorpus(),rowsFilter.getText());		
		});
		
		showPanRevisions.setOnAction((event) -> {
			statusBar.setText("Showing revisions...");
			mainApp.showSqlGrid("PAN Article Revisions","SELECT * FROM " + 
				config.getPanArticleRevTable() + "_" + config.getPanCorpus(),rowsFilter.getText());			
		});
		
		showTokenizedPanRevisions.setOnAction((event) -> {
			statusBar.setText("Showing tokenized revisions...");
			mainApp.showSqlGrid("PAN Tokenized Article Revisions","SELECT * FROM " + 
				config.getTokenizedPanArticleRevTable() + "_" + config.getPanCorpus(),rowsFilter.getText());			
		});
		
		showPlainTextPanRevisions.setOnAction((event) -> {
			statusBar.setText("Showing plain text revisions...");
			mainApp.showSqlGrid("PAN Plain Text Article Revisions","SELECT * FROM " + 
				config.getPlainTextPanArticleRevTable() + "_" + config.getPanCorpus(),rowsFilter.getText());			
		});
		
		showWordsDiffPanRevisions.setOnAction((event) -> {
			statusBar.setText("Showing inserted and deleted words...");
			mainApp.showSqlGrid("PAN Inserted And Deleted Words On Article Revisions","SELECT * FROM " + 
				config.getPanWordsDiffTable() + "_" + config.getPanCorpus(),rowsFilter.getText());			
		});
		
		showDiffPanRevisions.setOnAction((event) -> {
			statusBar.setText("Showing diffs...");
			mainApp.showSqlGrid("PAN Diffs On Article Revisions","SELECT * FROM " + 
				config.getPanDiffTable() + "_" + config.getPanCorpus(),rowsFilter.getText());			
		});
		
		showPanFeatures.setOnAction((event) -> {
			statusBar.setText("Showing features...");
			mainApp.showSqlGrid("PAN Features","SELECT * FROM " + 
				config.getPanFeatureTable() + "_" + config.getPanCorpus(),rowsFilter2.getText());		
		});
		
		exportPanFeaturesToWeka.setOnAction((event) -> {						
			statusBar.setText("Exporting features to Weka ARFF format...");
			if (mainApp.exportPanFeaturesToWeka()) {
				statusBar.setText("Succeeded...");				
			} else {
				statusBar.setText("Cancelled...");
			}
		});
		
		selectModelFileName.setOnAction((event) -> {			
			String fileName = mainApp.selectModelFile(); 
			if (fileName.equals("")) {
				statusBar.setText("Cancelled...");				
			} else {
				vectorModelFile.setText(fileName);
				statusBar.setText("Succeeded...");
			}
		});
					
//		showWpPages.setOnAction((event) -> {
//			statusBar.setText("Showing pages meta history...");
//			mainApp.showSqlGrid("Wikipedia Page Revisions","SELECT * FROM " +
//				config.getWpPageMetaHistoryTable(),rowsFilter.getText());			
//		});	
		
	}	
	
	public void setStatusText(String text) {
		statusBar.setText(text);
	}
	
	public void clearCenterPane() {
		mainApp.clearCenterPane();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
		
    private class ConcurrentService extends Service<Void> {
    	private int taskNo;
    	
		public void setTaskNo(int taskNo) {
			this.taskNo = taskNo;
		}
        
		public int getTaskNo() {
			return taskNo;
		}
		@Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                	switch (taskNo) {
                		case 1 :
                			mainApp.loadPan();	
                			break;
                		case 2 :
                			mainApp.loadPagesMetaHistory();	
                			break;
                		case 3 :
                			mainApp.generatePanFeatures();	
                			break;
                		case 4 :
                			mainApp.loadEdits();	
                			break;
                		case 5 :
                			mainApp.loadRevisions();	
                			break;
                		case 6 :
                			mainApp.tokenizeRevisions();	
                			break;
                		case 7 :
                			mainApp.extractPlainTextOfRevisions();	
                			break;
                		case 8 :
                			mainApp.buildWordVectorModel(vectorModelFile.getText());	
                			break;
                		case 9 :
                			mainApp.extractWordsDiffsOfRevisions();	
                			break;
                		case 10 :
                			mainApp.extractWordSimilarities(vectorModelFile.getText());	
                			break;                			                			
                		case 11 :
                			mainApp.extractDiffsOfRevisions();	
                			break;
                		case 12 :
                			mainApp.generateGraphWordLists(rankingAlgorithmValue, rankingRegularVandalicValue);	
                			break;
                		case 13 :
                			mainApp.createDNNVEModel();	
                			break;                			
                		case 14 :
                			mainApp.evaluateFeatures();	
                			break;                			
                	}                	
                    return null;
                }
            };
        }
    }
	
}
