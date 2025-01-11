package jrmr.vds.model.features;

public class BiasedFrequency extends BaseFrequency {
	public double calculate(String insertedWords) {	
		return super.calculate(insertedWords, "config//bias.txt", false);
	}
}
