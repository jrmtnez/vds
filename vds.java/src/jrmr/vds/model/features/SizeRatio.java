package jrmr.vds.model.features;

public class SizeRatio extends BaseFeature {
	public double calculate(String oldRevisionText, String newRevisionText) {			
		return (1.0 + newRevisionText.length()) / (1.0 + oldRevisionText.length());	
	}
}
