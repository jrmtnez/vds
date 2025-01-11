package jrmr.vds.model.text.graph.impl;

public class Edge
{
    private final int edgeId;
    private String edgeLabel;
    private double weight;
    

    public String getEdgeLabel() {
		return edgeLabel;
	}

	public void setEdgeLabel(String edgeLabel) {
		this.edgeLabel = edgeLabel;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Edge(int edgeId) {
        this.edgeId = edgeId;
    }

    public int getEdgeId() {
        return edgeId;
    }
	
    public String toString() {
		return String.valueOf(weight);
	}
}
