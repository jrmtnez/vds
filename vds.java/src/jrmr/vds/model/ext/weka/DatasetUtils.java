package jrmr.vds.model.ext.weka;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import jrmr.vds.model.db.DatabaseUtils;
import weka.core.Instances;

public class DatasetUtils {

	Instances maintainAttributes(Instances dataset, int[] attributes, boolean showList) {
		
		int total = dataset.numAttributes() - 2;
		for (int i = total; i >= 0; i--) {
			if (!contains(attributes, i)) {
				dataset.deleteAttributeAt(i);
			}			
		}

		if (showList) {
			for (int i = 0; i < dataset.numAttributes() - 1; i++) {
				System.out.println("-> " + convertAttributeName(dataset.attribute(i).toString()));
			}
		}
		
		return dataset;
	}
	
	public boolean contains(int[] v, int i) {
		
		for (int j = 0; j < v.length; j++) {
			if (v[j] == i)
			  return true;
		}
		return false;
	}
	
	public String convertAttributeName(String name) {
		name = name.replaceAll("@attribute ", "");		
		name = name.replaceAll(" numeric", "");
		name = name.replaceAll(" \\{f,t\\}", "");				
		return name;
	}
	
	public String convertAttributeMeasures(String measures) {
		measures = measures.replaceAll("\\.", ",");
		return measures;
	}
	
	
	public void arff2Table(String inputFile) {
		try {
			
			FileInputStream fiStream;
			fiStream = new FileInputStream(inputFile);
			DataInputStream diStream = new DataInputStream(fiStream);		
			BufferedReader docReader = new BufferedReader(new InputStreamReader(diStream,Charset.forName("UTF-8")));
	        String docLine;
	       	     
	        
	        DatabaseUtils databaseUtils = new DatabaseUtils();
	        databaseUtils.openConnection();
	        	        
	        boolean[] isString = new boolean[10000]; 
	        String sqlStatement = "";
	        boolean data = false;
	        int attribute = 0;
	        String tableName = "";
	        while ((docLine = docReader.readLine()) != null) 
	        {
	        	if (!data) {
	        		String[] lineTokens = docLine.split(" ");
	        		if (lineTokens[0].equals("@relation")) {
	        			tableName = lineTokens[1];
	        			sqlStatement = 
		        			"DROP TABLE IF EXISTS " + tableName + "; " +
	        				"CREATE TABLE " + tableName + "(\n";
	        		}
	        		
	        		if (lineTokens[0].equals("@attribute")) {
	        			String dataType = "";
	        			switch (lineTokens[2]) {
	        				case "numeric" :
	        					dataType = "double precision";
	        					break;
	        				case "{f,t}" :
	        					dataType = "text";
	        					isString[attribute] = true;
	        					break;
	        				case "{0,1}" :
	        					dataType = "text";
	        					isString[attribute] = true;
	        					break;
	        			}
	        			
	        			if (attribute == 0)
	        				sqlStatement = sqlStatement + lineTokens[1] + " " + dataType;
	        			else
	        				sqlStatement = sqlStatement + ",\n" + lineTokens[1] + " " + dataType;
	        			attribute++;
	        		}
	        		if (lineTokens[0].equals("@data")) {
	        			sqlStatement = sqlStatement + ");\n";
	        			data = true;
	        	        System.out.println(sqlStatement);	        			
	        			databaseUtils.createTable(sqlStatement);
	        		}
	        	} else {
	        		boolean firstAttribute = true;
	        		String[] lineTokens2 = docLine.split(",");
        			sqlStatement = "INSERT INTO " + tableName + " VALUES (";
        			for (int col = 0; col < lineTokens2.length; col++) {
        				String separator = "";
        				if (isString[col])
        					separator = "'";
        				if (firstAttribute) {
        					sqlStatement = sqlStatement + separator + lineTokens2[col] + separator;
        					firstAttribute = false;
        				} else {
        					sqlStatement = sqlStatement + "," + separator + lineTokens2[col] + separator;
        				}
        			}
        	        sqlStatement = sqlStatement + ");\n";
        	        System.out.println(sqlStatement);
        	        databaseUtils.createTable(sqlStatement);
	        	}
	        	
	        }
	        
	        docReader.close();
	        diStream.close();
	        fiStream.close();	
	        
	        databaseUtils.closeConnection();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void python2Table(String arffFile, String editIdFile) {
		try {
			
			FileInputStream fiStream;
			fiStream = new FileInputStream(arffFile);
			DataInputStream diStream = new DataInputStream(fiStream);		
			BufferedReader docReader = new BufferedReader(new InputStreamReader(diStream,Charset.forName("UTF-8")));
	        String docLine;
	       	     
			FileInputStream fiStream2;
			fiStream2 = new FileInputStream(editIdFile);
			DataInputStream diStream2 = new DataInputStream(fiStream2);		
			BufferedReader docReader2 = new BufferedReader(new InputStreamReader(diStream2,Charset.forName("UTF-8")));
	        String docLine2;

	        
	        DatabaseUtils databaseUtils = new DatabaseUtils();
	        databaseUtils.openConnection();
	        	        
	        boolean[] isString = new boolean[10000]; 
	        String sqlStatement = "";
	        boolean data = false;
	        int attribute = 0;
	        String tableName = "";
	        while ((docLine = docReader.readLine()) != null) 
	        {
	        	if (!data) {
	        		String[] lineTokens = docLine.split(" ");
	        		if (lineTokens[0].equals("@relation")) {
	        			tableName = lineTokens[1];	        				        			
	        			sqlStatement = 
	        				"DROP TABLE IF EXISTS " + tableName + "; " +
	        				"CREATE TABLE " + tableName + "(\n";  			
	        		}
	        		
	        		if (lineTokens[0].equals("@attribute")) {
	        			String dataType = "";
	        			switch (lineTokens[2]) {
	        				case "numeric" :
	        					dataType = "double precision";
	        					break;
	        				case "{f,t}" :
	        					dataType = "text";
	        					isString[attribute] = true;
	        					break;
	        				case "{0,1}" :
	        					dataType = "text";
	        					isString[attribute] = true;
	        					break;
	        			}
	        			
	        			if (attribute == 0)
	        				sqlStatement = sqlStatement + lineTokens[1] + " " + dataType;
	        			else
	        				sqlStatement = sqlStatement + ",\n" + lineTokens[1] + " " + dataType;
	        			
	        			attribute++;
	        		}
	        		if (lineTokens[0].equals("@data")) {
	        			
	        			sqlStatement = sqlStatement + ",\n edit_id integer, CONSTRAINT " + tableName + "_pk PRIMARY KEY (edit_id));\n";
	        			data = true;
	        	        System.out.println(sqlStatement);	        			
	        			databaseUtils.createTable(sqlStatement);
	        		}
	        	} else {
	        		boolean firstAttribute = true;
	        		String[] lineTokens2 = docLine.split(",");
        			sqlStatement = "INSERT INTO " + tableName + " VALUES (";
        			for (int col = 0; col < lineTokens2.length; col++) {
        				String separator = "";
        				if (isString[col])
        					separator = "'";
        				if (firstAttribute) {
        					sqlStatement = sqlStatement + separator + lineTokens2[col] + separator;
        					firstAttribute = false;
        				} else {
        					sqlStatement = sqlStatement + "," + separator + lineTokens2[col] + separator;
        				}
        			}
        			
        			if ((docLine2 = docReader2.readLine()) != null) {
        				sqlStatement = sqlStatement + ", " + docLine2;
        			}
        			
        	        sqlStatement = sqlStatement + ");\n";
        	        System.out.println(sqlStatement);
        	        databaseUtils.createTable(sqlStatement);
	        	}
	        	
	        }
	        
	        docReader.close();
	        diStream.close();
	        fiStream.close();
	        
	        docReader2.close();
	        diStream2.close();
	        fiStream2.close();	

	        
	        databaseUtils.closeConnection();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
