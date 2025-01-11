package jrmr.vds.model.features;

public class NewDegreeImpact extends BaseImpact {
	public double calculate(String oldTokenizedRevisionText, String newTokenizedRevisionText) {	
		return super.calculate(oldTokenizedRevisionText, newTokenizedRevisionText, 
				"model//coocurrence_vandalism_degree.txt", false);
	}
}