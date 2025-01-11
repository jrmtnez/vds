package jrmr.vds.model.text.graph.impl;

public class Vertex {
    private final int id;
    private String label;
    private int degree;
    private double pageRank;
    private double eigenvectorCentrality;
    private double authority;
    private double betweennessCentrality;
	
	public Vertex(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}	
	
    public double getPageRank() {
		return pageRank;
	}

	public void setPageRank(double pageRank) {
		this.pageRank = pageRank;
	}
    
    public double getEigenvectorCentrality() {
		return eigenvectorCentrality;
	}

	public void setEigenvectorCentrality(double eigenvectorCentrality) {
		this.eigenvectorCentrality = eigenvectorCentrality;
	}

	public double getAuthority() {
		return authority;
	}

	public void setAuthority(double authority) {
		this.authority = authority;
	}
		
	public double getBetweennessCentrality() {
		return betweennessCentrality;
	}

	public void setBetweennessCentrality(double betweennessCentrality) {
		this.betweennessCentrality = betweennessCentrality;
	}

	public String toString() {
		return label;
	}
}
