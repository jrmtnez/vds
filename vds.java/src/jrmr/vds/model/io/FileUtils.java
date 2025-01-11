package jrmr.vds.model.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import jrmr.vds.model.text.MiscTextUtils;

public class FileUtils {
	
	public String file2String(String inputFile) {
		
		String outputText = "";
		
		try {
			
			FileInputStream fiStream;
			fiStream = new FileInputStream(inputFile);
			DataInputStream diStream = new DataInputStream(fiStream);		
			BufferedReader docReader = new BufferedReader(new InputStreamReader(diStream,Charset.forName("UTF-8")));
	        String docLine;
	       	        
	        while ((docLine = docReader.readLine()) != null) 
	        {
	        	outputText = outputText + docLine + "\r\n";
	        }
	        
	        docReader.close();
	        diStream.close();
	        fiStream.close();	        
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return outputText;
	}	
	
	public void string2File(String inputText, String outputFile) {
	 	try 
		{
			OutputStream outputStream;
			outputStream = new FileOutputStream(outputFile);
			Writer outputWriter = new OutputStreamWriter(outputStream,Charset.forName("UTF-8"));
			outputWriter = new BufferedWriter(outputWriter);
			outputWriter.write(inputText);		
			outputWriter.flush();
			outputWriter.close();		
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}		
	}		
	
	public int maxLineLength(String inputFile) {
		
		int maxLengt = 0;
		
		try {
			
			FileInputStream fiStream;
			fiStream = new FileInputStream(inputFile);
			DataInputStream diStream = new DataInputStream(fiStream);		
			BufferedReader docReader = new BufferedReader(new InputStreamReader(diStream,Charset.forName("UTF-8")));
	        String docLine;
	       	        
	        while ((docLine = docReader.readLine()) != null) 
	        {
	        	if (docLine.length() > maxLengt) {
	        		maxLengt = docLine.length();
	        	}
	        }
	        
	        docReader.close();
	        diStream.close();
	        fiStream.close();	        
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return maxLengt;
	}	
	
	public void map2File(HashMap<String,Integer> map, String outputFile, int minOcurrence, int minLength, boolean insertCounts) {
	 	try 
		{
			OutputStream outputStream;
			outputStream = new FileOutputStream(outputFile);
			Writer outputWriter = new OutputStreamWriter(outputStream,Charset.forName("UTF-8"));
			outputWriter = new BufferedWriter(outputWriter);

			for (Map.Entry<String, Integer> entry : map.entrySet()) {				
				if (entry.getValue() >= minOcurrence && entry.getKey().length() >= minLength)
					if (insertCounts) {
						outputWriter.write(entry.getKey() + ";" + entry.getValue() + "\n");						
					} else {
						outputWriter.write(entry.getKey() + "\n");							
					}
			}
					
			outputWriter.flush();
			outputWriter.close();		
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}		
	}		
	
	public HashMap<String,Integer> file2Map (String inputFile) {
		
		HashMap<String,Integer> map = new HashMap<String,Integer>();
	
		try 
		{
			String term;
			InputStream inputStream;
			BufferedReader bufferedReader;
			inputStream = new FileInputStream(inputFile);
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
		
			while ((term = bufferedReader.readLine()) != null) {				
				if (map.containsKey(term)) {	
					map.put(term,map.get(term).intValue() + 1);		
				} else {
					map.put(term, 1);
				}	
			}
			bufferedReader.close();		
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}	
		
		return map;
	}
	
	public HashMap<Integer,Integer> file2MapInt (String inputFile) {
		
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
	
		try 
		{
			String line;
			InputStream inputStream;
			BufferedReader bufferedReader;
			inputStream = new FileInputStream(inputFile);
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
		
			while ((line = bufferedReader.readLine()) != null) {	
				int key = Integer.parseInt(line.split(";")[0]);
				int value = Integer.parseInt(line.split(";")[1]);				
				if (!map.containsKey(key)) {	
					map.put(key, value);
				}	
			}
			bufferedReader.close();		
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}	
		
		return map;
	}
	
	public void map2FileWithFilter(HashMap<String,Integer> map,String outputFile, int minOcurrence, int minLength, boolean insertCounts) {
	 	try 
		{
			OutputStream outputStream;
			outputStream = new FileOutputStream(outputFile);
			Writer outputWriter = new OutputStreamWriter(outputStream,Charset.forName("UTF-8"));
			outputWriter = new BufferedWriter(outputWriter);
			
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				
				if (entry.getValue() >= minOcurrence && entry.getKey().length() >= minLength &&	!MiscTextUtils.isNumeric(entry.getKey()))
					if (insertCounts) {
						outputWriter.write(entry.getKey() + ";" + entry.getValue() + "\n");						
					} else {
						outputWriter.write(entry.getKey() + "\n");						
					}
			}
					
			outputWriter.flush();
			outputWriter.close();		
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}		
	}	
	
	public void copyFile(String fromFile, String toFile) {

		Path fromPath = Paths.get(fromFile);
		Path toPath = Paths.get(toFile);

		CopyOption[] options = new CopyOption[] {
			StandardCopyOption.REPLACE_EXISTING,
			StandardCopyOption.COPY_ATTRIBUTES
		};

		try {
			Files.copy(fromPath, toPath, options);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
