package jrmr.vds.model.features;

public class LongestCharacterSequence extends BaseFeature {
	public int calculate(String insertedText) {			
		Character prev = null;		
		int subseqLen = 0;
		int longestCharSeq = 0;		
		for (int i = 0; i < insertedText.length(); i++) {			
			Character c  = insertedText.charAt(i);			
			if (c == prev) {
				subseqLen++;
			} else {
				subseqLen = 1;
			}			
			if (longestCharSeq < subseqLen) {
				longestCharSeq = subseqLen;
			}							
			prev = c;
		}		
		return longestCharSeq;
	}
}
