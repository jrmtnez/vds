package jrmr.vds.model.features;

public class NewEigenvectorCentralityFrequency extends BaseFrequency {
	public double calculate(String insertedWords) {	
		return super.calculate(insertedWords, "model//coocurrence_vandalism_eigenvector.txt", false);		
	}
}
