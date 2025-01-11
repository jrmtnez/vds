package jrmr.vds.model.features;

public class NewPageRankImpact extends BaseImpact {
	public double calculate(String oldTokenizedRevisionText, String newTokenizedRevisionText) {	
		return super.calculate(oldTokenizedRevisionText, newTokenizedRevisionText, 
				"model//coocurrence_vandalism_pagerank.txt", false);
	}
}