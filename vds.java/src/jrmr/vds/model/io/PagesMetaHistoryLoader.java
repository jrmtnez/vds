package jrmr.vds.model.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jrmr.vds.model.config.Config;
import jrmr.vds.model.db.DatabaseUtils;

public class PagesMetaHistoryLoader {

	Config config;

	public PagesMetaHistoryLoader() {
		config = new Config();
	}

	public void load() {

		String inputFile = config.getWpPageMetaHistoryFile();

		String tagContent = "";

		String pageTitle = "";
		String pageNs = "";
		String pageId = "";
		String revisionId = "";
		String revisionTimestamp = "";
		String revisionContributorUsername = "";
		String revisionContributorId = "";
		String revisionContributorIp = "";
		String revisionMinor = "";
		String revisionComment = "";
		String revisionText = "";
		String revisionSha1 = "";
		String revisionModel = "";	
		String revisionFormat = "";	
		boolean addRevisionText = false;
		boolean addRevisionComment = false;

		DatabaseUtils databaseUtils = new DatabaseUtils();   	    
		databaseUtils.openConnection();
		databaseUtils.createTable(databaseUtils.getCreateWpPageMetaHistoryStatement());		

		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();			
			InputStream is = new FileInputStream(inputFile); 
			XMLStreamReader reader = factory.createXMLStreamReader(is);

			while(reader.hasNext()){
				int event = reader.next();

				switch(event) {
				case XMLStreamConstants.START_ELEMENT :
					switch(reader.getLocalName()) {
					case "page" :						
						pageTitle = "";
						pageNs = "";								
						pageId = "";
						break;

					case "revision" :
						revisionId = "";
						revisionTimestamp = "";
						revisionContributorUsername = "";
						revisionContributorId = "";
						revisionContributorIp = "";
						revisionMinor = "";
						revisionComment = "";
						revisionText = "";
						revisionSha1 = "";
						revisionModel = "";	
						revisionFormat = "";
						break;

					case "comment" :						
						revisionComment = "";
						addRevisionComment = true;
						break;

					case "text" :						
						revisionText = "";
						addRevisionText = true;
						break;

					}
					break;
				case XMLStreamConstants.CHARACTERS:
					tagContent = reader.getText().trim(); 

					if (addRevisionText) {
						revisionText = revisionText + tagContent;	
					}

					if (addRevisionComment) {
						revisionComment = revisionComment + tagContent;	
					}

					break;
				case XMLStreamConstants.END_ELEMENT:
					switch(reader.getLocalName()) {					    	
					case "title":
						pageTitle = tagContent;
						break;

					case "ns":
						pageNs = tagContent;
						break;

					case "id":		

						// identificación del tercer nodo "id" correspondiente a "contributor"
						if (revisionContributorId.equals("") && !revisionId.equals(""))
							revisionContributorId = tagContent;

						// identificación del segundo nodo "id" correspondiente a "revision"
						if (revisionId.equals("") && !pageId.equals(""))
							revisionId = tagContent;

						// identificación del primer nodo "id" correspondiente a "page"
						if (pageId.equals(""))
							pageId = tagContent;

						break;

					case "timestamp":
						revisionTimestamp = tagContent;
						break;

					case "username":
						revisionContributorUsername = tagContent;
						break;

					case "ip":
						revisionContributorIp = tagContent;
						break;

					case "minor":
						revisionMinor = tagContent;
						break;

					case "comment":
						addRevisionComment = false;
						break;

					case "text":
						addRevisionText = false;				    		
						break;

					case "sha1":
						revisionSha1 = tagContent;
						break;

					case "model":
						revisionModel = tagContent;
						break;

					case "format":
						revisionFormat = tagContent;
						break;

					case "revision" :

						ZonedDateTime revisionTimestamp2 = ZonedDateTime.parse(revisionTimestamp); 
						Timestamp revisionTimestamp3 = Timestamp.valueOf(
								revisionTimestamp2.getYear() + "-" +
										revisionTimestamp2.getMonthValue()+ "-" + 
										revisionTimestamp2.getDayOfMonth() + " " + 
										revisionTimestamp2.getHour() + ":" +
										revisionTimestamp2.getMinute() + ":" +
										revisionTimestamp2.getSecond());

						if (revisionContributorId.equals(""))
							revisionContributorId = "0";

						//revisionText = ""; // desbordamiento

						databaseUtils.insertWpPageRevision(
								pageTitle, 
								Integer.parseInt(pageNs), 
								Integer.parseInt(pageId), 
								Integer.parseInt(revisionId), 
								revisionTimestamp3, 
								revisionContributorUsername, 
								Integer.parseInt(revisionContributorId), 
								revisionContributorIp, 
								revisionMinor, 
								revisionComment, 
								revisionText, 
								revisionSha1, 
								revisionModel, 
								revisionFormat);

						System.out.println(revisionId + " - " + pageTitle);

						break;
					}
					break;
				}
			}			

		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		databaseUtils.closeConnection();
	}
}
