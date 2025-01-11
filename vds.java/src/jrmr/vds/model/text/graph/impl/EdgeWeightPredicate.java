package jrmr.vds.model.text.graph.impl;

import org.apache.commons.collections15.Predicate;

@SuppressWarnings("rawtypes")
public class EdgeWeightPredicate implements Predicate {
	
	private double value;
	private String condition;

	public EdgeWeightPredicate() {
		super();
	}

	public EdgeWeightPredicate(double value, String condition) {
		this();
		this.value = value;
		this.condition = condition;
	}
	
	public boolean evaluate(Object o) {
		
		double weight = (double)(((Edge)o).getWeight());
		
		switch (condition) {
			case "=" : 
				return (weight == value);
			case "<" : 
				return (weight < value);
			case ">" : 
				return (weight > value);
			case "<=" : 
				return (weight <= value);
			case ">=" : 
				return (weight >= value);
			case "<>" : 
				return (weight != value);			
		}
		
		return false;
	}	
}
