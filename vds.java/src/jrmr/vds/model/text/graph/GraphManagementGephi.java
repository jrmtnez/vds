package jrmr.vds.model.text.graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jrmr.vds.model.io.FileUtils;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Query;
import org.gephi.filters.api.Range;
import org.gephi.filters.plugin.attribute.AttributeRangeBuilder;
import org.gephi.filters.plugin.graph.DegreeRangeBuilder.DegreeRangeFilter;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.EigenvectorCentrality;
import org.gephi.statistics.plugin.Hits;
import org.gephi.statistics.plugin.PageRank;
import org.gephi.statistics.plugin.WeightedDegree;
import org.openide.util.Lookup;

public class GraphManagementGephi {

	public void pageRankWordList(String fileName, Double minValue, int maxLength) {
    	
    	ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
    	pc.newProject();
    	Workspace workspace = pc.getCurrentWorkspace();

    	AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
    	GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
    	ImportController importController = Lookup.getDefault().lookup(ImportController.class);

    	System.out.println("Loading " + fileName + ".graphml ...");
    	Container container;
    	try {
    	    File file = new File(fileName + ".graphml");
    	    container = importController.importFile(file);
    	    container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED);
    	} catch (Exception ex) {
    	    ex.printStackTrace();
    	    return;
    	}

    	importController.process(container, new DefaultProcessor(), workspace);

    	System.out.println("Calculating PageRank");
    	PageRank pageRank = new PageRank();
    	pageRank.setDirected(false);
    	pageRank.execute(graphModel, attributeModel);   	
    	
    	FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
    	AttributeRangeBuilder.AttributeRangeFilter pageRankFilter = 
    			new AttributeRangeBuilder.AttributeRangeFilter(attributeModel.getNodeTable().getColumn(PageRank.PAGERANK));    	
    	pageRankFilter.setRange(new Range(minValue, Double.MAX_VALUE));    	
    	Query query = filterController.createQuery(pageRankFilter);
    	GraphView view = filterController.filter(query);
    	graphModel.setVisibleView(view); 
    	UndirectedGraph graph = graphModel.getUndirectedGraphVisible();
    	
    	AttributeColumn attributeColumn = attributeModel.getNodeTable().getColumn(PageRank.PAGERANK);
    	
    	System.out.println("Exporting word list");
	 	try 
		{
			OutputStream outputStream;
			outputStream = new FileOutputStream(fileName + "_pagerank.txt");
			Writer outputWriter = new OutputStreamWriter(outputStream,Charset.forName("UTF-8"));
			outputWriter = new BufferedWriter(outputWriter);
			
			if (maxLength == 0) {
		    	for (Node n : graph.getNodes()) {
		    		Double score = (Double) n.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
		    		System.out.println(n.getId() + " " + n.getNodeData().getLabel() + " " + score);
					outputWriter.write(n.getNodeData().getLabel() + "\n");
		    	}
			} else {
				
				List <Node> nodeList = new ArrayList <Node> ();	
				
		    	for (Node n : graph.getNodes()) {
		    		nodeList.add(n);
		    	}
		    					
			 	Comparator <Node> compareNodes = new Comparator<Node> () {
			 	    public int compare(Node a, Node b) {
			 	    	Double scoreb = (Double) b.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
			 	    	Double scorea = (Double) a.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
			 	        
			 	    	return Double.compare(scoreb, scorea);
			 	    };
			 	};
			 	  
			 	Collections.sort(nodeList, compareNodes);
			 	
			 	int lastElement = 0;
			 	for (Node n : nodeList) {
			 		lastElement++;
			 		if (lastElement <= maxLength) {
			    		Double score = (Double) n.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
			    		System.out.println(n.getId() + " " + n.getNodeData().getLabel() + " " + score);
						outputWriter.write(n.getNodeData().getLabel() + "\n");			 			
			 		} else {
			 			break;
			 		}
			 	}
			}
			outputWriter.flush();
			outputWriter.close();
			
			if (fileName.contains("vandalism")) {
				FileUtils fileUtils = new FileUtils();
				fileUtils.copyFile(fileName + "_pagerank.txt", "model//coocurrence_vandalism_pagerank.txt");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}	    	
    }
	
	
	public void degreeWordList(String fileName, Double minValue, int maxLength) {
    	
    	ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
    	pc.newProject();
    	Workspace workspace = pc.getCurrentWorkspace();

    	AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
    	GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
    	ImportController importController = Lookup.getDefault().lookup(ImportController.class);

    	System.out.println("Loading " + fileName + ".graphml ...");
    	Container container;
    	try {
    	    File file = new File(fileName + ".graphml");
    	    container = importController.importFile(file);
    	    container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED);
    	} catch (Exception ex) {
    	    ex.printStackTrace();
    	    return;
    	}

