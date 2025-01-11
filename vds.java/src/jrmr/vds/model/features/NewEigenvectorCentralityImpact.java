package jrmr.vds.model.features;

public class NewEigenvectorCentralityImpact extends BaseImpact {
	public double calculate(String oldTokenizedRevisionText, String newTokenizedRevisionText) {	
		return super.calculate(oldTokenizedRevisionText, newTokenizedRevisionText, 
				"model//coocurrence_vandalism_eigenvector.txt", false);		
	}
}