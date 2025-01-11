package jrmr.vds.model.features;

public class NewDegreeFrequency extends BaseFrequency {
	public double calculate(String insertedWords) {	
		return super.calculate(insertedWords, "model//coocurrence_vandalism_degree.txt", false);
	}
}
