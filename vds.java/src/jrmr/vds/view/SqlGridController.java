package jrmr.vds.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TitledPane;
import jrmr.vds.model.db.DatabaseUtilsFx;

public class SqlGridController {
    
	@FXML
	private TitledPane sqlTitledPane;
	
	@FXML
    private TableView<ObservableList<?>> sqlTableView;
		
	@FXML
	private Label rowCounter;

	
	private ObservableList<ObservableList<?>> data;    

    public SqlGridController() {
    	
    }
    

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@FXML
    private void initialize() {
		
		sqlTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
				if(sqlTableView.getSelectionModel().getSelectedItem() != null) 
				{    
					TableViewSelectionModel selectionModel = sqlTableView.getSelectionModel();
					ObservableList selectedCells = selectionModel.getSelectedCells();
					TablePosition tablePosition = (TablePosition) selectedCells.get(0);
					rowCounter.setText(Integer.toString(tablePosition.getRow()));
					//System.out.println(sqlTableView.getSelectionModel().getSelectedItem().get(0));
				}
			}
		});
        
    }    
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void buildData(String paneTitle, String sqlStatement1, String sqlStatement2) {
    	
    	sqlTitledPane.setText(paneTitle);
    	    	
        DatabaseUtilsFx databaseUtilsFx = new DatabaseUtilsFx();
    	
    	String sqlWhere = "";
    	if (!sqlStatement2.equals("")) {
    		sqlWhere = " WHERE ";
    	}            	
    	
    	TableColumn[] col = databaseUtilsFx.selectGridTableColumns(sqlStatement1 + sqlWhere + sqlStatement2);
    	for (int i = 0; i < col.length; i++) {
    		sqlTableView.getColumns().addAll(col[i]);
    	}
    	
    	data = databaseUtilsFx.selectGridTableData(sqlStatement1 + sqlWhere + sqlStatement2);
    	sqlTableView.setItems((ObservableList<ObservableList<?>>) data);    		    	
    }  
}