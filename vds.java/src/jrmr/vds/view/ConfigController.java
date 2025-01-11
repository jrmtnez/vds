package jrmr.vds.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import jrmr.vds.model.config.Config;
import jrmr.vds.model.db.DatabaseUtils;

public class ConfigController {
	
	Config config;
	DatabaseUtils databaseUtils;
	
	@FXML
    private TextField databaseURL;
	
	@FXML
    private TextField databaseUser;

	@FXML
    private TextField databasePassword;
	
	@FXML
    private TextField editsFile;
	
	@FXML
    private TextField goldAnnotationsFile;
	
	@FXML
    private TextField articleRevFolder;
	
	@FXML
    private TextField pagesMetaHistoryFile;
	
	@FXML
    private TextField editsTable;
	
	@FXML
    private TextField articleRevTable;
	
	@FXML
    private TextField tokenizedArticleRevTable;
	
	@FXML
    private TextField plainTextArticleRevTable;
	
	@FXML
    private TextField featureTable;
	
	@FXML
    private TextField panCorpus;
	
	@FXML
    private TextField onlyWithClass;

	@FXML
    private TextField pagesMetaHistoryTable;
	
	@FXML
	private TextField wordsDiffTable;
	
	@FXML
	private TextField diffTable;
	
	@FXML
	private TextField wordVectorModelFile;
	
	
	@FXML
    private Button cancelConfig;
	
	@FXML
    private Button saveConfig;	

	@FXML
    private Button testConnection;

	@FXML
    private Button initDB;
	
	private RootController rootController;
	
	public ConfigController() {
		
	}

	@FXML
    private void initialize() {   
		config = new Config();
		databaseUtils = new DatabaseUtils();
	   
		databaseURL.setText(config.databaseURLProperty().getValue());
		databaseUser.setText(config.databaseUserProperty().getValue());
		databasePassword.setText(config.databasePasswordProperty().getValue());
		
		editsFile.setText(config.panEditFileProperty().getValue());		
		goldAnnotationsFile.setText(config.panGoldAnnotationsFileProperty().getValue());		
		articleRevFolder.setText(config.panArticleRevFolderProperty().getValue());
		editsTable.setText(config.panEditTableProperty().getValue());		
		articleRevTable.setText(config.panArticleRevTableProperty().getValue());
		tokenizedArticleRevTable.setText(config.panTokenizedArticleRevTableProperty().getValue());
		plainTextArticleRevTable.setText(config.panPlainTextArticleRevTableProperty().getValue());
		featureTable.setText(config.panFeatureTableProperty().getValue());
		panCorpus.setText(config.panCorpusProperty().getValue());
		onlyWithClass.setText(config.onlyWithClassProperty().getValue());
		wordsDiffTable.setText(config.panWordsDiffTableProperty().getValue());
		diffTable.setText(config.panDiffTableProperty().getValue());
		
//		pagesMetaHistoryFile.setText(config.wpPageMetaHistoryFileProperty().getValue());
//		pagesMetaHistoryTable.setText(config.wpPageMetaHistoryTableProperty().getValue());
		
		wordVectorModelFile.setText(config.wordVectorModelFileProperty().getValue());
		
		cancelConfig.setOnAction((event) -> {
		    rootController.setStatusText("Changes discarted");
		    rootController.clearCenterPane();		    
		});
		
		saveConfig.setOnAction((event) -> {
		    config.databaseURLProperty().setValue(databaseURL.getText());
		    config.databaseUserProperty().setValue(databaseUser.getText());
		    config.databasePasswordProperty().setValue(databasePassword.getText());
		    
		    config.panEditFileProperty().setValue(editsFile.getText());
		    config.panGoldAnnotationsFileProperty().setValue(goldAnnotationsFile.getText());
		    config.panArticleRevFolderProperty().setValue(articleRevFolder.getText());
		    config.panEditTableProperty().setValue(editsTable.getText());
		    config.panArticleRevTableProperty().setValue(articleRevTable.getText());
		    config.panTokenizedArticleRevTableProperty().setValue(tokenizedArticleRevTable.getText());		    
		    config.panPlainTextArticleRevTableProperty().setValue(plainTextArticleRevTable.getText());
		    config.panFeatureTableProperty().setValue(featureTable.getText());
		    config.panCorpusProperty().setValue(panCorpus.getText());
		    config.onlyWithClassProperty().setValue(onlyWithClass.getText());
		    config.panWordsDiffTableProperty().setValue(wordsDiffTable.getText());
		    config.panDiffTableProperty().setValue(diffTable.getText());		    
		    
		    config.wpPageMetaHistoryFileProperty().setValue(pagesMetaHistoryFile.getText());
		    config.wpPageMetaHistoryTableProperty().setValue(pagesMetaHistoryTable.getText());

		    config.wordVectorModelFileProperty().setValue(wordVectorModelFile.getText());
		    
		    config.saveProperties();		    
		    rootController.setStatusText("Configuration saved");
		    rootController.clearCenterPane();		    
		});
		
		testConnection.setOnAction((event) -> {
			
			String header = null;
			String body = null;
						
			if (databaseUtils.testDBConnection(databaseURL.getText(),databaseUser.getText(),databasePassword.getText())) {
				header  = "Connection established.";
			} else {
				header  = "Connection error.";
			}
			body = databaseUtils.getDBVersion(databaseURL.getText(),databaseUser.getText(),databasePassword.getText());
			
        	Alert alert = new Alert(AlertType.INFORMATION);
        	alert.setTitle("Information Dialog");
        	alert.setHeaderText(header);
        	alert.setContentText(body);
        	alert.showAndWait();
        		        
		});
		
		
		initDB.setOnAction((event) -> {
			
			String header = null;
			String body = null;
						
			if (databaseUtils.initDatabase2()) {
				header  = "Database created.";
			} else {
				header  = "Connection error.";
			}
			body = databaseURL.getText();
			
        	Alert alert = new Alert(AlertType.INFORMATION);
        	alert.setTitle("Information Dialog");
        	alert.setHeaderText(header);
        	alert.setContentText(body);
        	alert.showAndWait();
        		        
		});
		
		
		
	}
	
    public void setRootController(RootController rootController) {
        this.rootController = rootController;
    }
}
