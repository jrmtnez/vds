package jrmr.vds.model.features;

import jrmr.vds.model.text.EntropyUtils;

public class CharacterDistribution extends BaseFeature {
	public double calculate(String oldRevisionText,String insertedText) {
		EntropyUtils entropyUtils = new EntropyUtils();
		return entropyUtils.getKLDistance(oldRevisionText, insertedText);
	}	
}
