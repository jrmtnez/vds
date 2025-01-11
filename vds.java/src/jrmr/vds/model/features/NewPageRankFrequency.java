package jrmr.vds.model.features;

public class NewPageRankFrequency extends BaseFrequency {
	public double calculate(String insertedWords) {	
		return super.calculate(insertedWords, "model//coocurrence_vandalism_pagerank.txt", false);
	}
}
