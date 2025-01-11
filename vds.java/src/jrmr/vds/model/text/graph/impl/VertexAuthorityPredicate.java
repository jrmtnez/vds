package jrmr.vds.model.text.graph.impl;

import org.apache.commons.collections15.Predicate;

@SuppressWarnings("rawtypes")
public class VertexAuthorityPredicate implements Predicate {
	
	private double value;
	private String condition;

	public VertexAuthorityPredicate() {
		super();
	}
	
	public VertexAuthorityPredicate(double value, String condition) {
		this();
		this.value = value;
		this.condition = condition;
	}

public boolean evaluate(Object o) {
		
		double authority = (double)(((Vertex)o).getAuthority());
		
		switch (condition) {
			case "=" : 
				return (authority == value);
			case "<" : 
				return (authority < value);
			case ">" : 
				return (authority > value);
			case "<=" : 
				return (authority <= value);
			case ">=" : 
				return (authority >= value);
			case "<>" : 
				return (authority != value);			
		}
		
		return false;
	}	
}
