package jrmr.vds.model.text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jrmr.vds.model.ext.lzw.LZW;

public class CompressibilityUtils {

	public double getCompressibilityRate(String inputText) {
		
		// last expression
//		double compressibilityRate = 0.0;
		// Götze
		double compressibilityRate = 0.5;
		
	    LZW lzw = new LZW();
	    InputStream is = new ByteArrayInputStream(inputText.getBytes());
		OutputStream os = new ByteArrayOutputStream();
				
		try {
			lzw.compress(is, os);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (inputText.length() != 0) {
			// last expression used
//			compressibilityRate = (double)os.toString().length() / (double)inputText.length();
			
			// Götze (1 = max compression)
			compressibilityRate = (double)inputText.length() / ((double)os.toString().length() + (double)inputText.length());			
		}
		
		try {
			is.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return compressibilityRate;
	}
	
}
