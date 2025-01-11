package jrmr.vds.model.features;

public class PronounsFrequency extends BaseFrequency {
	public double calculate(String insertedWords) {	
		return super.calculate(insertedWords, "config//pronouns.txt", false);
	}
}
