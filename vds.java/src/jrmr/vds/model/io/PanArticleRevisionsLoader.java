package jrmr.vds.model.io;

import java.io.File;

import jrmr.vds.model.config.Config;
import jrmr.vds.model.db.DatabaseUtils;

public class PanArticleRevisionsLoader {
	
	Config config;
	
	public PanArticleRevisionsLoader() {
		config = new Config();
	}
	
	public void load() {
				
		String corpusPath = config.getPanArticleRevFolder();

		DatabaseUtils databaseUtils = new DatabaseUtils();
		

        databaseUtils.openConnection();		
        
       	databaseUtils.createTable(databaseUtils.getCreatePanArticleRevStatement(config.getPanArticleRevTable(),""));
        
		FileUtils fileUtils = new FileUtils();
		File dir = new File(corpusPath);
		String[] dirList = dir.list();

		if (dirList == null)
		{
			System.out.println("No files in the folder " + corpusPath + ".");
		}
		else 
		{
			int k = 0;
			for (int i = 0; i < dirList.length; i++)
			{
				File dir2 = new File(corpusPath + "//" + dirList[i]);
				String[] dirList2 = dir2.list();

				if (dirList2 == null) 
				{
					System.out.println("No files in the folder " + corpusPath + "//" + dirList[i] + ".");
				} 
				else 
				{
					for (int j = 0; j < dirList2.length; j++)
					{	
						String inputFile = dirList2[j];
						String pathName = corpusPath + "//" + dirList[i] + "//";						
						int revisionId = Integer.parseInt(inputFile.split("\\.")[0]);
						
						String revisionText = fileUtils.file2String(pathName + inputFile);
						
				        databaseUtils.insertPanRevision(config.getPanArticleRevTable(), "", revisionId, revisionText);
						
						k++;
						System.out.println(inputFile + " => " + config.getPanArticleRevTable() + "_" +	config.getPanCorpus() + 
							" " + (100 * k / (dirList.length * dirList2.length)) + "%");
					}				
				}
			}
		}
		
		databaseUtils.closeConnection();
	}	

}
