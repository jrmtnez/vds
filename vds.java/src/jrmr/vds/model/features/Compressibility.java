package jrmr.vds.model.features;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jrmr.vds.model.ext.lzw.LZW;

public class Compressibility extends BaseFeature {
	public double calculate(String insertedText) {
		double compressibilityRate = 0.5;
		
	    LZW lzw = new LZW();
	    InputStream is = new ByteArrayInputStream(insertedText.getBytes());
		OutputStream os = new ByteArrayOutputStream();
				
		try {
			lzw.compress(is, os);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (insertedText.length() != 0) {
			compressibilityRate = (double) insertedText.length() / 
					((double)os.toString().length() + (double) insertedText.length());			
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
