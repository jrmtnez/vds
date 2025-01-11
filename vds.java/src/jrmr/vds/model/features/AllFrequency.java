package jrmr.vds.model.features;

public class AllFrequency extends BaseFrequency {
	private int totalCount;
	private int wordCount;
	
	public double calculate(String insertedWords) {
		@SuppressWarnings("unused")
		double frequency;
				
		frequency = super.calculate(insertedWords, "config//vulgarism.txt", false);
		totalCount = super.getTotalCount();
		wordCount = super.getWordCount();
		
		frequency = super.calculate(insertedWords, "config//pronouns.txt", false);
		wordCount = wordCount + super.getWordCount();
		
		frequency = super.calculate(insertedWords, "config//bias.txt", false);
		wordCount = wordCount + super.getWordCount();
		
		frequency = super.calculate(insertedWords, "config//sex.txt", false);
		wordCount = wordCount + super.getWordCount();
		
		frequency = super.calculate(insertedWords, "config//bad.txt", false);
		wordCount = wordCount + super.getWordCount();
		
		return super.frequency(wordCount, totalCount);

	}
}
