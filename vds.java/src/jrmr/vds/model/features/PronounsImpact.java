package jrmr.vds.model.features;

public class PronounsImpact extends BaseImpact {
	public double calculate(String oldTokenizedRevisionText, String newTokenizedRevisionText) {	
		return super.calculate(oldTokenizedRevisionText, newTokenizedRevisionText, 
			"config//pronouns.txt", false);
	}
}