    	importController.process(container, new DefaultProcessor(), workspace);

    	System.out.println("Calculating Degree");
    	WeightedDegree degree = new WeightedDegree();
    	degree.execute(graphModel, attributeModel);   	
    	
    	FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
    	AttributeRangeBuilder.AttributeRangeFilter degreeFilter = 
    			new AttributeRangeBuilder.AttributeRangeFilter(attributeModel.getNodeTable().getColumn(WeightedDegree.WDEGREE));    	
    	degreeFilter.setRange(new Range(minValue, Double.MAX_VALUE));    	
    	Query query = filterController.createQuery(degreeFilter);
    	GraphView view = filterController.filter(query);
    	graphModel.setVisibleView(view); 
    	UndirectedGraph graph = graphModel.getUndirectedGraphVisible();
    	
    	AttributeColumn attributeColumn = attributeModel.getNodeTable().getColumn(WeightedDegree.WDEGREE);
    	
    	System.out.println("Exporting word list");
	 	try 
		{
			OutputStream outputStream;
			outputStream = new FileOutputStream(fileName + "_degree.txt");
			Writer outputWriter = new OutputStreamWriter(outputStream,Charset.forName("UTF-8"));
			outputWriter = new BufferedWriter(outputWriter);
			
			if (maxLength == 0) {
		    	for (Node n : graph.getNodes()) {
		    		Double score = (Double) n.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
		    		System.out.println(n.getId() + " " + n.getNodeData().getLabel() + " " + score);
					outputWriter.write(n.getNodeData().getLabel() + "\n");
		    	}
			} else {
				
				List <Node> nodeList = new ArrayList <Node> ();	
				
		    	for (Node n : graph.getNodes()) {
		    		nodeList.add(n);
		    	}
		    					
			 	Comparator <Node> compareNodes = new Comparator<Node> () {
			 	    public int compare(Node a, Node b) {
			 	    	Double scoreb = (Double) b.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
			 	    	Double scorea = (Double) a.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
			 	        
			 	    	return Double.compare(scoreb, scorea);
			 	    };
			 	};
			 	  
			 	Collections.sort(nodeList, compareNodes);
			 	
			 	int lastElement = 0;
			 	for (Node n : nodeList) {
			 		lastElement++;
			 		if (lastElement <= maxLength) {
			 			Double score = (Double) n.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
			    		System.out.println(n.getId() + " " + n.getNodeData().getLabel() + " " + score);
						outputWriter.write(n.getNodeData().getLabel() + "\n");			 			
			 		} else {
			 			break;
			 		}
			 	}
			}
			outputWriter.flush();
			outputWriter.close();
			
			if (fileName.contains("vandalism")) {
				FileUtils fileUtils = new FileUtils();
				fileUtils.copyFile(fileName + "_degree.txt", "model//coocurrence_vandalism_degree.txt");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}	    	
    }
	
	public void degreeWordList2(String fileName, int minValue, int maxLength) {
    	
    	ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
    	pc.newProject();
    	Workspace workspace = pc.getCurrentWorkspace();

    	GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
    	ImportController importController = Lookup.getDefault().lookup(ImportController.class);

    	System.out.println("Loading " + fileName + ".graphml ...");
    	Container container;
    	try {
    	    File file = new File(fileName + ".graphml");
    	    container = importController.importFile(file);
    	    container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED);
    	} catch (Exception ex) {
    	    ex.printStackTrace();
    	    return;
    	}

    	importController.process(container, new DefaultProcessor(), workspace);
    	UndirectedGraph graph = graphModel.getUndirectedGraph();   	
    	
    	System.out.println("Calculating degree");    	
    	FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
    	DegreeRangeFilter degreeFilter = new DegreeRangeFilter();
    	degreeFilter.init(graph);
    	degreeFilter.setRange(new Range(minValue, Integer.MAX_VALUE));
    	Query query = filterController.createQuery(degreeFilter);    	
    	GraphView view = filterController.filter(query);
    	graphModel.setVisibleView(view); 
    	UndirectedGraph visibleGraph = graphModel.getUndirectedGraphVisible();
    	    	
