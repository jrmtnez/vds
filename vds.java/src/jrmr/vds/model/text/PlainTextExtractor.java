package jrmr.vds.model.text;

public class PlainTextExtractor {

	public String extract(String inputText, String tokensFile) {
		
		WordList wikiTokensList = new WordList(tokensFile);	
		String[] tokenizedText = inputText.split("[ \n\r]"); // prevents the carriage return unifies two tokens
		String plainText = "";
		boolean first = true;
			
		for (int i = 0; i < tokenizedText.length; i++) {
			String token = tokenizedText[i];
			
			if (!token.equals("")) {
				if (!wikiTokensList.findTerm(token)) {
					if (first) {
//						-C001
						plainText = token.toLowerCase();
//						plainText = token;
//						+C001
					} else {
//						-C001
						plainText = plainText + " " + token.toLowerCase();						
//						plainText = plainText + " " + token;
//						+C001
					}
				}								
			}
			first = false;
		}			
		return plainText;
	}		
	
	public String extractWithLines(String inputText, String tokensFile, boolean separateSentences) {
		
		WordList wikiTokensList = new WordList(tokensFile);
		
		String plainText = "";		
		String[] textLines  = inputText.split("[\n\r]");
		boolean newLineInserted = false;

		for (int i = 0; i < textLines.length; i++) {
		
			String[] tokenizedText = textLines[i].split(" ");
			String token = "";
			
			for (int j = 0; j < tokenizedText.length; j++) {				
				token = tokenizedText[j];
			
				if (!token.equals("")) {
					if (!wikiTokensList.findTerm(token)) {
//						-C001
						plainText = plainText + " " + token.toLowerCase();	
//						plainText = plainText + " " + token;	
//						+C001
					}						
				}
				
				newLineInserted = false;
				if (separateSentences && isEndOfSencente(token)) {					
					plainText = plainText + "\n";
					newLineInserted = true;
				}
			}			
			
			if (!textLines[i].equals("") && !newLineInserted) {
				plainText = plainText + "\n";
			}
		}			
		
		return plainText;
	}		
	
	public boolean isEndOfSencente(String token) {
		return token.equals(".") || token.equals("?") || token.equals("!");
	}
}
