package jrmr.vds.model.features;

public class SizeIncrement extends BaseFeature {
	public int calculate(String oldRevisionText, String newRevisionText) {			
		return newRevisionText.length() - oldRevisionText.length();		
	}
}
