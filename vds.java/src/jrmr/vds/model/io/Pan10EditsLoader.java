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

public class Pan10EditsLoader {
	
	Config config;
	
	public Pan10EditsLoader() {
		config = new Config();
	}

	public void load() {
		loadEdits();
		loadGoldAnnotations();
	}
	
	public void loadEdits() {
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
	        
	        int editId = 0;
	        String editor = "";
	        int oldRevisionId = 0;
	        int newRevisionId = 0;
	        String diffUrl = "";
	        ZonedDateTime editTime2 = null; 
	        Timestamp editTime = null;
	        String editComment = "";		        	
        	int articleId = 0;
        	String articleTitle = "";
	        
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
			        
			        
		        	switch (config.getPanCorpus()) {
			        	case "all" :
					        editId = Integer.parseInt(lineItems[0]);
					        editor = extractText(lineItems[1]);
					        oldRevisionId = Integer.parseInt(lineItems[2]);
					        newRevisionId = Integer.parseInt(lineItems[3]);
					        diffUrl = extractText(lineItems[4]);
					        editTime2 = ZonedDateTime.parse(extractText(lineItems[5])); 
					        editTime = Timestamp.valueOf(
							  editTime2.getYear() + "-" +
							  editTime2.getMonthValue()+ "-" + 
							  editTime2.getDayOfMonth() + " " + 
							  editTime2.getHour() + ":" +
							  editTime2.getMinute() + ":" +
							  editTime2.getSecond());
					        editComment = extractText(lineItems[6]);
					        articleId = Integer.parseInt(lineItems[7]);
				        	articleTitle = extractText(lineItems[8]);		        				        	
			        		break;		        		
			        	case "training" :
					        editId = Integer.parseInt(lineItems[0]);
					        editor = extractText(lineItems[1]);
					        oldRevisionId = Integer.parseInt(lineItems[2]);
					        newRevisionId = Integer.parseInt(lineItems[3]);
					        diffUrl = extractText(lineItems[4]);
					        editTime2 = ZonedDateTime.parse(extractText(lineItems[5])); 
					        editTime = Timestamp.valueOf(
							  editTime2.getYear() + "-" +
							  editTime2.getMonthValue()+ "-" + 
							  editTime2.getDayOfMonth() + " " + 
							  editTime2.getHour() + ":" +
							  editTime2.getMinute() + ":" +
							  editTime2.getSecond());
					        editComment = extractText(lineItems[6]);					        
				        	articleId = 0;
				        	articleTitle = extractText(lineItems[7]);
			        		break;
			        	case "test" :
			        		editId++;
					        editor = extractText(lineItems[0]);
					        oldRevisionId = Integer.parseInt(lineItems[1]);
					        newRevisionId = Integer.parseInt(lineItems[2]);
					        diffUrl = extractText(lineItems[3]);
					        editTime2 = ZonedDateTime.parse(extractText(lineItems[4])); 
					        editTime = Timestamp.valueOf(
							  editTime2.getYear() + "-" +
							  editTime2.getMonthValue()+ "-" + 
							  editTime2.getDayOfMonth() + " " + 
							  editTime2.getHour() + ":" +
							  editTime2.getMinute() + ":" +
							  editTime2.getSecond());
					        editComment = extractText(lineItems[5]);
					        articleId = Integer.parseInt(lineItems[6]);
				        	articleTitle = extractText(lineItems[7]);		        				        	
			        		break;
		        	}
		        	
			        System.out.println(
		        		"editId:          "  + editId + "\n" +
		        		"editor:          "  + editor + "\n" +
		        		"oldRevisionId:   "  + oldRevisionId + "\n" +
		        		"newRevisionId:   "  + newRevisionId + "\n" +
		        		"diffUrl:         "  + diffUrl + "\n" +
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
			        	"", 
			        	0, 
			        	0, 
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
	
	public void loadGoldAnnotations() {
		try {
			String inputFile = config.getPanGoldAnnotationsFile();
			FileInputStream fiStream;
			fiStream = new FileInputStream(inputFile);
			DataInputStream diStream = new DataInputStream(fiStream);		
			BufferedReader docReader = new BufferedReader(new InputStreamReader(diStream,Charset.forName("UTF-8")));
	        String docLine;	 
	        boolean columnHeaders = true;
	        
	        DatabaseUtils databaseUtils = new DatabaseUtils();   	
	        databaseUtils.openConnection();	                	        
	        
	        int editId = 0;
	        String revisionClass = "";
	        int annotators = 0;
	        int totalAnnotators = 0;
	        int oldRevisionId = 0;
	        int newRevisionId = 0;
	        
	        System.out.println("-> Loading annotations...");
	        while ((docLine = docReader.readLine()) != null) 
	        {
	        	if (!columnHeaders || config.getPanCorpus().equals("test")) {	// ground-truth.txt without headers in test corpus
	        			        			        
			        switch (config.getPanCorpus()) {
			        case "all" : case "training" :
			        	
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

				        editId = Integer.parseInt(lineItems[0]);
				        revisionClass = extractText(lineItems[1]);
				        annotators = Integer.parseInt(lineItems[2]);
				        totalAnnotators = Integer.parseInt(lineItems[3]);
				        oldRevisionId = 0;
				        newRevisionId = 0;
			        	break;
			        case "test" :
			        	
			        	String lineItems2[] = docLine.split(" ");
			        	
				        editId = 0;
				        revisionClass = lineItems2[2];
				        annotators = 0;
				        totalAnnotators = 0;
				        oldRevisionId = Integer.parseInt(lineItems2[0]);
				        newRevisionId = Integer.parseInt(lineItems2[1]);
			        	break;
			        }

			        System.out.println(
			        		"editId:          "  + editId + "\n" +
			        		"revisionClass:   "  + revisionClass + "\n" +
			        		"annotators:      "  + annotators + "\n" +
			        		"totalAnnotators: "  + totalAnnotators + "\n" +
			        		"oldRevisionId:   "  + oldRevisionId + "\n" +
			        		"newRevisionId:   "  + newRevisionId + "\n");

			        databaseUtils.updatePanEdit(
			        	editId, 
			        	revisionClass, 
			        	annotators, 
			        	totalAnnotators,
			        	oldRevisionId,
			        	newRevisionId);	        	
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
