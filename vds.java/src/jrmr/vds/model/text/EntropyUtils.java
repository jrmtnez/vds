package jrmr.vds.model.text;

import java.util.HashMap;
import java.util.Map;

public class EntropyUtils {
	
	public HashMap<Character,Integer> getDifferentCharsList(HashMap<Character,Integer> differentCharsList, String inputText, boolean notCount) {
				
		for (int i = 0; i < inputText.length(); i++) {
			
			Character c  = inputText.charAt(i);
			
			if (!Character.isWhitespace(c)) {
				if (differentCharsList.containsKey(c))  {
					if (!notCount) {
						differentCharsList.put(c,differentCharsList.get(c) + 1);
					}
				} else {
					if (!notCount) {
						differentCharsList.put(c,1);
					} else {
						differentCharsList.put(c,0);
					}
				}
			}
		}
		
		return differentCharsList;
	}
	
	public HashMap<Character,Integer> diffChars(
			HashMap<Character,Integer> differentCharsList1, HashMap<Character,Integer> differentCharsList2) {
		
		HashMap<Character,Integer> diffChars = new HashMap<Character,Integer>();
		
		for (Map.Entry<Character, Integer> entry : differentCharsList2.entrySet()) {
			if (!differentCharsList1.containsKey(entry.getKey())) {
				diffChars.put(entry.getKey(), entry.getValue());
			}
		}
		
		return diffChars;
	}
	
	public int getDifferentCharsListSize(HashMap<Character,Integer> differentCharsList) {
		int size = 0;
		
		for (Map.Entry<Character, Integer> entry : differentCharsList.entrySet()) {
			size = size + entry.getValue();
		}
		
		return size;
	}

	public double getTextEntropy(String inputText) {
		
		HashMap<Character,Integer> differentCharsList = getDifferentCharsList(new HashMap<Character,Integer>(),inputText,false);

		return getTextEntropy(differentCharsList,inputText);
	}
	
	public double getTextEntropy(HashMap<Character,Integer> differentCharsList,String inputText) {
		double entropy = 0.0;

		int n = differentCharsList.size();
		for (Map.Entry<Character, Integer> entry : differentCharsList.entrySet()) {
			double p = (double) entry.getValue() / n;
			entropy = entropy + p * Math.log(p) / Math.log(2);
		}

		return -entropy;
	}

	
	public double getKLDivergence(String inputText1,String inputText2) {
		double klDivergence = 0.0;
		
		HashMap<Character,Integer> differentCharsList1 = getDifferentCharsList(new HashMap<Character,Integer>(),inputText1 + inputText2,true);
		HashMap<Character,Integer> differentCharsList2 = getDifferentCharsList(new HashMap<Character,Integer>(),inputText1 + inputText2,true);		
		
		int n = differentCharsList1.size() + differentCharsList2.size();
		
		if  (inputText1 != null) {

			differentCharsList1 = getDifferentCharsList(differentCharsList1,inputText1,false);			
			differentCharsList2 = getDifferentCharsList(differentCharsList2,inputText2,false);
			
			for (Map.Entry<Character, Integer> entry : differentCharsList1.entrySet()) {

				Character c = entry.getKey();
				
				System.out.print(c + " " + (double) differentCharsList1.get(c) / n + "; ");
								
				double p = (double) differentCharsList1.get(c) / n;
				double q = (double) differentCharsList2.get(c) / n;

				if (q == 0) {
					klDivergence = Double.POSITIVE_INFINITY;
					break;
				} else {
					if (p != 0) {
						klDivergence = klDivergence + p * Math.log(p / q) / Math.log(2);
					}				
				}
			}
		}		
		return klDivergence;
	}
	
	public double getKLDistance(String inputText1,String inputText2) {
		double klDivergence = 0.0;
		
		HashMap<Character,Integer> differentCharsList1 = getDifferentCharsList(new HashMap<Character,Integer>(),inputText1,false);
		HashMap<Character,Integer> differentCharsList2 = getDifferentCharsList(new HashMap<Character,Integer>(),inputText2,false);		
		
		int differentCharsList1Size = getDifferentCharsListSize(differentCharsList1);
		int differentCharsList2Size = getDifferentCharsListSize(differentCharsList2);

		double sum1 = 0.0;
		for (Map.Entry<Character, Integer> entry : differentCharsList1.entrySet()) {	
			double p = (double) entry.getValue() / (double) differentCharsList1Size;
			sum1 = sum1 + p;			
		}
		
		double sum2 = 0.0;
		for (Map.Entry<Character, Integer> entry : differentCharsList2.entrySet()) {
			double p = (double) entry.getValue() / (double) differentCharsList2Size;			
			sum2 = sum2 + p;
		}
		
		double epsilon = Double.MAX_VALUE;
		for (Map.Entry<Character, Integer> entry : differentCharsList1.entrySet()) {
			double p = (double) entry.getValue() / (double) differentCharsList1Size;			
			if (epsilon > (p / sum1)) {
				epsilon = (p / sum1);
			}
		}		
		for (Map.Entry<Character, Integer> entry : differentCharsList2.entrySet()) {
			double p = (double) entry.getValue() / (double) differentCharsList2Size;
			if (epsilon > (p / sum2)) {
				epsilon = (p / sum2);
			}
		}
	
		epsilon = epsilon * 0.001;
		HashMap<Character,Integer> diffChars = diffChars(differentCharsList1, differentCharsList2);		
		double gamma = 1 - diffChars.size() * epsilon;				
		
		for (Map.Entry<Character, Integer> entry : differentCharsList1.entrySet()) {			
			char c = entry.getKey();
			double p1 = ((double) entry.getValue() / (double) differentCharsList1Size) / sum1;						
			double p2 = 0.0;			
			
			if (differentCharsList2.containsKey(c)) {
				p2 = gamma * ((double) differentCharsList2.get(c) / (double) differentCharsList2Size) / sum1;
			} else {
				p2 = epsilon;
			}
			
			klDivergence = klDivergence + ((p1 - p2) * Math.log(p1 / p2));			
		}
		
		return klDivergence;
	}	
	
	public double getCharacterDiversity(String inputText) {
	
		double characterDiversity = 0.0;		
		
		HashMap<Character,Integer> differentCharsList = 
			getDifferentCharsList(new HashMap<Character,Integer>(),inputText,false);
		int differentChars = differentCharsList.size();
		int totalChars = getDifferentCharsListSize(differentCharsList);
		
		if (differentChars > 0) {
			characterDiversity = Math.pow(totalChars, (double) 1 / (double)differentChars);			
		}		

		return characterDiversity;
	}
}
