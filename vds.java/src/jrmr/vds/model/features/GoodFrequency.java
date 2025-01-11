package jrmr.vds.model.features;

public class GoodFrequency extends BaseFrequency {
	public double calculate(String insertedWords) {	
		return super.calculate(insertedWords, "config//goodtokens.txt", true);
	}
}