    	System.out.println("Exporting word list");
	 	try 
		{
			OutputStream outputStream;
			outputStream = new FileOutputStream(fileName + "_degree.txt");
			Writer outputWriter = new OutputStreamWriter(outputStream,Charset.forName("UTF-8"));
			outputWriter = new BufferedWriter(outputWriter);
			
			if (maxLength == 0) {
		    	for (Node n : visibleGraph.getNodes()) {
		    		int score = visibleGraph.getDegree(n);		    		
		    		System.out.println(n.getId() + " " + n.getNodeData().getLabel() + " " + score);
					outputWriter.write(n.getNodeData().getLabel() + "\n");
		    	}
			} else {
				
				List <Node> nodeList = new ArrayList <Node> ();	
				
		    	for (Node n : visibleGraph.getNodes()) {
		    		nodeList.add(n);
		    	}
		    					
			 	Comparator <Node> compareNodes = new Comparator<Node> () {
			 	    public int compare(Node a, Node b) {
			 	    	int scoreb = visibleGraph.getDegree(b);
			 	    	int scorea = visibleGraph.getDegree(a);
			 	        
			 	    	return Integer.compare(scoreb, scorea);
			 	    };
			 	};
			 	  
			 	Collections.sort(nodeList, compareNodes);
			 	
			 	int lastElement = 0;
			 	for (Node n : nodeList) {
			 		lastElement++;
			 		if (lastElement <= maxLength) {
			    		int score = visibleGraph.getDegree(n);
			    		System.out.println(n.getId() + " " + n.getNodeData().getLabel() + " " + score);
						outputWriter.write(n.getNodeData().getLabel() + "\n");			 			
			 		} else {
			 			break;
			 		}
			 	}
			}
			outputWriter.flush();
			outputWriter.close();
			
			if (fileName.contains("vandalism")) {
				FileUtils fileUtils = new FileUtils();
				fileUtils.copyFile(fileName + "_degree.txt", "model//coocurrence_vandalism_degree.txt");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}	    	
    }	
	
	public void eigenvectorCentralityWordList(String fileName, Double minValue, int maxLength) {
    	
    	ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
    	pc.newProject();
    	Workspace workspace = pc.getCurrentWorkspace();

    	AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
    	GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
    	ImportController importController = Lookup.getDefault().lookup(ImportController.class);

    	System.out.println("Loading " + fileName + ".graphml ...");
    	Container container;
    	try {
    	    File file = new File(fileName + ".graphml");
    	    container = importController.importFile(file);
    	    container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED);
    	} catch (Exception ex) {
    	    ex.printStackTrace();
    	    return;
    	}

    	importController.process(container, new DefaultProcessor(), workspace);

    	System.out.println("Calculating Eigenvector Centrality");
    	EigenvectorCentrality eigenvectorCentrality = new EigenvectorCentrality();
    	eigenvectorCentrality.setDirected(false);
    	eigenvectorCentrality.execute(graphModel, attributeModel);   	
    	
    	FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
    	AttributeRangeBuilder.AttributeRangeFilter eigenvectorContralityFilter = 
    			new AttributeRangeBuilder.AttributeRangeFilter(attributeModel.getNodeTable().getColumn(EigenvectorCentrality.EIGENVECTOR));    	
    	eigenvectorContralityFilter.setRange(new Range(minValue, Double.MAX_VALUE));    	
    	Query query = filterController.createQuery(eigenvectorContralityFilter);
    	GraphView view = filterController.filter(query);
    	graphModel.setVisibleView(view); 
    	UndirectedGraph graph = graphModel.getUndirectedGraphVisible();
    	
    	AttributeColumn attributeColumn = attributeModel.getNodeTable().getColumn(EigenvectorCentrality.EIGENVECTOR);
    	
    	System.out.println("Exporting word list");
	 	try 
		{
			OutputStream outputStream;
			outputStream = new FileOutputStream(fileName + "_eigenvector.txt");
			Writer outputWriter = new OutputStreamWriter(outputStream,Charset.forName("UTF-8"));
			outputWriter = new BufferedWriter(outputWriter);
			
			if (maxLength == 0) {
		    	for (Node n : graph.getNodes()) {
		    		Double score = (Double) n.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
		    		System.out.println(n.getId() + " " + n.getNodeData().getLabel() + " " + score);
					outputWriter.write(n.getNodeData().getLabel() + "\n");
		    	}
			} else {
				
				List <Node> nodeList = new ArrayList <Node> ();	
				
		    	for (Node n : graph.getNodes()) {
		    		nodeList.add(n);
		    	}
		    					
			 	Comparator <Node> compareNodes = new Comparator<Node> () {
			 	    public int compare(Node a, Node b) {
			 	    	Double scoreb = (Double) b.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
			 	    	Double scorea = (Double) a.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
			 	        
			 	    	return Double.compare(scoreb, scorea);
			 	    };
			 	};
			 	  
			 	Collections.sort(nodeList, compareNodes);
			 	
			 	int lastElement = 0;
			 	for (Node n : nodeList) {
			 		lastElement++;
			 		if (lastElement <= maxLength) {
			    		Double score = (Double) n.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
			    		System.out.println(n.getId() + " " + n.getNodeData().getLabel() + " " + score);
						outputWriter.write(n.getNodeData().getLabel() + "\n");			 			
			 		} else {
			 			break;
			 		}
			 	}
			}
			outputWriter.flush();
			outputWriter.close();
			
			if (fileName.contains("vandalism")) {
				FileUtils fileUtils = new FileUtils();
				fileUtils.copyFile(fileName + "_eigenvector.txt", "model//coocurrence_vandalism_eigenvector.txt");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}	    	
    }

