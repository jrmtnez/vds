package jrmr.vds.model.text;

public class Wiki2PlainTextParser {

	Wiki2PlainTextParser() {		
	}
	
	public String parse(String inputText) {;
	
		inputText = inputText.replaceAll("\\{\\{listaref\\}\\}","");
		inputText = inputText.replaceAll("^\\|Imagen\\s\\=\\s","");
		inputText = inputText.replaceAll("^\\|Tamaño\\s\\=\\sframeless","");
			
		inputText = inputText.replaceAll("<[a-z]*>|</[a-z]*>", " ");
		inputText = inputText.replaceAll("http://[^\\s]*", " ");
		inputText = inputText.replaceAll("\\.\\.\\.", "");	
		inputText = inputText.replaceAll("([\\\"*'*\\Ó,;%¿?¡!(*)*\\[*\\]*{*}*<*>*«*»*\\.´*])","");
		inputText = inputText.replaceAll("([_*=*\\|*:*\\-*])"," ");
		inputText = inputText.replaceAll("(\\s)\\s","$1");
		inputText = inputText.replaceAll("^[\\s]*","");
		inputText = inputText.replaceAll("[\\s]*$","");
	
		return inputText;		
	}	
}
