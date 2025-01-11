package jrmr.vds.model.text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;

public class WordList {

	private HashMap<String,Integer> wordList;
	
	public WordList(String wordListFileName) {
		try 
		{
			wordList = new HashMap<String,Integer>();
			String term;
			InputStream inputStream;
			BufferedReader bufferedReader;
			inputStream = new FileInputStream(wordListFileName);
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
		
			while ((term = bufferedReader.readLine()) != null) {
				wordList.put(term, 1);
			}
			bufferedReader.close();		
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public boolean findTerm(String term)
	{
	    return wordList.containsKey(term);
	}
	
	public HashMap<String,Integer> getList() {
		return wordList;
	}
}
