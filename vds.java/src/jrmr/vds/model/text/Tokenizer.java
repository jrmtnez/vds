package jrmr.vds.model.text;

import jrmr.vds.model.io.FileUtils;

public class Tokenizer { 	
	
	public Tokenizer() {
	
	}

	public String tokenize(String inputText) {
		
		String tokenizedText = "";		
		Character prev = null;
		String charTokens = ".,:;\"«»'|?!=()*";
		String char2Tokens = "[]{}";
		
		for (int i = 0; i < inputText.length(); i++) {		    
			
			Character c  = inputText.charAt(i);
			Character next = null;
			
			if (i < (inputText.length() - 1)) {				
				next  = inputText.charAt(i + 1);				
			}
						
			if (prev !=null) {
				
				if (charTokens.contains(c.toString()) && !Character.isSpaceChar(prev)) {
	    			tokenizedText = tokenizedText + " ";				
				}				

				if (char2Tokens.contains(c.toString()) && (c != prev) && !Character.isSpaceChar(prev)) {
	    			tokenizedText = tokenizedText + " ";
				}
			}
			
			tokenizedText = tokenizedText + c.toString();
			
			if (prev !=null) {
				if (charTokens.contains(c.toString()) && Character.isSpaceChar(prev)) {
	    			tokenizedText = tokenizedText + " ";
	    			c = ' ';
				}
			}
			
			if (next !=null) {
				if (char2Tokens.contains(c.toString()) && (c == prev) && !Character.isSpaceChar(next)) {
	    			tokenizedText = tokenizedText + " ";
	    			c = ' ';
				}
				
				if (char2Tokens.contains(c.toString()) && (c != prev) && (c != next) && !Character.isSpaceChar(next)) {
		    		tokenizedText = tokenizedText + " ";
	    			c = ' ';
				}
			}
		    
			prev = c;
		}
		
		return tokenizedText;
	}	
	
	public String tokenize(String inputText, String tokensFile) {
		
		FileUtils fileUtils = new FileUtils();
		int maxTokenLength = fileUtils.maxLineLength(tokensFile);
		int tokenLength = 0;
		
		String tokenizedText = "";
		
		WordList wikiTokensList = new WordList(tokensFile);	
					
		for (int i = 0; i < inputText.length(); i++) {
			
			if (maxTokenLength > (inputText.length() - i)) {
				maxTokenLength = inputText.length() - i;
			}
			
			String token = "";
			String separator1 = "";					
			String separator2 = "";
			boolean found = false;
			for (tokenLength = maxTokenLength; tokenLength > 0; tokenLength--) {
				
				token = inputText.substring(i, i + tokenLength);
				
				if (wikiTokensList.findTerm(token)) {

					i = i + tokenLength - 1;

					separator1 = "";					
					separator2 = "";
					
					if (tokenizedText.length() > 0) {
						Character c = tokenizedText.charAt(tokenizedText.length() - 1);
						if (!Character.isWhitespace(c)) {
							separator1 = " ";
						}
					}
					
					if (i < inputText.length()) {						
						Character c  = inputText.charAt(i + 1);						
						if (!Character.isWhitespace(c)) {
							separator2 = " ";
						}
					}
					
					tokenizedText = tokenizedText + separator1 + token + separator2;
					
					found = true;
					break;
				}			
			}		
			
			if (!found) {
			  tokenizedText = tokenizedText + token;
			}
		}
		
		return tokenizedText;
	}
}

