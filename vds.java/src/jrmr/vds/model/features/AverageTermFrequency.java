package jrmr.vds.model.features;

import jrmr.vds.model.text.DiffUtils;

public class AverageTermFrequency extends BaseFeature {
	public double calculate(String oldTokenizedRevisionText, String newTokenizedRevisionText) {
		DiffUtils diffUtils = new DiffUtils();	
		diffUtils.calcInsertedAndDeletedWords(oldTokenizedRevisionText.toLowerCase(), newTokenizedRevisionText.toLowerCase());
		return diffUtils.getAverageTermFrequency();	
	}
}
