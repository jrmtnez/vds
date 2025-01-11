package jrmr.vds.model.features;

public class SexImpact extends BaseImpact {
	public double calculate(String oldTokenizedRevisionText, String newTokenizedRevisionText) {	
		return super.calculate(oldTokenizedRevisionText, newTokenizedRevisionText, 
			"config//sex.txt", false);
	}
}
