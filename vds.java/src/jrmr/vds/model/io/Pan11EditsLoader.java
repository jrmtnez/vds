package jrmr.vds.model.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

import jrmr.vds.model.config.Config;
import jrmr.vds.model.db.DatabaseUtils;

public class Pan11EditsLoader {
	
	Config config;
	
	public Pan11EditsLoader() {
		config = new Config();
	}

	public void load() {
		try {			
			String inputFile = config.getPanEditFile();
			FileInputStream fiStream;
			fiStream = new FileInputStream(inputFile);
			DataInputStream diStream = new DataInputStream(fiStream);		
			BufferedReader docReader = new BufferedReader(new InputStreamReader(diStream,Charset.forName("UTF-8")));
	        String docLine;	 
	        boolean columnHeaders = true;
		        
	        DatabaseUtils databaseUtils = new DatabaseUtils();   	
	        databaseUtils.openConnection();	        
	        databaseUtils.createTable(databaseUtils.getCreatePanEditTableStatement());	        	        
	        
	        while ((docLine = docReader.readLine()) != null) 
	        {
	        	if (!columnHeaders) {
	        		

			        String lineItems[] = docLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			        
			        // x(?=y)		Coincide con 'x' solo si 'x' está seguida de 'y'.
			        // (	
			        //  [^\"]*
			        //  \"			
			        //  [^\"]*
			        //  \"			
			        // )*			Secuencias que contienen un número de comillas pares.
			        // [^\"]*		Secuencias sin comillas.
			        // $			Fin de línea.
			        
			        int editId = Integer.parseInt(lineItems[0]);
			        String editor = extractText(lineItems[1]);
			        int oldRevisionId = Integer.parseInt(lineItems[2]);
			        int newRevisionId = Integer.parseInt(lineItems[3]);
			        String diffUrl = extractText(lineItems[4]);
			        String revisionClass = extractText(lineItems[5]);
			        int annotators = Integer.parseInt(lineItems[6]);
			        int totalAnnotators = Integer.parseInt(lineItems[7]);
			        ZonedDateTime editTime2 = ZonedDateTime.parse(extractText(lineItems[8])); 
			        Timestamp editTime = Timestamp.valueOf(
					  editTime2.getYear() + "-" +
					  editTime2.getMonthValue()+ "-" + 
					  editTime2.getDayOfMonth() + " " + 
					  editTime2.getHour() + ":" +
					  editTime2.getMinute() + ":" +
					  editTime2.getSecond());
			        String editComment = extractText(lineItems[9]);
		        	int articleId = Integer.parseInt(lineItems[10]);
		        	String articleTitle = extractText(lineItems[11]);
			        
			        System.out.println(
		        		"editId:          "  + editId + "\n" +
		        		"editor:          "  + editor + "\n" +
		        		"oldRevisionId:   "  + oldRevisionId + "\n" +
		        		"newRevisionId:   "  + newRevisionId + "\n" +
		        		"diffUrl:         "  + diffUrl + "\n" +
		        		"revisionClass:   "  + revisionClass + "\n" +
		        		"annotators:      "  + annotators + "\n" +
		        		"totalAnnotators: "  + totalAnnotators + "\n" +
		        		"editTime:        "  + editTime2.toString() + "\n" +
		        		"editComment:     "  + editComment + "\n" +
		        		"articleId:       "  + articleId + "\n" +
		        		"articleTitle:    "  + articleTitle + "\n");
	        			        
			        databaseUtils.insertPanEdit(
			        	editId, 
			        	editor, 
			        	oldRevisionId, 
			        	newRevisionId, 
			        	diffUrl, 
			        	revisionClass, 
			        	annotators, 
			        	totalAnnotators, 
			        	editTime, 
			        	editComment, 
			        	articleId, 
			        	articleTitle);	        	
	        	}
	        		
	        	columnHeaders = false;
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
	
	public static String extractText(String inputText) {
		String text = "";
        
		if (inputText.length() > 2)
			text = inputText.substring(1, inputText.length() - 1);
		
		return text;
	}	
}
