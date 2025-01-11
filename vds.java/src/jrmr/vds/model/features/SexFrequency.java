package jrmr.vds.model.features;

public class SexFrequency extends BaseFrequency {
	public double calculate(String insertedWords) {	
		return super.calculate(insertedWords, "config//sex.txt", false);
	}
}
