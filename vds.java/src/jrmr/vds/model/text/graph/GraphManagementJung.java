package jrmr.vds.model.text.graph;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.xml.sax.SAXException;

import jrmr.vds.model.io.FileUtils;
import jrmr.vds.model.text.graph.impl.Edge;
import jrmr.vds.model.text.graph.impl.EdgeWeightPredicate;
import jrmr.vds.model.text.graph.impl.Vertex;
import jrmr.vds.model.text.graph.impl.VertexFactory;
import jrmr.vds.model.text.graph.impl.EdgeFactory;
import edu.uci.ics.jung.algorithms.filters.EdgePredicateFilter;
import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.scoring.EigenvectorCentrality;
import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLReader;

public class GraphManagementJung {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void pageRankWordList(String fileName, Double minValue, int maxLength) {
    			
		System.out.println("Loading " + fileName + ".graphml ...");
		
		GraphMLReader<UndirectedGraph<Vertex, Edge>, Vertex, Edge> graphMLReader;		
		try {
			graphMLReader = new GraphMLReader<UndirectedGraph<Vertex, Edge>, Vertex, Edge>(new VertexFactory(), new EdgeFactory());
			final UndirectedGraph<Vertex, Edge> graph = new UndirectedSparseMultigraph<Vertex, Edge>();
			graphMLReader.load(fileName + ".graphml", graph);
		    System.out.println("Graph " + fileName + " loaded: " + graph.getVertexCount() + " vertex and " + graph.getEdgeCount() + " edges");
		    
			Map<String, GraphMLMetadata<Vertex>> vertexMetadata = graphMLReader.getVertexMetadata();
			Map<String, GraphMLMetadata<Edge>> edgeMetadata = graphMLReader.getEdgeMetadata();
			for (Vertex v : graph.getVertices()) {
				v.setLabel(vertexMetadata.get("label").transformer.transform(v));
			}

			for (Edge e : graph.getEdges())	{		
				e.setWeight(Double.parseDouble(edgeMetadata.get("weight").transformer.transform(e)));
			}
			
			Predicate edgeWeightPredicate = new EdgeWeightPredicate(1,">=");
			EdgePredicateFilter edgePredicateFilter = new EdgePredicateFilter(edgeWeightPredicate);
			Graph graph2 = edgePredicateFilter.transform(graph);
						
		    Transformer<Edge, Double> edgeWeightsTransformer = new Transformer<Edge,Double>() {
		    	public Double transform(Edge e) {
		    		return e.getWeight();
		    	}
		    };  		    
			
			System.out.println("Calculating PageRank");
		     
		    PageRank<Vertex, Edge> pageRank = new PageRank<Vertex, Edge>(graph2,edgeWeightsTransformer,0.15);
		    pageRank.setMaxIterations(20);
		    pageRank.evaluate();
		     	    
		    for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {
		    	v.setPageRank(pageRank.getVertexScore(v));
		    }
		    
	    	System.out.println("Exporting word list");
		    
			OutputStream outputStream;
			outputStream = new FileOutputStream(fileName + "_pagerank.txt");
			Writer outputWriter = new OutputStreamWriter(outputStream,Charset.forName("UTF-8"));
			outputWriter = new BufferedWriter(outputWriter);
			
			if (maxLength == 0) {
			    for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {		    		
		    		
		    		Double score = (Double) v.getPageRank();
		    		System.out.println(v.getId() + " " + v.getLabel() + " " + score);
					outputWriter.write(v.getLabel() + "\n");
		    	}
			} else {
				
				List <Vertex> verticesList = new ArrayList <Vertex> ();	
				
		    	for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {
		    		verticesList.add(v);
		    	}
		    					
			 	Comparator <Vertex> compareVertices = new Comparator<Vertex> () {
			 	    public int compare(Vertex a, Vertex b) {
			 	    	Double scoreb = (Double) b.getPageRank();
			 	    	Double scorea = (Double) a.getPageRank();
			 	        
			 	    	return Double.compare(scoreb, scorea);
			 	    };
			 	};
			 	  
			 	Collections.sort(verticesList, compareVertices);
			 	
			 	int lastElement = 0;
			 	for (Vertex v : verticesList) {
			 		lastElement++;
			 		if (lastElement <= maxLength) {
			    		Double score = (Double) v.getPageRank();
			    		System.out.println(v.getId() + " " + v.getLabel() + " " + score);
						outputWriter.write(v.getLabel() + "\n");			 			
			 		} else {
			 			break;
			 		}
			 	}
			}
			outputWriter.flush();
			outputWriter.close();
			
			if (fileName.contains("vandalism")) {
				FileUtils fileUtils = new FileUtils();
				fileUtils.copyFile(fileName + "_pagerank.txt", "model\\coocurrence_vandalism_pagerank.txt");
			}	    	
		    
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void degreeWordList(String fileName, int minValue, int maxLength) {
		System.out.println("Loading " + fileName + ".graphml ...");
		
		GraphMLReader<UndirectedGraph<Vertex, Edge>, Vertex, Edge> graphMLReader;		
		try {
			graphMLReader = new GraphMLReader<UndirectedGraph<Vertex, Edge>, Vertex, Edge>(new VertexFactory(), new EdgeFactory());
			final UndirectedGraph<Vertex, Edge> graph = new UndirectedSparseMultigraph<Vertex, Edge>();
			graphMLReader.load(fileName + ".graphml", graph);
		    System.out.println("Graph " + fileName + " loaded: " + graph.getVertexCount() + " vertex and " + graph.getEdgeCount() + " edges");
		    
			Map<String, GraphMLMetadata<Vertex>> vertexMetadata = graphMLReader.getVertexMetadata();
			Map<String, GraphMLMetadata<Edge>> edgeMetadata = graphMLReader.getEdgeMetadata();
			for (Vertex v : graph.getVertices()) {
				v.setLabel(vertexMetadata.get("label").transformer.transform(v));
			}

			for (Edge e : graph.getEdges())	{		
				e.setWeight(Double.parseDouble(edgeMetadata.get("weight").transformer.transform(e)));
			}
			
			Predicate edgeWeightPredicate = new EdgeWeightPredicate(1,">=");
			EdgePredicateFilter edgePredicateFilter = new EdgePredicateFilter(edgeWeightPredicate);
			Graph graph2 = edgePredicateFilter.transform(graph);
									
			System.out.println("Calculating Degree");
			
			DegreeScorer<Vertex> degreeScorer = new DegreeScorer<Vertex>(graph2);
		    for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {		    	
		    	v.setDegree(degreeScorer.getVertexScore(v));		   
		    }
		    
	    	System.out.println("Exporting word list");
		    
			OutputStream outputStream;
			outputStream = new FileOutputStream(fileName + "_degree.txt");
			Writer outputWriter = new OutputStreamWriter(outputStream,Charset.forName("UTF-8"));
			outputWriter = new BufferedWriter(outputWriter);
			
			if (maxLength == 0) {
			    for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {		    		
		    		
		    		int score = (int) v.getDegree();
		    		System.out.println(v.getId() + " " + v.getLabel() + " " + score);
					outputWriter.write(v.getLabel() + "\n");
		    	}
			} else {
				
				List <Vertex> verticesList = new ArrayList <Vertex> ();	
				
		    	for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {
		    		verticesList.add(v);
		    	}
		    					
			 	Comparator <Vertex> compareVertices = new Comparator<Vertex> () {
			 	    public int compare(Vertex a, Vertex b) {
			 	    	int scoreb = (int) b.getDegree();
			 	    	int scorea = (int) a.getDegree();
			 	        
			 	    	return Integer.compare(scoreb, scorea);
			 	    };
			 	};
			 	  
			 	Collections.sort(verticesList, compareVertices);
			 	
			 	int lastElement = 0;
			 	for (Vertex v : verticesList) {
			 		lastElement++;
			 		if (lastElement <= maxLength) {
			    		int score = (int) v.getDegree();
			    		System.out.println(v.getId() + " " + v.getLabel() + " " + score);
						outputWriter.write(v.getLabel() + "\n");			 			
			 		} else {
			 			break;
			 		}
			 	}
			}
			outputWriter.flush();
			outputWriter.close();
			
			if (fileName.contains("vandalism")) {
				FileUtils fileUtils = new FileUtils();
				fileUtils.copyFile(fileName + "_degree.txt", "model\\coocurrence_vandalism_degree.txt");
			}	    	
		    
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void eigenvectorCentralityWordList(String fileName, Double minValue, int maxLength) {
		System.out.println("Loading " + fileName + ".graphml ...");
		
		GraphMLReader<UndirectedGraph<Vertex, Edge>, Vertex, Edge> graphMLReader;		
		try {
			graphMLReader = new GraphMLReader<UndirectedGraph<Vertex, Edge>, Vertex, Edge>(new VertexFactory(), new EdgeFactory());
			final UndirectedGraph<Vertex, Edge> graph = new UndirectedSparseMultigraph<Vertex, Edge>();
			graphMLReader.load(fileName + ".graphml", graph);
		    System.out.println("Graph " + fileName + " loaded: " + graph.getVertexCount() + " vertex and " + graph.getEdgeCount() + " edges");
		    
			Map<String, GraphMLMetadata<Vertex>> vertexMetadata = graphMLReader.getVertexMetadata();
			Map<String, GraphMLMetadata<Edge>> edgeMetadata = graphMLReader.getEdgeMetadata();
			for (Vertex v : graph.getVertices()) {
				v.setLabel(vertexMetadata.get("label").transformer.transform(v));
			}

			for (Edge e : graph.getEdges())	{		
				e.setWeight(Double.parseDouble(edgeMetadata.get("weight").transformer.transform(e)));
			}
			
			Predicate edgeWeightPredicate = new EdgeWeightPredicate(1,">=");
			EdgePredicateFilter edgePredicateFilter = new EdgePredicateFilter(edgeWeightPredicate);
			Graph graph2 = edgePredicateFilter.transform(graph);
						
		    Transformer<Edge, Double> edgeWeightsTransformer = new Transformer<Edge,Double>() {
		    	public Double transform(Edge e) {
		    		return e.getWeight();
		    	}
		    };  		    
			
			System.out.println("Calculating Eigenvector Centrality");
		     
		    EigenvectorCentrality<Vertex, Edge> eigenvectorCentrality = new EigenvectorCentrality<Vertex, Edge>(graph2,edgeWeightsTransformer);
		    eigenvectorCentrality.setMaxIterations(20); 	    
		    
		    for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {
		    	v.setEigenvectorCentrality(eigenvectorCentrality.getVertexScore(v));
		    }
		    
	    	System.out.println("Exporting word list");
		    
			OutputStream outputStream;
			outputStream = new FileOutputStream(fileName + "_eigenvector.txt");
			Writer outputWriter = new OutputStreamWriter(outputStream,Charset.forName("UTF-8"));
			outputWriter = new BufferedWriter(outputWriter);
			
			if (maxLength == 0) {
			    for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {		    		
		    		
		    		Double score = (Double) v.getEigenvectorCentrality();
		    		System.out.println(v.getId() + " " + v.getLabel() + " " + score);
					outputWriter.write(v.getLabel() + "\n");
		    	}
			} else {
				
				List <Vertex> verticesList = new ArrayList <Vertex> ();	
				
		    	for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {
		    		verticesList.add(v);
		    	}
		    					
			 	Comparator <Vertex> compareVertices = new Comparator<Vertex> () {
			 	    public int compare(Vertex a, Vertex b) {
			 	    	Double scoreb = (Double) b.getEigenvectorCentrality();
			 	    	Double scorea = (Double) a.getEigenvectorCentrality();
			 	        
			 	    	return Double.compare(scoreb, scorea);
			 	    };
			 	};
			 	  
			 	Collections.sort(verticesList, compareVertices);
			 	
			 	int lastElement = 0;
			 	for (Vertex v : verticesList) {
			 		lastElement++;
			 		if (lastElement <= maxLength) {
			    		Double score = (Double) v.getEigenvectorCentrality();
			    		System.out.println(v.getId() + " " + v.getLabel() + " " + score);
						outputWriter.write(v.getLabel() + "\n");			 			
			 		} else {
			 			break;
			 		}
			 	}
			}
			outputWriter.flush();
			outputWriter.close();
			
			if (fileName.contains("vandalism")) {
				FileUtils fileUtils = new FileUtils();
				fileUtils.copyFile(fileName + "_eigenvector.txt", "model\\coocurrence_vandalism_eigenvector.txt");
			}	    	
		    
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void authorityWordList(String fileName, Double minValue, int maxLength) {
		System.out.println("Loading " + fileName + ".graphml ...");
		
		GraphMLReader<UndirectedGraph<Vertex, Edge>, Vertex, Edge> graphMLReader;		
		try {
			graphMLReader = new GraphMLReader<UndirectedGraph<Vertex, Edge>, Vertex, Edge>(new VertexFactory(), new EdgeFactory());
			final UndirectedGraph<Vertex, Edge> graph = new UndirectedSparseMultigraph<Vertex, Edge>();
			graphMLReader.load(fileName + ".graphml", graph);
		    System.out.println("Graph " + fileName + " loaded: " + graph.getVertexCount() + " vertex and " + graph.getEdgeCount() + " edges");
		    
			Map<String, GraphMLMetadata<Vertex>> vertexMetadata = graphMLReader.getVertexMetadata();
			Map<String, GraphMLMetadata<Edge>> edgeMetadata = graphMLReader.getEdgeMetadata();
			for (Vertex v : graph.getVertices()) {
				v.setLabel(vertexMetadata.get("label").transformer.transform(v));
			}

			for (Edge e : graph.getEdges())	{		
				e.setWeight(Double.parseDouble(edgeMetadata.get("weight").transformer.transform(e)));
			}
			
			Predicate edgeWeightPredicate = new EdgeWeightPredicate(1,">=");
			EdgePredicateFilter edgePredicateFilter = new EdgePredicateFilter(edgeWeightPredicate);
			Graph graph2 = edgePredicateFilter.transform(graph);
						
		    Transformer<Edge, Double> edgeWeightsTransformer = new Transformer<Edge,Double>() {
		    	public Double transform(Edge e) {
		    		return e.getWeight();
		    	}
		    };  		    
			
			System.out.println("Calculating Authority");
		     
		    HITS<Vertex, Edge> hits = new HITS<Vertex, Edge>(graph2,edgeWeightsTransformer,0.0);
		    hits.initialize();
		    hits.setTolerance(0.000001);
		    hits.setMaxIterations(20);
		    hits.evaluate();		    
		    
		    for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {
		    	v.setAuthority(hits.getVertexScore(v).authority);
		    }
		    
	    	System.out.println("Exporting word list");
		    
			OutputStream outputStream;
			outputStream = new FileOutputStream(fileName + "_authority.txt");
			Writer outputWriter = new OutputStreamWriter(outputStream,Charset.forName("UTF-8"));
			outputWriter = new BufferedWriter(outputWriter);
			
			if (maxLength == 0) {
			    for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {		    				    		
		    		Double score = (Double) v.getAuthority();
		    		System.out.println(v.getId() + " " + v.getLabel() + " " + score);
					outputWriter.write(v.getLabel() + "\n");
		    	}
			} else {
				
				List <Vertex> verticesList = new ArrayList <Vertex> ();	
				
		    	for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {
		    		verticesList.add(v);
		    	}
		    					
			 	Comparator <Vertex> compareVertices = new Comparator<Vertex> () {
			 	    public int compare(Vertex a, Vertex b) {
			 	    	Double scoreb = (Double) b.getAuthority();
			 	    	Double scorea = (Double) a.getAuthority();
			 	        
			 	    	return Double.compare(scoreb, scorea);
			 	    };
			 	};
			 	  
			 	Collections.sort(verticesList, compareVertices);
			 	
			 	int lastElement = 0;
			 	for (Vertex v : verticesList) {
			 		lastElement++;
			 		if (lastElement <= maxLength) {
			    		Double score = (Double) v.getAuthority();
			    		System.out.println(v.getId() + " " + v.getLabel() + " " + score);
						outputWriter.write(v.getLabel() + "\n");			 			
			 		} else {
			 			break;
			 		}
			 	}
			}
			outputWriter.flush();
			outputWriter.close();
			
			if (fileName.contains("vandalism")) {
				FileUtils fileUtils = new FileUtils();
				fileUtils.copyFile(fileName + "_authority.txt", "model\\coocurrence_vandalism_authority.txt");
			}	    	
		    
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void betweennessCentralityWordList(String fileName, Double minValue, int maxLength) {
		System.out.println("Loading " + fileName + ".graphml ...");
		
		GraphMLReader<UndirectedGraph<Vertex, Edge>, Vertex, Edge> graphMLReader;		
		try {
			graphMLReader = new GraphMLReader<UndirectedGraph<Vertex, Edge>, Vertex, Edge>(new VertexFactory(), new EdgeFactory());
			final UndirectedGraph<Vertex, Edge> graph = new UndirectedSparseMultigraph<Vertex, Edge>();
			graphMLReader.load(fileName + ".graphml", graph);
		    System.out.println("Graph " + fileName + " loaded: " + graph.getVertexCount() + " vertex and " + graph.getEdgeCount() + " edges");
		    
			Map<String, GraphMLMetadata<Vertex>> vertexMetadata = graphMLReader.getVertexMetadata();
			Map<String, GraphMLMetadata<Edge>> edgeMetadata = graphMLReader.getEdgeMetadata();
			for (Vertex v : graph.getVertices()) {
				v.setLabel(vertexMetadata.get("label").transformer.transform(v));
			}

			for (Edge e : graph.getEdges())	{		
				e.setWeight(Double.parseDouble(edgeMetadata.get("weight").transformer.transform(e)));
			}
			
			Predicate edgeWeightPredicate = new EdgeWeightPredicate(1,">=");
			EdgePredicateFilter edgePredicateFilter = new EdgePredicateFilter(edgeWeightPredicate);
			Graph graph2 = edgePredicateFilter.transform(graph);
						
		    Transformer<Edge, Double> edgeWeightsTransformer = new Transformer<Edge,Double>() {
		    	public Double transform(Edge e) {
		    		return e.getWeight();
		    	}
		    };  		    
			
			System.out.println("Calculating Betweenness Centrality");
		     
			BetweennessCentrality<Vertex, Edge> betweennessCentrality = new BetweennessCentrality<Vertex, Edge>(graph2,edgeWeightsTransformer);		    
			
			for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {
		    	v.setBetweennessCentrality(betweennessCentrality.getVertexScore(v));
		    }
		    
	    	System.out.println("Exporting word list");
		    
			OutputStream outputStream;
			outputStream = new FileOutputStream(fileName + "_betweenness.txt");
			Writer outputWriter = new OutputStreamWriter(outputStream,Charset.forName("UTF-8"));
			outputWriter = new BufferedWriter(outputWriter);
			
			if (maxLength == 0) {
			    for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {		    		
		    		
		    		Double score = (Double) v.getBetweennessCentrality();
		    		System.out.println(v.getId() + " " + v.getLabel() + " " + score);
					outputWriter.write(v.getLabel() + "\n");
		    	}
			} else {
				
				List <Vertex> verticesList = new ArrayList <Vertex> ();	
				
		    	for (Vertex v : (Collection<Vertex>) graph2.getVertices()) {
		    		verticesList.add(v);
		    	}
		    					
			 	Comparator <Vertex> compareVertices = new Comparator<Vertex> () {
			 	    public int compare(Vertex a, Vertex b) {
			 	    	Double scoreb = (Double) b.getBetweennessCentrality();
			 	    	Double scorea = (Double) a.getBetweennessCentrality();
			 	        
			 	    	return Double.compare(scoreb, scorea);
			 	    };
			 	};
			 	  
			 	Collections.sort(verticesList, compareVertices);
			 	
			 	int lastElement = 0;
			 	for (Vertex v : verticesList) {
			 		lastElement++;
			 		if (lastElement <= maxLength) {
			    		Double score = (Double) v.getBetweennessCentrality();
			    		System.out.println(v.getId() + " " + v.getLabel() + " " + score);
						outputWriter.write(v.getLabel() + "\n");			 			
			 		} else {
			 			break;
			 		}
			 	}
			}
			outputWriter.flush();
			outputWriter.close();
			
			if (fileName.contains("vandalism")) {
				FileUtils fileUtils = new FileUtils();
				fileUtils.copyFile(fileName + "_betweenness.txt", "model\\coocurrence_vandalism_betweenness.txt");
			}	    	
		    
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
	}
}
