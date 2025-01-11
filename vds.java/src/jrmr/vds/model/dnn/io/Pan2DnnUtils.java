package jrmr.vds.model.dnn.io;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import jrmr.vds.model.config.Config;
import jrmr.vds.model.db.DatabaseUtils;

public class Pan2DnnUtils {

	Config config;
	
	public Pan2DnnUtils() {
		config = new Config();
	}
	
	public void writeFeatures2DnnCsv() {

		File outputFile1 = new File("D:\\workspace\\vds.deeplearning1\\resources\\features_all_" + config.getPanCorpus() + "_input.txt");
		File outputFile2 = new File("D:\\workspace\\vds.deeplearning1\\resources\\features_all_" + config.getPanCorpus() + "_target.txt");
		String tableName = config.getPanFeatureTable() + "_" + config.getPanCorpus();
		int classColumn = 1;
		
		Statement st = null;
		ResultSet rs = null;
		String sqlStatement = "SELECT * FROM " + tableName;
		
		//String sqlStatement = 
		//	"SELECT * FROM feature_test WHERE vandalism = true " +
		//	"UNION " +
		//	"(SELECT * FROM feature_test WHERE vandalism = false LIMIT 1481)";		
		
		//String sqlStatement = 
		//	"SELECT * FROM feature_training WHERE vandalism = true " +
		//	"UNION " +
		//	"(SELECT * FROM feature_training WHERE vandalism = false LIMIT 921)";
		
		FileWriter fileWriter1 = null;
		PrintWriter printWriter1 = null;
		FileWriter fileWriter2 = null;
		PrintWriter printWriter2 = null;
		
		DatabaseUtils databaseUtils = new DatabaseUtils();

		try 
		{
			Connection con = databaseUtils.openConnection2();	 
			
			fileWriter1 = new FileWriter(outputFile1);
			printWriter1 = new PrintWriter(fileWriter1);
			fileWriter2 = new FileWriter(outputFile2);
			printWriter2 = new PrintWriter(fileWriter2);        	          


			try {
				st = con.createStatement();
				rs = st.executeQuery(sqlStatement);

				while(rs.next()) {
					String dataLine = "";
					String column = "";
					for(int i = 2; i <= rs.getMetaData().getColumnCount(); i++) { // primary key and class excluded
						if ((i - 1) != classColumn) {
							column = rs.getString(i);														
							
							if (column.equals("t")) {
								column = "1.0";
							};
							if (column.equals("f")) {
								column = "0.0";								
							}
							
							float f = Float.parseFloat(column);
							if (f == 0.0) {
								f = Float.MIN_VALUE;
								column = Float.toString(f);
							}
							
								
							if (dataLine.equals("")) {
								dataLine = column;
							} else {
								dataLine = dataLine + "," + column;						
							}
						}
					}
					printWriter1.println(dataLine);
					
					column = rs.getString(classColumn + 1);
					if (column.equals("t")) {
						column = "1.0,0.0";
					};
					if (column.equals("f")) {
						column = "0.0,1.0";								
					}
					printWriter2.println(column);
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

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fileWriter1) 
					fileWriter1.close();
				if (null != fileWriter2) 
					fileWriter2.close();				
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

}
