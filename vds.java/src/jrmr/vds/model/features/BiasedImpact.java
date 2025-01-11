package jrmr.vds.model.features;

public class BiasedImpact extends BaseImpact {
	public double calculate(String oldTokenizedRevisionText, String newTokenizedRevisionText) {	
		return super.calculate(oldTokenizedRevisionText, newTokenizedRevisionText, 
			"config//bias.txt", false);
	}
}
