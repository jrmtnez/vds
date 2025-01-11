package jrmr.vds.model.features;

public class CommentLength extends BaseFeature {	
	public int calculate(String comment) {		
		return comment.length();		
	}
}
