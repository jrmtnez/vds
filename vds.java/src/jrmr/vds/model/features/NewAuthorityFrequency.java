package jrmr.vds.model.features;

public class NewAuthorityFrequency extends BaseFrequency {
	public double calculate(String insertedWords) {	
		return super.calculate(insertedWords, "model//coocurrence_vandalism_authority.txt", false);
	}
}
