package jrmr.vds.model.text;

public class MiscTextUtils {
	
	public static boolean isNumeric(String str)	{
	  return str.matches("-?\\d+(\\.\\d+)?");
	}
	
	
	public static boolean isAlphabetical(String str)	{
		  return str.matches("[a-zA-Z]*");
	}

}
