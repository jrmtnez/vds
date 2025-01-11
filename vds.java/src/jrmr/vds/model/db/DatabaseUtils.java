package jrmr.vds.model.db;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jrmr.vds.model.config.Config;
import jrmr.vds.model.io.FileUtils;
import jrmr.vds.model.io.WikipediaApiQuerys;
import jrmr.vds.model.pan.PanEdit;
import jrmr.vds.model.text.DiffUtils;
import jrmr.vds.model.text.PlainTextExtractor;
import jrmr.vds.model.text.Tokenizer;
import jrmr.vds.model.text.cluster.WordClustering;

public class DatabaseUtils {

	Config config = null;
	Connection con = null;

	public DatabaseUtils() {
		config = new Config();
	}
	
	public void initDatabase() {
		try {
			Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "postgres");
			Statement statement = con.createStatement();
			statement.executeUpdate("CREATE USER user1 WITH PASSWORD 'user1';");
			statement.executeUpdate("CREATE DATABASE pan10 OWNER user1;");			
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}		
	}
	
	public boolean initDatabase2() {
		try {
			Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "postgres");
			Statement statement = con.createStatement();
			statement.executeUpdate(
				"CREATE USER " + config.getDatabaseUser() +	" WITH PASSWORD '" + config.getDatabasePassword() + "';");			
			String databaseName = config.getDatabaseURL().split("/")[config.getDatabaseURL().split("/").length - 1];			
			statement.executeUpdate("CREATE DATABASE " + databaseName + " OWNER " + config.getDatabaseUser() + ";");			
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}	
		return true;
	}

	public void openConnection() {    	
		try {
			con = DriverManager.getConnection(config.getDatabaseURL(), config.getDatabaseUser(), config.getDatabasePassword());
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	
	public Connection openConnection2() {    	
		
		Connection con = null;
		try {
			con = DriverManager.getConnection(config.getDatabaseURL(), config.getDatabaseUser(), config.getDatabasePassword());
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}
		
		return con;
	}

	public void closeConnection() {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}    	
	}

	public String getDBVersion(String url, String user, String password) {

		Connection localCon = null;
		Statement st = null;
		ResultSet rs = null;
		String versionReturned = null;

		try {
			localCon = DriverManager.getConnection(url, user, password);			
			st = localCon.createStatement();
			rs = st.executeQuery("SELECT VERSION()");

			if (rs.next()) {
				versionReturned = rs.getString(1);
			}

		} catch (SQLException ex) {

			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			return ex.getMessage();			

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (localCon != null) {
					localCon.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
				return ex.getMessage();
			}
		}

		return versionReturned;
	}
	
	public boolean testDBConnection(String url, String user, String password) {
		
		Connection localCon = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			localCon = DriverManager.getConnection(url, user, password);
			st = localCon.createStatement();
			rs = st.executeQuery("SELECT VERSION()");		

		} catch (SQLException ex) {

			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);     
			return false;

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (localCon != null) {
					localCon.close();
				}
				
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
				return false;
			}
		}

		return true;
	}

	public void createTable(String sqlStatement) {

		Statement st = null;

		try {
			st = con.createStatement();
			st.execute(sqlStatement);

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (st != null) {
					st.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	public String[][] getTableColumns(String sqlStatement) {
		String[][] tableColumns = null;
		int columnCount = 0;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = con.createStatement();
			rs = st.executeQuery(sqlStatement + " LIMIT 1");
			columnCount = rs.getMetaData().getColumnCount();
			tableColumns = new String[columnCount][2];

			for(int i = 0; i < columnCount; i++) {
				tableColumns[i][0] = rs.getMetaData().getColumnName(i + 1);
				tableColumns[i][1] = rs.getMetaData().getColumnTypeName(i + 1);
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
		return tableColumns;
	}	
			
	public void insertPanEdit(
			int editId,
			String editor,
			int oldRevisionId,
			int newRevisionId,
			String diffUrl,
			String revisionClass,
			int annotators,
			int totalAnnotators,
			Timestamp editTime,
			String editComment,
			int articleId,
			String articleTitle) {

		Statement st = null;
		ResultSet rs = null;    

		try {
			st = con.createStatement();

			String stm =
					"INSERT INTO " + config.getPanEditTable() + "_" + config.getPanCorpus() + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pst = con.prepareStatement(stm);
			pst.setInt(1, editId);
			pst.setString(2, editor);       
			pst.setInt(3, oldRevisionId);
			pst.setInt(4, newRevisionId);
			pst.setString(5, diffUrl);           
			pst.setString(6, revisionClass);
			pst.setInt(7, annotators);
			pst.setInt(8, totalAnnotators);
			pst.setTimestamp(9, editTime);           
			pst.setString(10, editComment);
			pst.setInt(11, articleId);
			pst.setString(12, articleTitle);           
			pst.executeUpdate();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	public void updatePanEdit(
			int editId,
			String revisionClass,
			int annotators,
			int totalAnnotators,
			int oldRevisionId,
			int newRevisionId) {

		Statement st = null;
		ResultSet rs = null;    

		try {
			st = con.createStatement();
			String stm = "";
			PreparedStatement pst = null;
			
	        switch (config.getPanCorpus()) {
	        case "all": case "training" :
				stm =	"UPDATE " + config.getPanEditTable() + "_" + config.getPanCorpus() + 
				" SET (revision_class, annotators, total_annotators) = (?, ?, ?)" +
				" WHERE edit_id = ?";
				
				pst = con.prepareStatement(stm);
				pst.setString(1, revisionClass);
				pst.setInt(2, annotators);
				pst.setInt(3, totalAnnotators);
				pst.setInt(4, editId);
				pst.executeUpdate();
	        	break;
	        case "test" :
	        	
	        	if (revisionClass.equals("R")) {
	        		revisionClass = "regular";
	        	} else {
	        		revisionClass = "vandalism";
	        	}
	        	
				stm =	"UPDATE " + config.getPanEditTable() + "_" + config.getPanCorpus() + 
				" SET (revision_class) = (?)" +
				" WHERE old_revision_id = ? AND new_revision_id = ?";

	        	pst = con.prepareStatement(stm);
				pst.setString(1, revisionClass);
				pst.setInt(2, oldRevisionId);
				pst.setInt(3, newRevisionId);
				pst.executeUpdate();
	        	break;
	        }	        

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	public void insertPanRevision(String tableName, String corpus, int revisionId, String revisionText) {

		Statement st = null;
		ResultSet rs = null;    
		
		if (corpus.equals("")) {			
			corpus = config.getPanCorpus();
		}
		
		try {
			st = con.createStatement();

			String stm = "";
			stm = "INSERT INTO " + tableName + "_" + corpus + " VALUES(?, ?)";                	

			PreparedStatement pst = con.prepareStatement(stm);
			pst.setInt(1, revisionId);
			pst.setString(2, revisionText);       
			pst.executeUpdate();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	public void insertPanFeature(
			int editId,
			Boolean vandalism,
			Boolean anonymous,
			int commentLength,
			int sizeIncrement,
			double sizeRatio,
			double upperToLowerRatio,
			double upperToAllRatio,
			double digitRatio,
			double nonAlphanumericRatio,
			double characterDiversity,    		
			double characterDistribution,    		
			double compressibility,    		
			double goodTokensFrequency,    		
			double goodTokensImpact,    		
			double averageTermFrequency,    		
			int longestWord,
			int longestCharacterSequence,
			double vulgarismFrequency,    		
			double vulgarismImpact,    		
			double pronounsFrequency,    		
			double pronounsImpact,    		
			double biasFrequency,    		
			double biasImpact,    		
			double sexFrequency,    		
			double sexImpact,    		
			double badFrequency,    		
			double badImpact,    		
			double allFrequency,    		
			double allImpact,			
			double relatedFrequency,				// new feature 1    		
			double relatedImpact,					// new feature 1
			int wordsInVandalicCluster1,			// new feature 2
			int wordsInVandalicCluster2,			// new feature 3			
			double pageRankFrequency,				// new feature 4    		
			double pageRankImpact,					// new feature 4
			double degreeFrequency,					// new feature 5    		
			double degreeImpact,					// new feature 5
			double eigenvectorCentralityFrequency,	// new feature 6    		
			double eigenvectorCentralityImpact,		// new feature 6
			double authorityFrequency,				// new feature 7    		
			double authorityImpact					// new feature 7
			) {

		Statement st = null;
		ResultSet rs = null;    

		try {
			st = con.createStatement();

			String stm =
				"INSERT INTO " + config.getPanFeatureTable() + "_" + config.getPanCorpus() + " VALUES(" + 
				"?, ?, ?, ?, ?, ?, ?, ?, ?, ?," + 
				"?, ?, ?, ?, ?, ?, ?, ?, ?, ?," + 
				"?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
				"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // new
			PreparedStatement pst = con.prepareStatement(stm);
			pst.setInt(1, editId);
			pst.setBoolean(2,vandalism);			
			pst.setBoolean(3,anonymous);
			pst.setInt(4, commentLength);
			pst.setInt(5, sizeIncrement);
			pst.setDouble(6, sizeRatio);
			pst.setDouble(7, upperToLowerRatio);
			pst.setDouble(8, upperToAllRatio);
			pst.setDouble(9, digitRatio);
			pst.setDouble(10, nonAlphanumericRatio);
			pst.setDouble(11, characterDiversity);
			pst.setDouble(12, characterDistribution);
			pst.setDouble(13, compressibility);
			pst.setDouble(14, goodTokensFrequency);
			pst.setDouble(15, goodTokensImpact);
			pst.setDouble(16, averageTermFrequency);
			pst.setInt(17, longestWord);
			pst.setInt(18, longestCharacterSequence);
			pst.setDouble(19, vulgarismFrequency);
			pst.setDouble(20, vulgarismImpact);
			pst.setDouble(21, pronounsFrequency);
			pst.setDouble(22, pronounsImpact);
			pst.setDouble(23, biasFrequency);
			pst.setDouble(24, biasImpact);
			pst.setDouble(25, sexFrequency);
			pst.setDouble(26, sexImpact);
			pst.setDouble(27, badFrequency);
			pst.setDouble(28, badImpact);
			pst.setDouble(29, allFrequency);
			pst.setDouble(30, allImpact);	
			pst.setDouble(31, relatedFrequency);
			pst.setDouble(32, relatedImpact);	
			pst.setDouble(33, wordsInVandalicCluster1);
			pst.setDouble(34, wordsInVandalicCluster2);
			pst.setDouble(35, pageRankFrequency);
			pst.setDouble(36, pageRankImpact);				
			pst.setDouble(37, degreeFrequency);
			pst.setDouble(38, degreeImpact);
			pst.setDouble(39, eigenvectorCentralityFrequency);
			pst.setDouble(40, eigenvectorCentralityImpact);			
			pst.setDouble(41, authorityFrequency);
			pst.setDouble(42, authorityImpact);			
			pst.executeUpdate();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	public void insertWordsDiff(
			int editId,
			String revisionClass,
			int oldRevisionId,
			int newRevisionId,
			String insertedWordsList,
			String insertedWordsFrequencyList,
			String deletedWordsList,
			String deletedWordsFrequencyList,
			int columnSorting) {

		Statement st = null;
		ResultSet rs = null;    

		try {
			st = con.createStatement();

			String stm =
					"INSERT INTO " + config.getPanWordsDiffTable() + "_" + config.getPanCorpus() +
					" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pst = con.prepareStatement(stm);

			pst.setInt(1, editId);
			pst.setString(2, revisionClass);
			pst.setInt(3, oldRevisionId);
			pst.setInt(4, newRevisionId);
			pst.setString(5, insertedWordsList);
			pst.setString(6, insertedWordsFrequencyList);
			pst.setString(7, deletedWordsList);
			pst.setString(8, deletedWordsFrequencyList);
			pst.setInt(9, columnSorting);
			
			pst.executeUpdate();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	public void insertDiffs(
			int editId,
			String revisionClass,
			int oldRevisionId,
			int newRevisionId,
			String insertedText,
			String insertedTextTokenized,
			String insertedTextPlain) {

		Statement st = null;
		ResultSet rs = null;    

		try {
			st = con.createStatement();

			String stm =
					"INSERT INTO " + config.getPanDiffTable() + "_" + config.getPanCorpus() +
					" VALUES(?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pst = con.prepareStatement(stm);

			pst.setInt(1, editId);
			pst.setString(2, revisionClass);
			pst.setInt(3, oldRevisionId);
			pst.setInt(4, newRevisionId);
			pst.setString(5, insertedText);
			pst.setString(6, insertedTextTokenized);
			pst.setString(7, insertedTextPlain);

			pst.executeUpdate();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}
	
	public void insertLinkedArticles(String articleTitle, String linkedArticleTitle, String wikitext) {

		Statement st = null;
		ResultSet rs = null;    

		try {
			st = con.createStatement();

			String stm = "";
			stm = "INSERT INTO " + config.getLinkedArticleTable() + "_" + config.getPanCorpus() + " VALUES(?, ?, ?)";                	

			PreparedStatement pst = con.prepareStatement(stm);
			pst.setString(1, articleTitle);       
			pst.setString(2, linkedArticleTitle);
			pst.setString(3, wikitext);			
			pst.executeUpdate();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}
	
	public void insertWpPageRevision(
			String pageTitle,
			int pageNs,
			int pageId,
			int revisionId,
			Timestamp revisionTimestamp,
			String revisionContributorUsername,
			int revisionContributorId,
			String revisionContributorIp,
			String revisionMinor,
			String revisionComment,
			String revisionText,
			String revisionSha1,
			String revisionModel,	
			String revisionFormat) {

		Statement st = null;
		ResultSet rs = null;    

		try {
			st = con.createStatement();

			String stm =
					"INSERT INTO " + config.getWpPageMetaHistoryTable() + 
					" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pst = con.prepareStatement(stm);

			pst.setString(1, pageTitle);
			pst.setInt(2, pageNs);
			pst.setInt(3, pageId);
			pst.setInt(4, revisionId);
			pst.setTimestamp(5, revisionTimestamp);
			pst.setString(6, revisionContributorUsername);
			pst.setInt(7, revisionContributorId);
			pst.setString(8, revisionContributorIp);                
			pst.setString(9, revisionMinor);
			pst.setString(10, revisionComment);
			pst.setString(11, revisionText);
			pst.setString(12, revisionSha1);
			pst.setString(13, revisionModel);
			pst.setString(14, revisionFormat);                

			pst.executeUpdate();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	public boolean existRecord(String sqlStatement) {

		boolean exist = false;
		
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			pst = con.prepareStatement(sqlStatement);
			rs = pst.executeQuery();

			if (rs.next()) {
				exist = true;
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
		
		return exist;
	}
	
	public void calcFeaturesFromEdits(boolean insertInFeatureTable, int editId, boolean onlyWithClass, int featureGroup) {

		WordClustering wordClustering1 = new WordClustering();
    	wordClustering1.loadVectorModel(config.getWordVectorModelFile());
    	wordClustering1.loadWordClusters("model//wordclusters1.bin");
		WordClustering wordClustering2 = new WordClustering();
    	wordClustering2.loadVectorModel(config.getWordVectorModelFile());
    	wordClustering2.loadWordClusters("model//wordclusters2.bin");		
		
		PreparedStatement pst = null;
		PreparedStatement pst2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		
		
		try {
			
			String tableName = config.getPanEditTable() + "_" + config.getPanCorpus();

			if (insertInFeatureTable) {				
				if (onlyWithClass) {					
					pst = con.prepareStatement("SELECT * FROM " + tableName + " WHERE revision_class != ''");
					pst2 = con.prepareStatement("SELECT COUNT(1) FROM " + tableName + " WHERE revision_class != ''");
				} else {
					pst = con.prepareStatement("SELECT * FROM " + tableName);					
					pst2 = con.prepareStatement("SELECT COUNT(1) FROM " + tableName);
				}
			} else {
				pst = con.prepareStatement("SELECT * FROM " + tableName + " WHERE edit_id = '" + editId + "'");
				pst2 = con.prepareStatement("SELECT COUNT(1) FROM " + tableName + " WHERE edit_id = '" + editId + "'");
			}
			
			rs = pst.executeQuery();
			rs2 = pst2.executeQuery();

			int rowCount = 0;
			if (rs2.next()) {
				rowCount = rs2.getInt(1);
			}				
			
			int actRow = 0;
			while (rs.next()) {
				actRow++;		
				
				PanEdit panEdit = rs2PanEdit(rs);
								
				panEdit.calcTextRepresentations(wordClustering1, wordClustering2);
									
				System.out.println(
					"Processing " + (int) ((float) actRow / (float) rowCount * 100) + 
					"% (" + rowCount + ") on " + config.getPanCorpus() + 
					" Edit: " +	panEdit.getEditId() + " " + panEdit.getArticleTitle());				
				
				if (insertInFeatureTable) {
					
					if (featureGroup == 0) 
						insertPanFeature(
								panEdit.getEditId(),
								panEdit.isVandalism(),
								panEdit.getAnonymousFeature(),
								panEdit.getCommentLengthFeature(),
								panEdit.getSizeIncrementFeature(),
								panEdit.getSizeRatioFeature(),
								panEdit.getUpperToLowerRatioFeature(),
								panEdit.getUpperToAllRatioFeature(),
								panEdit.getDigitRatioFeature(),
								panEdit.getNonAlphanumericRatioFeature(),
								panEdit.getCharacterDiversityFeature(),    		
								panEdit.getCharacterDistributionFeature(),    		
								panEdit.getCompressibilityFeature(),    		
								panEdit.getGoodTokensFrequencyFeature(),    		
								panEdit.getGoodTokensImpactFeature(),    		
								panEdit.getAverageTermFrequencyFeature(),   		
								panEdit.getLongestWordFeature(),
								panEdit.getLongestCharacterSequenceFeature(),
								panEdit.getVulgarismFrequencyFeature(),    		
								panEdit.getVulgarismImpactFeature(),    		
								panEdit.getPronounsFrequencyFeature(),    		
								panEdit.getPronounsImpactFeature(),    		
								panEdit.getBiasedFrequencyFeature(),    		
								panEdit.getBiasImpactFeature(),    		
								panEdit.getSexFrequencyFeature(),    		
								panEdit.getSexImpactFeature(),    		
								panEdit.getBadFrequencyFeature(),    		
								panEdit.getBadImpactFeature(),    		
								panEdit.getAllFrequencyFeature(),    		
								panEdit.getAllImpactFeature(),
								panEdit.getRelatedFrequencyFeature(),						// new feature 1
								panEdit.getRelatedImpactFeature(),							// new feature 1
								panEdit.getWordsInVandalicCluster1Feature(wordClustering1),	// new feature 2
								panEdit.getWordsInVandalicCluster2Feature(wordClustering2),	// new feature 3
								panEdit.getPageRankFrequencyFeature(),						// new feature 4
								panEdit.getPageRankImpactFeature(),							// new feature 4
								panEdit.getDegreeFrequencyFeature(),						// new feature 5
								panEdit.getDegreeImpactFeature(),							// new feature 5
								panEdit.getEigenvectorCentralityFrequencyFeature(),			// new feature 6
								panEdit.getEigenvectorCentralityImpactFeature(),			// new feature 6
								panEdit.getAuthorityFrequencyFeature(),						// new feature 7
								panEdit.getAuthorityImpactFeature()							// new feature 7
								);


					if (featureGroup == 1) 
						insertPanFeature(
								panEdit.getEditId(),
								panEdit.isVandalism(),
								false,
								0,
								0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0,
								0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,														// new feature 1
								0.0,														// new feature 1
								0,															// new feature 2
								0,															// new feature 3
								panEdit.getPageRankFrequencyFeature(),						// new feature 4
								panEdit.getPageRankImpactFeature(),							// new feature 4
								panEdit.getDegreeFrequencyFeature(),						// new feature 5
								panEdit.getDegreeImpactFeature(),							// new feature 5
								panEdit.getEigenvectorCentralityFrequencyFeature(),			// new feature 6
								panEdit.getEigenvectorCentralityImpactFeature(),			// new feature 6
								panEdit.getAuthorityFrequencyFeature(),						// new feature 7
								panEdit.getAuthorityImpactFeature()							// new feature 7
								);
					
					
					if (featureGroup == 2) 
						insertPanFeature(
								panEdit.getEditId(),
								panEdit.isVandalism(),
								false,
								0,
								0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0,
								0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								0.0,
								panEdit.getRelatedFrequencyFeature(),						// new feature 1
								panEdit.getRelatedImpactFeature(),							// new feature 1
								panEdit.getWordsInVandalicCluster1Feature(wordClustering1),	// new feature 2
								panEdit.getWordsInVandalicCluster2Feature(wordClustering2),	// new feature 3
								0.0,														// new feature 4
								0.0,														// new feature 4
								0.0,														// new feature 5
								0.0,														// new feature 5
								0.0,														// new feature 6
								0.0,														// new feature 6
								0.0,														// new feature 7
								0.0															// new feature 7
								);					

					if (featureGroup == 3) 
						insertPanFeature(
								panEdit.getEditId(),
								panEdit.isVandalism(),
								panEdit.getAnonymousFeature(),
								panEdit.getCommentLengthFeature(),
								panEdit.getSizeIncrementFeature(),
								panEdit.getSizeRatioFeature(),
								panEdit.getUpperToLowerRatioFeature(),
								panEdit.getUpperToAllRatioFeature(),
								panEdit.getDigitRatioFeature(),
								panEdit.getNonAlphanumericRatioFeature(),
								panEdit.getCharacterDiversityFeature(),    		
								panEdit.getCharacterDistributionFeature(),    		
								panEdit.getCompressibilityFeature(),    		
								panEdit.getGoodTokensFrequencyFeature(),    		
								panEdit.getGoodTokensImpactFeature(),    		
								panEdit.getAverageTermFrequencyFeature(),   		
								panEdit.getLongestWordFeature(),
								panEdit.getLongestCharacterSequenceFeature(),
								panEdit.getVulgarismFrequencyFeature(),    		
								panEdit.getVulgarismImpactFeature(),    		
								panEdit.getPronounsFrequencyFeature(),    		
								panEdit.getPronounsImpactFeature(),    		
								panEdit.getBiasedFrequencyFeature(),    		
								panEdit.getBiasImpactFeature(),    		
								panEdit.getSexFrequencyFeature(),    		
								panEdit.getSexImpactFeature(),    		
								panEdit.getBadFrequencyFeature(),    		
								panEdit.getBadImpactFeature(),    		
								panEdit.getAllFrequencyFeature(),    		
								panEdit.getAllImpactFeature(),
								0.0,															// new feature 1
								0.0,															// new feature 1
								0,																// new feature 2
								0,																// new feature 3
								0.0,															// new feature 4
								0.0,															// new feature 4
								0.0,															// new feature 5
								0.0,															// new feature 5
								0.0,															// new feature 6
								0.0,															// new feature 6
								0.0,															// new feature 7
								0.0																// new feature 7
								);

				}					
			}


		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
	}
	
	public void updateWordDiffTable(boolean onlyWithClass) {

		DiffUtils diffUtils = new DiffUtils();
		FileUtils fileUtils = new FileUtils();
		
		HashMap<Integer,Integer> columnSortMap = fileUtils.file2MapInt("config//sort_" + config.getPanWordsDiffTable() + "_" + config.getPanCorpus() + ".txt");		
		
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {			
			
			if (onlyWithClass) {
				pst = con.prepareStatement("SELECT * FROM " + config.getPanEditTable() + "_" + config.getPanCorpus() + 
					" WHERE revision_class != ''");
			} else {
				pst = con.prepareStatement("SELECT * FROM " + config.getPanEditTable() + "_" + config.getPanCorpus());					
			}

			rs = pst.executeQuery();						
			
			int i = 0;

			while (rs.next()) {
				
				int editId = rs.getInt(1);
				
				String sqlStatement2 = "SELECT edit_id FROM " + config.getPanWordsDiffTable() + "_" + config.getPanCorpus() +
					" WHERE edit_id = " + editId;											
				
				if (!existRecord(sqlStatement2)) {					
					int oldRevisionId = rs.getInt(3); 
					int newRevisionId = rs.getInt(4);				
					String oldPlainTextRevisionText = 
						getRevisionText(config.getPlainTextPanArticleRevTable() + "_" + config.getPanCorpus(), oldRevisionId);			
					String newPlainTextRevisionText = 
						getRevisionText(config.getPlainTextPanArticleRevTable() + "_" + config.getPanCorpus(), newRevisionId);		
					
					diffUtils.calcInsertedAndDeletedWords(oldPlainTextRevisionText.toLowerCase(), newPlainTextRevisionText.toLowerCase());				
					
					int columnSort = 0;
					if (columnSortMap.get(editId) != null) {
						columnSort = columnSortMap.get(editId);
					}
					
					insertWordsDiff(
							editId,
							rs.getString(6),		// revisionClass 
							oldRevisionId,
							newRevisionId, 
							diffUtils.getInsertedWordsList(), 
							diffUtils.getInsertedWordsFrequencyList(), 
							diffUtils.getDeletedWordsList(), 
							diffUtils.getDeletedWordsFrequencyList(),
							columnSort);
				}
				
				i++;
				System.out.println("Words Diff Edit Id: " + editId + " (" + config.getPanCorpus() + ") / processed: " + i);
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
	}
	
	public void updateDiffTable(boolean onlyWithClass) {

		DiffUtils diffUtils = new DiffUtils();
		Tokenizer tokenizer = new Tokenizer();
		PlainTextExtractor plainTextExtractor = new PlainTextExtractor();
		
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {			
			
			if (onlyWithClass) {
				pst = con.prepareStatement("SELECT * FROM " + config.getPanEditTable() + "_" + config.getPanCorpus() + 
					" WHERE revision_class != ''");
			} else {
				pst = con.prepareStatement("SELECT * FROM " + config.getPanEditTable() + "_" + config.getPanCorpus());					
			}

			rs = pst.executeQuery();						
			
			int i = 0;

			while (rs.next()) {
				
				int editId = rs.getInt(1);
				
				String sqlStatement2 = "SELECT edit_id FROM " + config.getPanDiffTable() + "_" + config.getPanCorpus() +
					" WHERE edit_id = " + editId;											
				
				if (!existRecord(sqlStatement2)) {					
					int oldRevisionId = rs.getInt(3); 
					int newRevisionId = rs.getInt(4);				
					String oldRevisionText = 
						getRevisionText(config.getPanArticleRevTable() + "_" + config.getPanCorpus(), oldRevisionId);			
					String newRevisionText = 
						getRevisionText(config.getPanArticleRevTable() + "_" + config.getPanCorpus(), newRevisionId);							
					
					String insertedText = diffUtils.calcInsertedText(oldRevisionText, newRevisionText);					
					String insertedTextTokenized = tokenizer.tokenize(insertedText, "config//wikitokens.txt");
					String insertedTextPlain = plainTextExtractor.extractWithLines(insertedTextTokenized, "config//wikitokens.txt", false);
									
					insertDiffs(
							editId,
							rs.getString(6),		// revisionClass 
							oldRevisionId,
							newRevisionId, 
							insertedText,
							insertedTextTokenized,
							insertedTextPlain);
				}
				
				i++;
				System.out.println("Diff Edit Id: " + editId + " (" + config.getPanCorpus() + ") / processed: " + i);
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
	}
	
	public String getRevisionText(String tableName, int revisionId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		String revisionText = "";		

		try {

			pst = con.prepareStatement("SELECT revision_text FROM " + tableName + 
				" WHERE revision_id = " + revisionId);
			rs = pst.executeQuery();						
			
			if (rs.next()) {
				revisionText = rs.getString(1);				
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
		
		return revisionText;
	}
	
	public String createTokenizedRevisions() {
		
		Tokenizer tokenizer = new Tokenizer();
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		int revisionId = 0;
		String revisionText = "";
		int i = 0;
		
		try {
			
			String sqlStatement1 = "SELECT revision_id FROM " + config.getPanArticleRevTable() + "_" + config.getPanCorpus();
			
			String sqlStatement2 = 
				"SELECT " + config.getPanArticleRevTable() + "_" + config.getPanCorpus() + ".revision_id" +
					" FROM " + config.getPanArticleRevTable() + "_" + config.getPanCorpus() + "," +  
						config.getPanEditTable() + "_" + config.getPanCorpus() +
					" WHERE (" + config.getPanArticleRevTable() + "_" + config.getPanCorpus() + ".revision_id = " + 
						config.getPanEditTable() + "_" + config.getPanCorpus() + ".old_revision_id OR " +
							config.getPanArticleRevTable() + "_" + config.getPanCorpus() + ".revision_id = " +
							config.getPanEditTable() + "_" + config.getPanCorpus() + ".new_revision_id) AND " +
						config.getPanEditTable() + "_" + config.getPanCorpus() + ".revision_class != '';";		
			
			if (config.getOnlyWithClass().equals("true")){
				pst = con.prepareStatement(sqlStatement2);
			} else {
				pst = con.prepareStatement(sqlStatement1);				
			}
			
			rs = pst.executeQuery();						
			
			
			while (rs.next()) {
				
				revisionId = rs.getInt(1);								
				revisionText = getRevisionText(config.getPanArticleRevTable() + "_" + config.getPanCorpus(), revisionId);
				
				String sqlStatement3 = "SELECT revision_id FROM " + config.getTokenizedPanArticleRevTable() + "_" + config.getPanCorpus() +
						" WHERE revision_id = " + revisionId;				
				
				if (!existRecord(sqlStatement3)) {
					String tokenizedRevisionText = tokenizer.tokenize(revisionText,"config//wikitokens.txt");				
					insertPanRevision(config.getTokenizedPanArticleRevTable(), "", revisionId, tokenizedRevisionText);					
				}
				
				i++;
				System.out.println("Tokenized Revision Id: " + revisionId + " (" + config.getPanCorpus() + ") / processed: " + i);
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
		
		return revisionText;
	}
	
	public String createPlainTextRevisions() {
		
		PlainTextExtractor plainTextExtractor = new PlainTextExtractor();
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		int revisionId = 0;
		String revisionText = "";
		int i = 0;

		try {
			
			String sqlStatement1 = "SELECT revision_id FROM " + config.getTokenizedPanArticleRevTable() + "_" + config.getPanCorpus();

			pst = con.prepareStatement(sqlStatement1);
			rs = pst.executeQuery();						
			
			
			while (rs.next()) {
				
				revisionId = rs.getInt(1);
				revisionText = getRevisionText(config.getTokenizedPanArticleRevTable() + "_" + config.getPanCorpus(), revisionId);
				
				String sqlStatement2 = "SELECT revision_id FROM " + config.getPlainTextPanArticleRevTable() + "_" + config.getPanCorpus() +
						" WHERE revision_id = " + revisionId;				
								
				if (!existRecord(sqlStatement2)) {
					String plainTextRevisionText = plainTextExtractor.extractWithLines(revisionText,"config//wikitokens.txt",true);				
					insertPanRevision(config.getPlainTextPanArticleRevTable(), "", revisionId, plainTextRevisionText);					
				}
				
				i++;
				System.out.println("Plain Text Revision Id: " + revisionId + " (" + config.getPanCorpus() + ")  / processed: " + i);
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
		
		return revisionText;
	}
	
	public void createUnifiedPlainTextOfRevisions() {		
		PreparedStatement pst = null;
		ResultSet rs = null;
		int revisionId = 0;
		String plainTextRevisionText = "";
		int i = 0;

		try {
			
			String sqlStatement1 = "INSERT INTO " + config.getPlainTextPanArticleRevTable() + "_all " + 
					"SELECT * FROM " + config.getPlainTextPanArticleRevTable() + "_training";
			pst = con.prepareStatement(sqlStatement1);
			pst.execute();									
			pst.close();
					
			sqlStatement1 = "SELECT revision_id FROM " + config.getPlainTextPanArticleRevTable() + "_test";
			pst = con.prepareStatement(sqlStatement1);
			rs = pst.executeQuery();									
			
			while (rs.next()) {
				
				revisionId = rs.getInt(1);
				plainTextRevisionText = getRevisionText(config.getPlainTextPanArticleRevTable() + "_test", revisionId);
				
				String sqlStatement2 = "SELECT revision_id FROM " + config.getPlainTextPanArticleRevTable() + "_all" +
						" WHERE revision_id = " + revisionId;				
								
				if (!existRecord(sqlStatement2)) {			
					insertPanRevision(config.getPlainTextPanArticleRevTable(), "all", revisionId, plainTextRevisionText);					
				}
				
				i++;
				System.out.println("Merging Plain Text Revision Id: " + revisionId + " (all)  / processed: " + i);
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
	}
	
	public void updateLinkedArticles() {
	   	
		WikipediaApiQuerys wikipediaApiQuerys = new WikipediaApiQuerys();
	   	
		PreparedStatement pst = null;
		ResultSet rs = null;
		String articleTitle = "";
		int i = 0;
		
		try {
			
			String sqlStatement1 = "SELECT article_title FROM " + config.getPanEditTable() + "_" + config.getPanCorpus();
			pst = con.prepareStatement(sqlStatement1);							
			rs = pst.executeQuery();						
			
			
			while (rs.next()) {				
				
				articleTitle = rs.getString(1);
				
				i++;
				System.out.println("Article title: " + articleTitle + " / processed: " + i);
								
				String sqlStatement2 = "SELECT article_title FROM " + config.getLinkedArticleTable() + "_" + config.getPanCorpus() +
						" WHERE article_title = '" + articleTitle + "'";				
				
				if (!existRecord(sqlStatement2)) {
			    	
					
					String articleTitleEncoded = URLEncoder.encode(articleTitle,"UTF-8"); 					
					ArrayList<String> linkList = wikipediaApiQuerys.getPageLinks(articleTitleEncoded);			    	
			    	
					int size = linkList.size();
			    	int max = size;
			    	if (max > 5) {
			    		max = 5;
			    	}
			    	
			    	double k = 0.0;
								    	   	
			    	for (int j = 0; j < max; j++) {
			    		
			    		String linkedArticleTitle = "";
			    					    		
			    		if (max < 5) {			    		
				    		linkedArticleTitle = linkList.get(j);
			    		} else {
			    			if (k == 0) {
			    				linkedArticleTitle = linkList.get(j);
			    			} else {
			    				double l = (double) (size - 1) * k; 
			    				linkedArticleTitle = linkList.get((int) l);
			    			}
			    		}
			    		
			    		k = k + 0.25;
			    		
		    			String linkedArticleTitleEncoded = URLEncoder.encode(linkedArticleTitle,"UTF-8");			    						    			
		    			String wikitext = wikipediaApiQuerys.getWikitext(linkedArticleTitleEncoded);			    			
		    			insertLinkedArticles(articleTitle, linkedArticleTitle, wikitext);			    
		    			
		    			System.out.println("=> Found link: " + linkedArticleTitle);
			    	}					
				}				
			}

		} catch (SQLException | UnsupportedEncodingException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
		
	}

	public void writeArff(File outputFile) {

		String tableName = config.getPanFeatureTable() + "_" + config.getPanCorpus() + "_all";
		int classColumn = 1;
		
		Statement st = null;
		ResultSet rs = null;
//		String sqlStatement = "SELECT * FROM " + tableName;
		String sqlStatement = "SELECT * FROM " + tableName +  " ORDER BY edit_id";

		FileWriter fileWriter = null;
		PrintWriter printWriter = null;

		try 
		{
			openConnection();	       			
			String[][] tableColumns = getTableColumns(sqlStatement);  			

			fileWriter = new FileWriter(outputFile);
			printWriter = new PrintWriter(fileWriter);        	          
			printWriter.println("@relation " + tableName);
			String wekaDatatype = "";

			for (int i = 1; i < tableColumns.length; i++) { // primary key excluded

				wekaDatatype = "";
				switch (tableColumns[i][1]) {
				case "int4" :
					wekaDatatype = "numeric";
					break;
				case "float8" :
					wekaDatatype = "numeric";
					break;
				case "bool" :
					wekaDatatype = "{f,t}";
					break;
				case "varchar" :
					wekaDatatype = "string";
					break;
				case "timestamptz" :
					wekaDatatype = "date 'yyyy-MM-dd HH:mm:ss+HH'";
					break;            			            			
				}

				tableColumns[i][1] = wekaDatatype;

				if (i != classColumn) {
					printWriter.println("@attribute " + tableColumns[i][0] + " " + tableColumns[i][1]);					
				}
			}

			printWriter.println("@attribute " + tableColumns[classColumn][0] + " " + tableColumns[classColumn][1]);
			printWriter.println("@data");

			try {
				st = con.createStatement();
				rs = st.executeQuery(sqlStatement);

				while(rs.next()) {
					String dataLine = "";
					for(int i = 2; i <= rs.getMetaData().getColumnCount(); i++) { // primary key excluded
						if ((i - 1) != classColumn) {
							if (dataLine.equals("")) {
								dataLine = rs.getString(i);
							} else {
								dataLine = dataLine + "," + rs.getString(i);						
							}
						}
					}
					dataLine = dataLine + "," + rs.getString(classColumn + 1);
					printWriter.println(dataLine);
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.SEVERE, ex.getMessage(), ex);

			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (st != null) {
						st.close();
					}
				} catch (SQLException ex) {
					Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
					lgr.log(Level.WARNING, ex.getMessage(), ex);
				}
			}							

			closeConnection();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fileWriter)
					fileWriter.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}	
	
	public String getDocFromField(String sqlStatement, int size, int offset) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		String docText = "";		

		try {

			System.out.println(sqlStatement + " LIMIT " + size + " OFFSET " + offset);
			pst = con.prepareStatement(sqlStatement + " LIMIT " + size + " OFFSET " + offset);
			rs = pst.executeQuery();						
			
			while(rs.next()) {
				docText = docText + " " + rs.getString(1);	
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}	
		
		return docText;
	}

	
	
	// --- ResultSets to Objects ---
	
	public PanEdit rs2PanEdit(ResultSet rs) {
		
		PanEdit panEdit = null;
		
		try {
			panEdit = new PanEdit(
				rs.getInt(1),			// editId
				rs.getString(2),		// editor       
				rs.getInt(3),			// oldRevisionId
				rs.getInt(4),			// newRevisionId
				rs.getString(5),		// diffUrl           
				rs.getString(6),		// revisionClass
				rs.getInt(7),			// annotators
				rs.getInt(8),			// totalAnnotators
				rs.getTimestamp(9),		// editTime           
				rs.getString(10),		// editComment
				rs.getInt(11),			// articleId
				rs.getString(12));		// articleTitle
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		panEdit.setOldRevisionText(getRevisionText(
			config.getPanArticleRevTable() + "_" + config.getPanCorpus(), panEdit.getOldRevisionId()));
		panEdit.setNewRevisionText(getRevisionText(
			config.getPanArticleRevTable() + "_" + config.getPanCorpus(), panEdit.getNewRevisionId()));		
		panEdit.setOldTokenizedRevisionText(getRevisionText(
			config.getTokenizedPanArticleRevTable() + "_" + config.getPanCorpus(), panEdit.getOldRevisionId()));
		panEdit.setNewTokenizedRevisionText(getRevisionText(
			config.getTokenizedPanArticleRevTable() + "_" + config.getPanCorpus(), panEdit.getNewRevisionId()));		
		
		return panEdit;
	}
	

	// --- sql statements ---

	public String getCreatePanEditTableStatement() {
		String sqlStatement = 
				"DROP TABLE IF EXISTS " + config.getPanEditTable() + "_" + config.getPanCorpus() + ";" +	        		
				"CREATE TABLE IF NOT EXISTS " + config.getPanEditTable() + "_" + config.getPanCorpus() + 
						"(" +
						"  edit_id integer NOT NULL," +
						"  editor character varying(100)," +
						"  old_revision_id integer," +
						"  new_revision_id integer," +
						"  diff_url character varying(500)," +
						"  revision_class character varying(10)," +
						"  annotators integer," +
						"  total_annotators integer," +
						"  edit_tim timestamp with time zone," +
						"  edit_comment character varying(5000)," +
						"  article_id integer," +
						"  article_title character varying(500)," +
						"  CONSTRAINT " + config.getPanEditTable() + "_" + config.getPanCorpus() + "_pk PRIMARY KEY (edit_id)" +
						")" +
						"WITH (OIDS=FALSE); " +
						"ALTER TABLE " + config.getPanEditTable() + "_" + config.getPanCorpus() + " OWNER TO " + config.getDatabaseUser() + ";";

		return sqlStatement;
	}

	public String getCreatePanArticleRevStatement(String tableName, String corpus) {
		
		if (corpus.equals("")) {			
			corpus = config.getPanCorpus();
		}
		
		String sqlStatement = 
				//"DROP TABLE IF EXISTS " + tableName + "_" + corpus + ";" +	        		
				"CREATE TABLE IF NOT EXISTS " + tableName + "_" + corpus + 
						"(" +
						"  revision_id integer NOT NULL," +
						"  revision_text text," +
						"  CONSTRAINT " + tableName + "_" + corpus + "_pk PRIMARY KEY (revision_id)" +
						") " +
						"WITH (OIDS=FALSE);" +
						"ALTER TABLE " + tableName + "_" + corpus + " OWNER TO " + config.getDatabaseUser() + ";";

		return sqlStatement;
	}
		
	
	public String getPanArticleRevViewStatement(String viewName) {
		String sqlStatement =
			"CREATE MATERIALIZED VIEW " + viewName + " AS " + 
				"SELECT DISTINCT pan_article_revision_all_test.* FROM pan_article_revision_all_test, pan_edit_test " + 
				"WHERE pan_edit_test.revision_class != '' AND " +
				"(pan_edit_test.old_revision_id = pan_article_revision_all_test.revision_id OR " + 
				 "pan_edit_test.new_revision_id = pan_article_revision_all_test.revision_id) " +
				"ORDER BY pan_article_revision_all_test.revision_id;" +
			"ALTER TABLE " + viewName + " OWNER TO postgres;" + 
			"GRANT ALL ON TABLE " + viewName + " TO postgres;" +
			"GRANT SELECT ON TABLE " + viewName + " TO public;";
	
		return sqlStatement;
	}

	public String getCreateWpPageMetaHistoryStatement() {
				
		String sqlStatement = 
				"DROP TABLE IF EXISTS " + config.getWpPageMetaHistoryTable() + ";" +
				"CREATE TABLE IF NOT EXISTS " + config.getWpPageMetaHistoryTable() + 
						"(" +
						"  page_title character varying(500)," +
						"  page_ns integer," +
						"  page_id integer," +	    		
						"  revision_id integer NOT NULL," +
						"  revision_timestamp timestamp with time zone," +	    		
						"  revision_contributor_username character varying(100)," +
						"  revision_contributor_id integer," +
						"  revision_contributor_ip character varying(100)," +	    		
						"  revision_minor character varying(10)," +
						"  revision_comment character varying(5000)," +	    		
						"  revision_text text," +
						"  revision_sha1 character varying(50)," +
						"  revision_model character varying(50)," +
						"  revision_format character varying(50)," +
						"  CONSTRAINT " + config.getWpPageMetaHistoryTable() + "_pk PRIMARY KEY (page_id, revision_id)" +
						")" +
						"WITH (OIDS=FALSE);" +
						"ALTER TABLE " + config.getWpPageMetaHistoryTable() + " OWNER TO " + config.getDatabaseUser() + ";";	

		return sqlStatement;
	}

	public String getCreateFeatureStatement() {
		String sqlStatement = 
				"DROP TABLE IF EXISTS " + config.getPanFeatureTable() + "_" + config.getPanCorpus() + ";" +
				"CREATE TABLE IF NOT EXISTS " + config.getPanFeatureTable() + "_" + config.getPanCorpus() + 
						"(" +
						"  edit_id integer NOT NULL," +
						"  vandalism boolean," +
						"  anonymous boolean," +
						"  comment_length integer," +
						"  size_increment integer," +
						"  size_ratio double precision," +
						"  upper_to_lower_ratio double precision," +
						"  upper_to_all_ratio double precision," +
						"  digit_ratio double precision," +
						"  non_alphanumeric_ratio double precision," +
						"  character_diversity double precision," +    		
						"  character_distribution double precision," +    		
						"  compressibility double precision," +    		
						"  good_tokens_frequency double precision," +    		
						"  good_tokens_impact double precision," +
						"  average_term_frequency double precision," +    		
						"  longest_word integer," +
						"  longest_character_sequence integer," +
						"  vulgarism_frequency double precision," +    		
						"  vulgarism_impact double precision," +    		
						"  pronouns_frequency double precision," +    		
						"  pronouns_impact double precision," +    		
						"  bias_frequency double precision," +    		
						"  bias_impact double precision," +    		
						"  sex_frequency double precision," +    		
						"  sex_impact double precision," +    		
						"  bad_frequency double precision," +    		
						"  bad_impact double precision," +    		
						"  all_frequency double precision," +    		
						"  all_impact double precision," +
						"  related_frequency double precision," +					// new feature 1    		
						"  related_impact double precision," +						// new feature 1    								
						"  words_in_vandalic_cluster1 integer," +					// new feature 2						
						"  words_in_vandalic_cluster2 integer," +					// new feature 3						
						"  page_rank_frequency double precision," +					// new feature 4    		
						"  page_rank_impact double precision," +					// new feature 4    								
						"  degree_frequency double precision," +					// new feature 5    		
						"  degree_impact double precision," +						// new feature 5    								
						"  eigenvector_centrality_frequency double precision," +	// new feature 6    		
						"  eigenvector_centrality_impact double precision," +		// new feature 6    	
						"  authority_frequency double precision," +					// new feature 7    		
						"  authority_impact double precision," +					// new feature 7    	
						"  CONSTRAINT " + config.getPanFeatureTable() + "_" + config.getPanCorpus() + "_pk PRIMARY KEY (edit_id)" +
						")" +
						"WITH (OIDS=FALSE);" +
						"ALTER TABLE " + config.getPanFeatureTable() + "_" + config.getPanCorpus() + 
						" OWNER TO " + config.getDatabaseUser() + ";";	

		return sqlStatement;
	}

	public String getCreateWordsDiffTableStatement() {
		String sqlStatement =         		
					//"DROP TABLE IF EXISTS " + config.getPanWordsDiffTable() + "_" + config.getPanCorpus() +  ";" +
					"CREATE TABLE IF NOT EXISTS " + config.getPanWordsDiffTable() + "_" + config.getPanCorpus() + 
						"(" +
						"  edit_id integer NOT NULL," +
						"  revision_class text," + 
						"  old_revision_id integer," +
						"  new_revision_id integer," +
						"  inserted_words_list text," +
						"  inserted_words_frequency_list text," +
						"  deleted_words_list text," +
						"  deleted_words_frequency_list text," +						
						"  sorting_column integer," +						
						"  CONSTRAINT " + config.getPanWordsDiffTable() + "_" + config.getPanCorpus() + "_pk PRIMARY KEY (edit_id)" +
						")" +
						"WITH (OIDS=FALSE); " +
						"ALTER TABLE " + config.getPanWordsDiffTable() + "_" + config.getPanCorpus() + " OWNER TO " + config.getDatabaseUser() + ";";
		
		return sqlStatement;
	}	
	

	public String getCreateDiffTableStatement() {
		String sqlStatement =         		
					//"DROP TABLE IF EXISTS " + config.getPanDiffTable() + "_" + config.getPanCorpus() +  ";" +
					"CREATE TABLE IF NOT EXISTS " + config.getPanDiffTable() + "_" + config.getPanCorpus() + 
						"(" +
						"  edit_id integer NOT NULL," +
						"  revision_class text," + 
						"  old_revision_id integer," +
						"  new_revision_id integer," +
						"  inserted_text text," +
						"  inserted_text_tokenized text," +
						"  inserted_text_plain text," +						
						"  CONSTRAINT " + config.getPanDiffTable() + "_" + config.getPanCorpus() + "_pk PRIMARY KEY (edit_id)" +
						")" +
						"WITH (OIDS=FALSE); " +
						"ALTER TABLE " + config.getPanDiffTable() + "_" + config.getPanCorpus() + " OWNER TO " + config.getDatabaseUser() + ";";
		
		return sqlStatement;
	}
	
	public String getCreateLinkedArticleStatement() {
		
		String sqlStatement = 
				//"DROP TABLE IF EXISTS " + tableName + "_" + config.getPanCorpus() + ";" +	        		
				"CREATE TABLE IF NOT EXISTS " + config.getLinkedArticleTable() + "_" + config.getPanCorpus() + 
						"(" +
						"  article_title character varying(500) NOT NULL," +
						"  linked_article_title character varying(500) NOT NULL," +						
						"  wikitext text," +
						"  CONSTRAINT " + config.getLinkedArticleTable() + "_" + config.getPanCorpus() + "_pk PRIMARY KEY (article_title, linked_article_title)" +
						") " +
						"WITH (OIDS=FALSE);" +
						"ALTER TABLE " + config.getLinkedArticleTable() + "_" + config.getPanCorpus() + " OWNER TO " + config.getDatabaseUser() + ";";

		return sqlStatement;
	}
	
	public String getCreateTrainingFeatureViewStatement() {
		
		String sqlStatement = 
			"DROP MATERIALIZED VIEW IF EXISTS feature_training_all; " +
			"CREATE MATERIALIZED VIEW feature_training_all AS " +  
			"SELECT " +
				"feature_training.edit_id, " +
				"feature_training.vandalism, " +
			    
			    "feature_training.anonymous, " +
			    "feature_training.comment_length, " +
			    "feature_training.size_increment, " +
			    "feature_training.size_ratio, " +
			    "feature_training.upper_to_lower_ratio, " +
			    "feature_training.upper_to_all_ratio, " +
			    "feature_training.digit_ratio, " +
			    "feature_training.non_alphanumeric_ratio, " +
			    "feature_training.character_diversity, " +
			    "feature_training.character_distribution, " +
			    "feature_training.compressibility, " +
			    "feature_training.good_tokens_frequency, " +
			    "feature_training.good_tokens_impact, " +
			    "feature_training.average_term_frequency, " +
			    "feature_training.longest_word, " +
			    "feature_training.longest_character_sequence, " +
			    "feature_training.vulgarism_frequency, " +
			    "feature_training.vulgarism_impact, " +
			    "feature_training.pronouns_frequency, " +
			    "feature_training.pronouns_impact, " +
			    "feature_training.bias_frequency, " +
			    "feature_training.bias_impact, " +
			    "feature_training.sex_frequency, " +
			    "feature_training.sex_impact, " +
			    "feature_training.bad_frequency, " +
			    "feature_training.bad_impact, " +
			    "feature_training.all_frequency, " +
			    "feature_training.all_impact, " +
			    
			    "feature_training.degree_frequency, " +
			    "feature_training.degree_impact, " +
			    "feature_training.eigenvector_centrality_frequency, " +
			    "feature_training.eigenvector_centrality_impact, " +
			    "feature_training.page_rank_frequency, " +
			    "feature_training.page_rank_impact, " +
			    "feature_training.authority_frequency, " +
			    "feature_training.authority_impact, " +
				
			    "feature_training.related_frequency, " +
			    "feature_training.related_impact, " +
			    "feature_training.words_in_vandalic_cluster1, " +
			    "feature_training.words_in_vandalic_cluster2, " +
			    
			    "sda_train25.f0, " +
			    "sda_train25.f1, " +
			    "sda_train25.f2, " +
			    "sda_train25.f3, " +
			    "sda_train25.f4, " +
			    "sda_train25.f5, " +
			    "sda_train25.f6, " +
			    "sda_train25.f7, " +
			    "sda_train25.f8, " +
			    "sda_train25.f9, " +
			    "sda_train25.f10, " +
			    "sda_train25.f11, " +
			    "sda_train25.f12, " +
			    "sda_train25.f13, " +
			    "sda_train25.f14, " +
			    "sda_train25.f15, " +
			    "sda_train25.f16, " +
			    "sda_train25.f17, " +
			    "sda_train25.f18, " +
			    "sda_train25.f19, " +
			    "sda_train25.f20, " +
			    "sda_train25.f21, " +
			    "sda_train25.f22, " +
			    "sda_train25.f23, " +
			    "sda_train25.f24 " +
			    
		    "FROM feature_training, sda_train25 " +
		    "WHERE feature_training.edit_id = sda_train25.edit_id " +
		    "ORDER BY feature_training.edit_id " +
		    "WITH DATA; " +
		    "ALTER TABLE feature_training_all " +
		    "OWNER TO " + config.getDatabaseUser() + "; " +
		    "GRANT ALL ON TABLE feature_training_all TO " + config.getDatabaseUser() + "; " +
		    "GRANT SELECT ON TABLE feature_training_all TO public;";
		
		return sqlStatement;	
	}
	
	public String getCreateTestFeatureViewStatement() {
		
		String sqlStatement = 
			"DROP MATERIALIZED VIEW IF EXISTS feature_test_all; " +
			"CREATE MATERIALIZED VIEW feature_test_all AS " +  
			"SELECT " +
				"feature_test.edit_id, " +
				"feature_test.vandalism, " +
			
			    "feature_test.anonymous, " +
			    "feature_test.comment_length, " +
			    "feature_test.size_increment, " +
			    "feature_test.size_ratio, " +
			    "feature_test.upper_to_lower_ratio, " +
			    "feature_test.upper_to_all_ratio, " +
			    "feature_test.digit_ratio, " +
			    "feature_test.non_alphanumeric_ratio, " +
			    "feature_test.character_diversity, " +
			    "feature_test.character_distribution, " +
			    "feature_test.compressibility, " +
			    "feature_test.good_tokens_frequency, " +
			    "feature_test.good_tokens_impact, " +
			    "feature_test.average_term_frequency, " +
			    "feature_test.longest_word, " +
			    "feature_test.longest_character_sequence, " +
			    "feature_test.vulgarism_frequency, " +
			    "feature_test.vulgarism_impact, " +
			    "feature_test.pronouns_frequency, " +
			    "feature_test.pronouns_impact, " +
			    "feature_test.bias_frequency, " +
			    "feature_test.bias_impact, " +
			    "feature_test.sex_frequency, " +
			    "feature_test.sex_impact, " +
			    "feature_test.bad_frequency, " +
			    "feature_test.bad_impact, " +
			    "feature_test.all_frequency, " +
			    "feature_test.all_impact, " +
			    
			    "feature_test.degree_frequency, " +
			    "feature_test.degree_impact, " +
			    "feature_test.eigenvector_centrality_frequency, " +
			    "feature_test.eigenvector_centrality_impact, " +
			    "feature_test.page_rank_frequency, " +
			    "feature_test.page_rank_impact, " +
			    "feature_test.authority_frequency, " +
			    "feature_test.authority_impact, " +
			    
			    "feature_test.related_frequency, " +
			    "feature_test.related_impact, " +
			    "feature_test.words_in_vandalic_cluster1, " +
			    "feature_test.words_in_vandalic_cluster2, " +
			    
			    "sda_test25.f0, " +
			    "sda_test25.f1, " +
			    "sda_test25.f2, " +
			    "sda_test25.f3, " +
			    "sda_test25.f4, " +
			    "sda_test25.f5, " +
			    "sda_test25.f6, " +
			    "sda_test25.f7, " +
			    "sda_test25.f8, " +
			    "sda_test25.f9, " +
			    "sda_test25.f10, " +
			    "sda_test25.f11, " +
			    "sda_test25.f12, " +
			    "sda_test25.f13, " +
			    "sda_test25.f14, " +
			    "sda_test25.f15, " +
			    "sda_test25.f16, " +
			    "sda_test25.f17, " +
			    "sda_test25.f18, " +
			    "sda_test25.f19, " +
			    "sda_test25.f20, " +
			    "sda_test25.f21, " +
			    "sda_test25.f22, " +
			    "sda_test25.f23, " +
			    "sda_test25.f24 " +

			"FROM feature_test, sda_test25 " +
		    "WHERE feature_test.edit_id = sda_test25.edit_id " +
		    "ORDER BY feature_test.edit_id " +
		    "WITH DATA; " +
		    "ALTER TABLE feature_test_all " +
		    "OWNER TO " + config.getDatabaseUser() + "; " +
		    "GRANT ALL ON TABLE feature_test_all TO " + config.getDatabaseUser() + "; " +
		    "GRANT SELECT ON TABLE feature_test_all TO public;";
		
		return sqlStatement;	
	}
	
}
