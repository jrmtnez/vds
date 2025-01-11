package jrmr.vds.model.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Config {
    
	String PROPERTIES_FILE = "vds.properties";
	String PROPERTIES_FILE_DESCRIPTION = "jrmr.vds configuration file";
    private Properties vdsProperties;
    
	private final StringProperty databaseURL;
	private final StringProperty databaseUser;
	private final StringProperty databasePassword;
	
	private final StringProperty panEditFile;
	private final StringProperty panGoldAnnotationsFile;
	private final StringProperty panArticleRevFolder;	
	private final StringProperty panEditTable;
	private final StringProperty panArticleRevTable;	
	private final StringProperty panTokenizedArticleRevTable;	
	private final StringProperty panPlainTextArticleRevTable;	
	private final StringProperty panFeatureTable;
	private final StringProperty panCorpus;
	private final StringProperty onlyWithClass;			
	
	private final StringProperty wpPageMetaHistoryFile;
	private final StringProperty wpPageMetaHistoryTable;
	
	private final StringProperty wvTrainMethod;
	private final IntegerProperty wvVectorSize;
	private final IntegerProperty wvWindowSize;
	private final StringProperty panWordsDiffTable;
	private final StringProperty panDiffTable;
	private final StringProperty linkedArticleTable;
	private final StringProperty wordVectorModelFile;
	
	public Config() {
        initProperties();
        
		this.databaseURL = new SimpleStringProperty(vdsProperties.getProperty("database_url"));
		this.databaseUser = new SimpleStringProperty(vdsProperties.getProperty("database_user"));
		this.databasePassword = new SimpleStringProperty(vdsProperties.getProperty("database_password"));
		
		this.panEditFile = new SimpleStringProperty(vdsProperties.getProperty("pan_edit_file"));
		this.panGoldAnnotationsFile = new SimpleStringProperty(vdsProperties.getProperty("pan_gold_annotations_file"));
		this.panArticleRevFolder = new SimpleStringProperty(vdsProperties.getProperty("pan_article_revision_folder"));
		this.panEditTable = new SimpleStringProperty(vdsProperties.getProperty("pan_edit_table"));
		this.panArticleRevTable = new SimpleStringProperty(vdsProperties.getProperty("pan_article_revision_table"));
		this.panTokenizedArticleRevTable = new SimpleStringProperty(vdsProperties.getProperty("pan_tokenized_article_revision_table"));
		this.panPlainTextArticleRevTable = new SimpleStringProperty(vdsProperties.getProperty("pan_plain_text_article_revision_table"));
		this.panFeatureTable = new SimpleStringProperty(vdsProperties.getProperty("pan_feature_table"));
		this.panCorpus = new SimpleStringProperty(vdsProperties.getProperty("pan_corpus"));		
		this.onlyWithClass = new SimpleStringProperty(vdsProperties.getProperty("only_with_class"));				
		
		this.wpPageMetaHistoryFile = new SimpleStringProperty(vdsProperties.getProperty("wp_page_meta_history_file"));		
		this.wpPageMetaHistoryTable = new SimpleStringProperty(vdsProperties.getProperty("wp_page_meta_history_table"));
		
		this.wvTrainMethod = new SimpleStringProperty(vdsProperties.getProperty("wv_train_method"));
		this.wvWindowSize = new SimpleIntegerProperty(Integer.parseInt(vdsProperties.getProperty("wv_window_size")));
		this.wvVectorSize = new SimpleIntegerProperty(Integer.parseInt(vdsProperties.getProperty("wv_vector_size")));
		this.panWordsDiffTable = new SimpleStringProperty(vdsProperties.getProperty("pan_words_diff_table"));		
		this.panDiffTable = new SimpleStringProperty(vdsProperties.getProperty("pan_diff_table"));
		this.linkedArticleTable = new SimpleStringProperty(vdsProperties.getProperty("linked_article_table"));
		this.wordVectorModelFile = new SimpleStringProperty(vdsProperties.getProperty("word_vector_model_file"));		
	}
	
    // --- databaseURL ---
	public String getDatabaseURL() {
        return databaseURL.get();
    }
    
    public void setDatabaseURL(String databaseURL) {
        this.databaseURL.set(databaseURL);
    }
    
    public StringProperty databaseURLProperty() {
        return databaseURL;
    }

    // --- databaseUser ---
    public String getDatabaseUser() {
        return databaseUser.get();
    }

    public void setDatabaseUser(String databaseUser) {
        this.databaseUser.set(databaseUser);
    }

    public StringProperty databaseUserProperty() {
        return databaseUser;
    }
    
    // --- databasePassword ---
    public String getDatabasePassword() {
        return databasePassword.get();
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword.set(databasePassword);
    }

    public StringProperty databasePasswordProperty() {
        return databasePassword;
    }
    
  
    // --- panEditFile ---
    public String getPanEditFile() {
        return panEditFile.get();
    }

    public void setPanEditFile(String panEditFile) {
        this.panEditFile.set(panEditFile);
    }

    public StringProperty panEditFileProperty() {
        return panEditFile;
    }
    
    // --- panGoldAnnotationsFile ---
    public String getPanGoldAnnotationsFile() {
        return panGoldAnnotationsFile.get();
    }

    public void setPanGoldAnnotationsFile(String panGoldAnnotationsFile) {
        this.panGoldAnnotationsFile.set(panGoldAnnotationsFile);
    }

    public StringProperty panGoldAnnotationsFileProperty() {
        return panGoldAnnotationsFile;
    }

    // --- panArticleRevFolder ---
    public String getPanArticleRevFolder() {
        return panArticleRevFolder.get();
    }

    public void setPanArticleRevFolder(String panArticleRevFolder) {
        this.panArticleRevFolder.set(panArticleRevFolder);
    }

    public StringProperty panArticleRevFolderProperty() {
        return panArticleRevFolder;
    }
    
    // --- panEditTable ---   
    public String getPanEditTable() {
		return panEditTable.get();
	}

    public void setPanEditTable(String panEditTable) {
        this.panEditTable.set(panEditTable);
    }

    public StringProperty panEditTableProperty() {
        return panEditTable;
    }
    

    // --- panArticleRevTable ---
    public String getPanArticleRevTable() {
		return panArticleRevTable.get();
	}

    public void setPanArticleRevTable(String panArticleRevTable) {
        this.panArticleRevTable.set(panArticleRevTable);
    }

    public StringProperty panArticleRevTableProperty() {
        return panArticleRevTable;
    }
    
    // --- panTokenizedArticleRevTable ---
    public String getTokenizedPanArticleRevTable() {
		return panTokenizedArticleRevTable.get();
	}

    public void setTokenizedPanArticleRevTable(String panTokenizedArticleRevTable) {
        this.panTokenizedArticleRevTable.set(panTokenizedArticleRevTable);
    }

    public StringProperty panTokenizedArticleRevTableProperty() {
        return panTokenizedArticleRevTable;
    }

    // --- panPlainTextArticleRevTable ---
    public String getPlainTextPanArticleRevTable() {
		return panPlainTextArticleRevTable.get();
	}

    public void setPlainTextPanArticleRevTable(String panPlainTextArticleRevTable) {
        this.panPlainTextArticleRevTable.set(panPlainTextArticleRevTable);
    }

    public StringProperty panPlainTextArticleRevTableProperty() {
        return panPlainTextArticleRevTable;
    }
    
    // --- panFeatureTable ---
    public String getPanFeatureTable() {
		return panFeatureTable.get();
	}
    
    public void setPanFeatureTable(String panFeatureTable) {
        this.panFeatureTable.set(panFeatureTable);
    }

    public StringProperty panFeatureTableProperty() {
        return panFeatureTable;
    }

    // --- panCorpus ---
    public String getPanCorpus() {
		return panCorpus.get();
	}
    
    public void setPanCorpus(String panCorpus) {
        this.panCorpus.set(panCorpus);
    }

    public StringProperty panCorpusProperty() {
        return panCorpus;
    }
    
    // --- onlyWithClass ---
    public String getOnlyWithClass() {
		return onlyWithClass.get();
	}
    
    public void setOnlyWithClass(String onlyWithClass) {
        this.onlyWithClass.set(onlyWithClass);
    }

    public StringProperty onlyWithClassProperty() {
        return onlyWithClass;
    }
    
    // --- wpPageMetaHistoryFile ---
    public String getWpPageMetaHistoryFile() {
        return wpPageMetaHistoryFile.get();
    }

    public void setWpPageMetaHistoryFile(String wpPageMetaHistoryFile) {
        this.wpPageMetaHistoryFile.set(wpPageMetaHistoryFile);
    }

    public StringProperty wpPageMetaHistoryFileProperty() {
        return wpPageMetaHistoryFile;
    }    
        
    // --- wvTrainMethod ---
    public String getWpPageMetaHistoryTable() {
		return wpPageMetaHistoryTable.get();
	}
    
    public void setWpPageMetaHistoryTable(String wpPageMetaHistoryTable) {
        this.wpPageMetaHistoryTable.set(wpPageMetaHistoryTable);
    }

    public StringProperty wpPageMetaHistoryTableProperty() {
        return wpPageMetaHistoryTable;
    }
    
    // --- wvTrainMethod ---
    public String getWvTrainMethod() {
		return wvTrainMethod.get();
	}
    
    public void setWvTrainMethod(String wvTrainMethod) {
        this.wvTrainMethod.set(wvTrainMethod);
    }

    public StringProperty wvTrainMethodProperty() {
        return wvTrainMethod;
    }
    
    // --- wvVectorSize ---
    public Integer getWvVectorSize() {
		return wvVectorSize.get();
	}
    
    public void setWvVectorSize(Integer wvVectorSize) {
        this.wvVectorSize.set(wvVectorSize);
    }

    public IntegerProperty wvVectorSizeProperty() {
        return wvVectorSize;
    }
    
    // --- wvWindowSize ---
    public Integer getWvWindowSize() {
		return wvWindowSize.get();
	}
    
    public void setWvWindowSize(Integer wvWindowSize) {
        this.wvWindowSize.set(wvWindowSize);
    }

    public IntegerProperty wvWindowSizeProperty() {
        return wvWindowSize;
    }
    
    // --- panWordsDiffTable ---
    public String getPanWordsDiffTable() {
		return panWordsDiffTable.get();
	}
    
    public void setPanWordsDiffTable(String panWordsDiffTable) {
        this.panWordsDiffTable.set(panWordsDiffTable);
    }

    public StringProperty panWordsDiffTableProperty() {
        return panWordsDiffTable;
    }
    
    // --- panDiffTable ---
    public String getPanDiffTable() {
		return panDiffTable.get();
	}
    
    public void setPanDiffTable(String panDiffTable) {
        this.panDiffTable.set(panDiffTable);
    }

    public StringProperty panDiffTableProperty() {
        return panDiffTable;
    }
    
    // --- linkedArticleTable ---
    public String getLinkedArticleTable() {
		return linkedArticleTable.get();
	}
    
    public void setLinkedArticleTable(String linkedArticleTable) {
        this.linkedArticleTable.set(linkedArticleTable);
    }

    public StringProperty linkedArticleTableProperty() {
        return linkedArticleTable;
    }
    
    // --- wordVectorModelFile ---
    public String getWordVectorModelFile() {
        return wordVectorModelFile.get();
    }

    public void setWordVectorModelFile(String wordVectorModelFile) {
        this.wordVectorModelFile.set(wordVectorModelFile);
    }

    public StringProperty wordVectorModelFileProperty() {
        return wordVectorModelFile;
    }    

    
	public void initProperties() {
        try {
        	
            FileInputStream propertiesFile = new FileInputStream(PROPERTIES_FILE);
            vdsProperties = new Properties();
            vdsProperties.load(propertiesFile);
            propertiesFile.close();
          
        } catch (Exception e) {
        	  e.printStackTrace();
        }
    }
    
    public void saveProperties() {
        try {
        	FileOutputStream propertiesFile = new FileOutputStream(PROPERTIES_FILE);          
        	vdsProperties.setProperty("database_url", this.databaseURL.getValue());
        	vdsProperties.setProperty("database_user", this.databaseUser.getValue());
        	vdsProperties.setProperty("database_password", this.databasePassword.getValue());
        	
        	vdsProperties.setProperty("pan_edit_file", this.panEditFile.getValue());
        	vdsProperties.setProperty("pan_gold_annotations_file", this.panGoldAnnotationsFile.getValue());
        	vdsProperties.setProperty("pan_article_revision_folder", this.panArticleRevFolder.getValue());
        	vdsProperties.setProperty("pan_edit_table", this.panEditTable.getValue());
        	vdsProperties.setProperty("pan_article_revision_table", this.panArticleRevTable.getValue());
        	vdsProperties.setProperty("pan_tokenized_article_revision_table", this.panTokenizedArticleRevTable.getValue());
        	vdsProperties.setProperty("pan_plain_text_article_revision_table", this.panPlainTextArticleRevTable.getValue());
        	vdsProperties.setProperty("pan_feature_table", this.panFeatureTable.getValue());
        	vdsProperties.setProperty("pan_corpus", this.panCorpus.getValue());
        	vdsProperties.setProperty("only_with_class", this.onlyWithClass.getValue());
        	
        	vdsProperties.setProperty("wp_page_meta_history_file", this.wpPageMetaHistoryFile.getValue());
        	vdsProperties.setProperty("wp_page_meta_history_table", this.wpPageMetaHistoryTable.getValue());
        	
        	vdsProperties.setProperty("wv_train_method", this.wvTrainMethod.getValue());
        	vdsProperties.setProperty("wv_window_size", this.wvWindowSize.getValue().toString());
        	vdsProperties.setProperty("wv_vector_size", this.wvVectorSize.getValue().toString());
        	vdsProperties.setProperty("pan_words_diff_table", this.panWordsDiffTable.getValue());
        	vdsProperties.setProperty("pan_diff_table", this.panDiffTable.getValue());
        	vdsProperties.setProperty("linked_article_table", this.linkedArticleTable.getValue());
        	vdsProperties.setProperty("word_vector_model_file", this.wordVectorModelFile.getValue());        	
       	
        	vdsProperties.store(propertiesFile, PROPERTIES_FILE_DESCRIPTION);        	
            propertiesFile.close();
            
          
        } catch (Exception e) {
        	  e.printStackTrace();
        }
    }
}
