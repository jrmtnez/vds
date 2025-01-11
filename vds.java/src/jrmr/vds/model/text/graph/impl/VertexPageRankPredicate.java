package jrmr.vds.model.text.graph.impl;

import org.apache.commons.collections15.Predicate;

@SuppressWarnings("rawtypes")
public class VertexPageRankPredicate implements Predicate {
	
	private double value;
	private String condition;

	public VertexPageRankPredicate() {
		super();
	}
	
	public VertexPageRankPredicate(double value, String condition) {
		this();
		this.value = value;
		this.condition = condition;
	}

public boolean evaluate(Object o) {
		
		double pageRank = (double)(((Vertex)o).getPageRank());
		
		switch (condition) {
			case "=" : 
				return (pageRank == value);
			case "<" : 
				return (pageRank < value);
			case ">" : 
				return (pageRank > value);
			case "<=" : 
				return (pageRank <= value);
			case ">=" : 
				return (pageRank >= value);
			case "<>" : 
				return (pageRank != value);			
		}
		
		return false;
	}	
}