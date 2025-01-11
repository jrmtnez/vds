package jrmr.vds.model.features;

public class VulgarismImpact extends BaseImpact {
	public double calculate(String oldTokenizedRevisionText, String newTokenizedRevisionText) {	
		return super.calculate(oldTokenizedRevisionText, newTokenizedRevisionText, 
			"config//vulgarism.txt", false);
	}
}
