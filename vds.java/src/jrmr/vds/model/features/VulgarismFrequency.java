package jrmr.vds.model.features;

public class VulgarismFrequency extends BaseFrequency {
	public double calculate(String insertedWords) {	
		return super.calculate(insertedWords, "config//vulgarism.txt", false);
	}
}
