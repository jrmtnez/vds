package jrmr.vds.model.features;

import java.util.HashMap;

import jrmr.vds.model.text.EntropyUtils;

public class CharacterDiversity extends BaseFeature {	
	public double calculate(String insertedText) {
		
		EntropyUtils entropyUtils = new EntropyUtils();		
		double characterDiversity = 0.0;				
		HashMap<Character,Integer> differentCharsList = 
				entropyUtils.getDifferentCharsList(new HashMap<Character,Integer>(),insertedText,false);		
		int differentChars = differentCharsList.size();		
		int totalChars = entropyUtils.getDifferentCharsListSize(differentCharsList);
		
		if (differentChars > 0) {
			characterDiversity = Math.pow(totalChars, (double) 1 / (double)differentChars);			
		}		
		return characterDiversity;		
	}	
}
