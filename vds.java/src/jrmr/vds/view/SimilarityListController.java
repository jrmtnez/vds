package jrmr.vds.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import jrmr.vds.model.ext.word2vec.WordSimilarity;

public class SimilarityListController {
	@FXML
	private TitledPane titledPane;

    @FXML
    private TableView<WordSimilarity> wordSimilarity;
    
    @FXML
    private TableColumn<WordSimilarity, String> wordColumn;
    
    @FXML
    private TableColumn<WordSimilarity, Number> resultColumn;
	
    @FXML
    private Label wordLabel;
    
    @FXML
    private Label resultLabel;  
    
    public SimilarityListController() {
    	
    }
    
    @FXML
    private void initialize() {
    	wordColumn.setCellValueFactory(cellData -> cellData.getValue().wordProperty());
    	resultColumn.setCellValueFactory(cellData -> cellData.getValue().scoreProperty());
    }
    
    public void buildData (String paneTitle, ObservableList<WordSimilarity> wordSimilarityList) {
    	titledPane.setText(paneTitle);
    	wordSimilarity.setItems(wordSimilarityList);
    }    		
}
