package jrmr.vds.model.features;

public class NewRelatedFrequency extends BaseFrequency {
	public double calculate(String insertedWords) {	
		return super.calculate(insertedWords, "model//inserted_words_diff_related.txt", false);
	}
}