	public void authorityWordList(String fileName, Float minValue, int maxLength) {
    	
    	ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
    	pc.newProject();
    	Workspace workspace = pc.getCurrentWorkspace();

    	AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
    	GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
    	ImportController importController = Lookup.getDefault().lookup(ImportController.class);

    	System.out.println("Loading " + fileName + ".graphml ...");
    	Container container;
    	try {
    	    File file = new File(fileName + ".graphml");
    	    container = importController.importFile(file);
    	    container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED);
    	} catch (Exception ex) {
    	    ex.printStackTrace();
    	    return;
    	}
    	
    	importController.process(container, new DefaultProcessor(), workspace);
    	
    	System.out.println("Calculating Authority");
    	Hits hits = new Hits();
    	hits.setUndirected(true);
//    	hits.setEpsilon(0.0001);
    	hits.execute(graphModel, attributeModel);   	
        	
    	FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
    	AttributeRangeBuilder.AttributeRangeFilter authorityFilter = 
    			new AttributeRangeBuilder.AttributeRangeFilter(attributeModel.getNodeTable().getColumn(Hits.AUTHORITY));    	
        	
    	authorityFilter.setRange(new Range(minValue, Float.MAX_VALUE));    	
    	Query query = filterController.createQuery(authorityFilter);
    	
    	GraphView view = filterController.filter(query);
    	
    	graphModel.setVisibleView(view); 
    	UndirectedGraph graph = graphModel.getUndirectedGraphVisible();
    	
    	AttributeColumn attributeColumn = attributeModel.getNodeTable().getColumn(Hits.AUTHORITY);
    	
    	System.out.println("Exporting word list");
	 	try 
		{
			OutputStream outputStream;
			outputStream = new FileOutputStream(fileName + "_authority.txt");
			Writer outputWriter = new OutputStreamWriter(outputStream,Charset.forName("UTF-8"));
			outputWriter = new BufferedWriter(outputWriter);
			
			if (maxLength == 0) {
		    	for (Node n : graph.getNodes()) {
		    		Float score = (Float) n.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
		    		System.out.println(n.getId() + " " + n.getNodeData().getLabel() + " " + score);
					outputWriter.write(n.getNodeData().getLabel() + "\n");
		    	}
			} else {
				
				List <Node> nodeList = new ArrayList <Node> ();	
				
		    	for (Node n : graph.getNodes()) {
		    		nodeList.add(n);
		    	}
		    					
			 	Comparator <Node> compareNodes = new Comparator<Node> () {
			 	    public int compare(Node a, Node b) {
			 	    	Float scoreb = (Float) b.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
			 	    	Float scorea = (Float) a.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
			 	        
			 	    	return Float.compare(scoreb, scorea);
			 	    };
			 	};
			 	  
			 	Collections.sort(nodeList, compareNodes);
			 	
			 	int lastElement = 0;
			 	for (Node n : nodeList) {
			 		lastElement++;
			 		if (lastElement <= maxLength) {
			 			Float score = (Float) n.getNodeData().getAttributes().getValue(attributeColumn.getIndex());
			    		System.out.println(n.getId() + " " + n.getNodeData().getLabel() + " " + score);
						outputWriter.write(n.getNodeData().getLabel() + "\n");			 			
			 		} else {
			 			break;
			 		}
			 	}
			}
			outputWriter.flush();
			outputWriter.close();
			
			if (fileName.contains("vandalism")) {
				FileUtils fileUtils = new FileUtils();
				fileUtils.copyFile(fileName + "_authority.txt", "model//coocurrence_vandalism_authority.txt");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}	    	
    }
	
}
