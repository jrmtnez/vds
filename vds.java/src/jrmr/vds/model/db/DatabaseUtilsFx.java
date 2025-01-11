package jrmr.vds.model.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class DatabaseUtilsFx {
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public TableColumn[] selectGridTableColumns(String sqlStatement) {

		DatabaseUtils databaseUtils = new DatabaseUtils();
		
		Connection con =  databaseUtils.openConnection2();
		Statement st = null;
		ResultSet rs = null;
		TableColumn[] col = null;

		try {
			st = con.createStatement();
			rs = st.executeQuery(sqlStatement + " LIMIT 1");

			col = new TableColumn[rs.getMetaData().getColumnCount()];

			for(int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				final int j = i;               
				
				col[i] = new TableColumn(rs.getMetaData().getColumnName(i + 1));
				
				col[i].setSortable(true);
				
				col[i].setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>() {                   
					public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                             
						return new SimpleStringProperty(param.getValue().get(j).toString());                       
					}                   
				});
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}				
				if (con != null) {
					con.close();
				}
				
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return col;        
	}
	
	public ObservableList<ObservableList<?>> selectGridTableData(String sqlStatement) {
		
		DatabaseUtils databaseUtils = new DatabaseUtils();
		
		Connection con =  databaseUtils.openConnection2();
		Statement st = null;
		ResultSet rs = null;        
		ObservableList<ObservableList<?>> data = null;

		try {
			st = con.createStatement();
			rs = st.executeQuery(sqlStatement + " LIMIT 10000");
			data = FXCollections.observableArrayList();

			while(rs.next()){
				ObservableList<String> row = FXCollections.observableArrayList();
				for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++){
					row.add(rs.getString(i));
				}
				data.add(row);
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (con != null) {
					con.close();
				}
				
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return data;        
	}
}
