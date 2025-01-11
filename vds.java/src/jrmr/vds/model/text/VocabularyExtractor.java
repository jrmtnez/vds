package jrmr.vds.model.text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jrmr.vds.model.db.DatabaseUtils;
import jrmr.vds.model.io.FileUtils;

public class VocabularyExtractor {
	
	private int textLength;
	
	public VocabularyExtractor() {
		
	}
	
	public int getTextLength() {
		return textLength;
	}

	public HashMap<Character,Integer> getDifferentCharsList(String inputText) {
		
		HashMap<Character,Integer> differentCharsList = new HashMap<Character,Integer>();
		
		for (int i = 0; i < inputText.length(); i++) {
			Character c  = inputText.charAt(i);
			if (differentCharsList.containsKey(c))  {	    		    		
				differentCharsList.put(c,differentCharsList.get(c) + 1);
			} else {
				differentCharsList.put(c,1);
			}	    	
		}
		
		return differentCharsList;
	}
	
	// --- case sensitive vocabulary extractors ---
	// --- used only in base language features ---
	
	public HashMap<String,Integer> getVocabularyCaseSensitive(String inputText) {
		return getVocabularyCaseSensitive(inputText, 0, false);
	}
	
	public HashMap<String,Integer> getVocabularyCaseSensitive(String inputText, int minLength, boolean withoutNumbers) {
		
		HashMap<String,Integer> v = new HashMap<String,Integer>();				
		String[] tokenizedText = inputText.split("[ \n\r]"); // prevents the carriage return unifies two tokens		
		textLength = tokenizedText.length;			
		
		for (int i = 0; i < tokenizedText.length; i++) {
			String token = tokenizedText[i];
			boolean skip = (!MiscTextUtils.isAlphabetical(token) && withoutNumbers);
			
			if (!token.equals("") && (token.length() >= minLength) && !skip) {
				if (v.containsKey(token)) {	
					v.put(token, v.get(token).intValue() + 1);		
				} else {
					v.put(token, 1);
				}	
			}
		}				
		return v;
	}
	
	public HashMap<String,Integer> getVocabularyCaseSensitive(String inputText, String stopWordsList, int minLength, boolean withoutNumbers) {
		
		FileUtils fileutils = new  FileUtils();
		HashMap<String,Integer> stopWords = fileutils.file2Map(stopWordsList);
		
		HashMap<String,Integer> v = new HashMap<String,Integer>();		
		String[] tokenizedText = inputText.split("[ \n\r]"); // prevents the carriage return unifies two tokens
		textLength = tokenizedText.length;		
		
		for (int i = 0; i < tokenizedText.length; i++) {
			String token = tokenizedText[i];
			boolean skip = (!MiscTextUtils.isAlphabetical(token) && withoutNumbers);
			
			if (!token.equals("") && !stopWords.containsKey(token) && (token.length() >= minLength) && !skip) {				
				if (v.containsKey(tokenizedText[i])) {	
					v.put(token, v.get(token).intValue() + 1);		
				} else {
					v.put(token, 1);
				}	
			}
		}				
		return v;
	}
	
	// --- non case sensitive vocabulary extractors ---
	
	public HashMap<String,Integer> getVocabularyFromField(String sqlStatement) {					
		return getVocabularyFromField(sqlStatement, 0, false);
	}

	public HashMap<String,Integer> getVocabularyFromField(String sqlStatement, int minLength, boolean withoutNumbers) {	
			
		HashMap<String,Integer> v = new HashMap<String,Integer>();				
		textLength = 0;
		
		DatabaseUtils databaseUtils = new DatabaseUtils();		
		Connection con = databaseUtils.openConnection2();
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sqlStatement);
			rs = pst.executeQuery();

			while (rs.next()) {							
				
				String inputText = rs.getString(1);				
				String[] tokenizedText = inputText.split("[ \n\r]"); // prevents the carriage return unifies two tokens								
				textLength = textLength + tokenizedText.length;
				
				for (int i = 0; i < tokenizedText.length; i++) {					

					String token = tokenizedText[i].toLowerCase();						
					boolean skip = (!MiscTextUtils.isAlphabetical(token) && withoutNumbers);										
					if (!token.equals("") && (token.length() >= minLength) && !skip) {
						if (v.containsKey(token)) {	
							v.put(token, v.get(token).intValue() + 1);		
						} else {
							v.put(token, 1);
						}	
					}
				}
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				
				if (con != null) {
					con.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
		
		return v;
	}
	
	public HashMap<String,Integer> getVocabularyFromField(String sqlStatement, String stopWordsList, int minLength, boolean withoutNumbers) {	
		
		FileUtils fileutils = new  FileUtils();
		HashMap<String,Integer> stopWords = fileutils.file2Map(stopWordsList);
		
		HashMap<String,Integer> v = new HashMap<String,Integer>();				
		textLength = 0;
		
		DatabaseUtils databaseUtils = new DatabaseUtils();		
		Connection con = databaseUtils.openConnection2();
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sqlStatement);
			rs = pst.executeQuery();

			while (rs.next()) {							
				
				String inputText = rs.getString(1);				
				String[] tokenizedText = inputText.split("[ \n\r]"); // prevents the carriage return unifies two tokens				
				
				textLength = textLength + tokenizedText.length;
				
				for (int i = 0; i < tokenizedText.length; i++) {
					String token = tokenizedText[i].toLowerCase();
					boolean skip = (!MiscTextUtils.isAlphabetical(token) && withoutNumbers);					
					if (!token.equals("") && !stopWords.containsKey(token) && (token.length() >= minLength) && !skip) {
						if (v.containsKey(token)) {	
							v.put(token,v.get(token).intValue() + 1);		
						} else {
							v.put(token, 1);
						}	
					}
				}
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				
				if (con != null) {
					con.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
		
		return v;
	}
	
	public HashMap<String,Integer> getVocabularyFromRevisionText(String tableName) {	
		
		String sqlStatement = "SELECT revision_id FROM " + tableName;
		
		HashMap<String,Integer> v = new HashMap<String,Integer>();				
		textLength = 0;
		
		DatabaseUtils databaseUtils = new DatabaseUtils();		
		Connection con = databaseUtils.openConnection2();
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			pst = con.prepareStatement(sqlStatement);
			rs = pst.executeQuery();
			
			databaseUtils.openConnection();

			while (rs.next()) {							
				
				int revisionId = rs.getInt(1);
				
				String inputText = databaseUtils.getRevisionText(tableName, revisionId);				
				
				String[] tokenizedText = inputText.split("[ \n\r]"); // prevents the carriage return unifies two tokens				
				
				textLength = textLength + tokenizedText.length;
				
				for (int i = 0; i < tokenizedText.length; i++) {
					String token = tokenizedText[i].toLowerCase();
					if (!token.equals("")) {				
						if (v.containsKey(token)) {	
							v.put(token,v.get(token).intValue() + 1);		
						} else {
							v.put(token, 1);
						}	
					}
				}
			}
			
			databaseUtils.closeConnection();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
		
		return v;
	}	
	
}
