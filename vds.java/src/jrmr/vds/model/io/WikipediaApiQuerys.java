package jrmr.vds.model.io;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WikipediaApiQuerys {
	
    public ArrayList<String> getPageLinks(String page) {
    	
    	String url = "https://en.wikipedia.org/w/api.php?action=parse&format=xml&page=" + page + "&prop=links";
    	ArrayList<String> linkList = new ArrayList<String>();
    	
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder builder;
    	
    	try {
    		builder = factory.newDocumentBuilder();
    		Document document =	builder.parse(url);
    		
    		// "parse" node
    		NodeList nodeList1 = document.getDocumentElement().getChildNodes();
    		Node parseNode = nodeList1.item(0);   
    		
    		// "links" node
    		NodeList nodeList2 = parseNode.getChildNodes();
    		Node linksNode = nodeList2.item(0);
    		
    		// "pl" nodes
    		NodeList nodeList3 = linksNode.getChildNodes();
    		for (int i = 0; i < nodeList3.getLength(); i++) {

    			Node pageLinkNode = nodeList3.item(i);    	
    			
    			if (pageLinkNode.getAttributes().item(1).getNodeValue().equals("0")) { // only with attribute "ns=0"
    				linkList.add(pageLinkNode.getTextContent());
    			}    			
    		}

    	} catch (ParserConfigurationException e) {
    		e.printStackTrace();
    	} catch (SAXException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return linkList;
    }
	
    public String getWikitext(String page) {
    	
    	String url = "https://en.wikipedia.org/w/api.php?action=parse&format=xml&page=" + page + "&prop=wikitext";
    	String wikitext = "";
    	
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder builder;
    	
    	try {
    		builder = factory.newDocumentBuilder();
    		Document document =	builder.parse(url);
    		
    		// "parse" node
    		NodeList nodeList1 = document.getDocumentElement().getChildNodes();
    		Node parseNode = nodeList1.item(0);   
    		
    		// "wikitext" node
    		NodeList nodeList2 = parseNode.getChildNodes();    		    		
    		Node wikitextNode = nodeList2.item(0);
    		
			wikitext = wikitextNode.getTextContent();

    	} catch (ParserConfigurationException e) {
    		e.printStackTrace();
    	} catch (SAXException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return wikitext;
    }

    
}
