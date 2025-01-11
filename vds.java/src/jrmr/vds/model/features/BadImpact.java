package jrmr.vds.model.features;

public class BadImpact extends BaseImpact {
	public double calculate(String oldTokenizedRevisionText, String newTokenizedRevisionText) {	
		return super.calculate(oldTokenizedRevisionText, newTokenizedRevisionText, 
			"config//bad.txt", false);
	}
}
