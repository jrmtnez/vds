package jrmr.vds.model.features;

public class NewRelatedImpact extends BaseImpact {
	public double calculate(String oldTokenizedRevisionText, String newTokenizedRevisionText) {	
		return super.calculate(oldTokenizedRevisionText, newTokenizedRevisionText, 
			"model//inserted_words_diff_related.txt", false);
	}
}
