package jrmr.vds.model.features;

public class BadFrequency extends BaseFrequency {
	public double calculate(String insertedWords) {	
		return super.calculate(insertedWords, "config//bad.txt", false);
	}
}
