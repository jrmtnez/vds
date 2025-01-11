package jrmr.vds.model.features;

public class AllImpact extends BaseImpact {
	private int wordOldCount;
	private int wordNewCount;	

	public double calculate(String oldTokenizedRevisionText, String newTokenizedRevisionText) {
		@SuppressWarnings("unused")
		double impact;
		
		impact = super.calculate(oldTokenizedRevisionText, newTokenizedRevisionText, 
			"config//vulgarism.txt", false);
		wordOldCount = super.getWordOldCount();
		wordNewCount = super.getWordNewCount();
		
		impact = super.calculate(oldTokenizedRevisionText, newTokenizedRevisionText, 
				"config//pronouns.txt", false);
		wordOldCount = wordOldCount  + super.getWordOldCount();
		wordNewCount = wordNewCount + super.getWordNewCount();
		
		impact = super.calculate(oldTokenizedRevisionText, newTokenizedRevisionText, 
				"config//bias.txt", false);
		wordOldCount = wordOldCount  + super.getWordOldCount();
		wordNewCount = wordNewCount + super.getWordNewCount();

		impact = super.calculate(oldTokenizedRevisionText, newTokenizedRevisionText, 
				"config//sex.txt", false);
		wordOldCount = wordOldCount  + super.getWordOldCount();
		wordNewCount = wordNewCount + super.getWordNewCount();
		
		impact = super.calculate(oldTokenizedRevisionText, newTokenizedRevisionText, 
				"config//bad.txt", false);
		wordOldCount = wordOldCount  + super.getWordOldCount();
		wordNewCount = wordNewCount + super.getWordNewCount();
		
		return super.impact(wordOldCount, wordNewCount);
	}
}
