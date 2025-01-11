package jrmr.vds.model.dnn.io;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jrmr.vds.model.db.DatabaseUtils;
import jrmr.vds.model.io.FileUtils;
import jrmr.vds.model.text.VocabularyExtractor;

public class VSModelUtils {

	public void extractInsertedDeletedWordsVocabulary() {
		System.out.println("Extracting inserted / deleted words vocabulary...");

		VocabularyExtractor vocabularyExtractor = new VocabularyExtractor();
		FileUtils fileutils = new  FileUtils();

		HashMap<String,Integer> insertedVoc = vocabularyExtractor.getVocabularyFromField(
				"SELECT inserted_words_list FROM words_diff_training UNION SELECT inserted_words_list FROM words_diff_test"
				, "config//wordfrequency5000con_eliminaciones.txt", 1, false	// best
//				, "config//wordfrequency5000.txt", 1, false
				);
		fileutils.map2FileWithFilter(insertedVoc, "modeldnn//inserted_words_voc.txt",10,3,false); // best
//		fileutils.map2FileWithFilter(insertedVoc, "modeldnn//inserted_words_voc.txt",15,3,false);
//		fileutils.map2FileWithFilter(insertedVoc, "modeldnn//inserted_words_voc.txt",5,3,false);

		HashMap<String,Integer> deletedVoc = vocabularyExtractor.getVocabularyFromField(
				"SELECT deleted_words_list FROM words_diff_training UNION SELECT deleted_words_list FROM words_diff_test"
				, "config//wordfrequency5000con_eliminaciones.txt", 1, false	// best
//				, "config//wordfrequency5000.txt", 1, false
				);
		fileutils.map2FileWithFilter(deletedVoc, "modeldnn//deleted_words_voc.txt",10,3,false); // best
//		fileutils.map2FileWithFilter(deletedVoc, "modeldnn//deleted_words_voc.txt",15,3,false);
//		fileutils.map2FileWithFilter(deletedVoc, "modeldnn//deleted_words_voc.txt",5,3,false);
	}

	public void writeDiffInsertedDeletedVEModel() {

		// VE model over diff, inserted and deleted words

		int pos;
		int offset;
		FileUtils fileutils = new  FileUtils();
		HashMap<String,Integer> insertedVoc = fileutils.file2Map("modeldnn//inserted_words_voc.txt");
		HashMap<String,Integer> deletedVoc = fileutils.file2Map("modeldnn//deleted_words_voc.txt");
		HashMap<Integer,String> vocIndex = new HashMap<Integer,String>();

		System.out.println("--- inserted ---");
		pos = 0;
		offset = 0;
		for (Map.Entry<String, Integer> entry : insertedVoc.entrySet()) {
			insertedVoc.put(entry.getKey(), pos);
			vocIndex.put(pos + offset, entry.getKey());
			System.out.println(entry.getKey() + " " + pos);
			pos++;
		}

		System.out.println("--- deleted ---");
		pos = 0;
		offset = insertedVoc.size();
		for (Map.Entry<String, Integer> entry : deletedVoc.entrySet()) {
			deletedVoc.put(entry.getKey(), pos);
			vocIndex.put(pos + offset, entry.getKey());
			System.out.println(entry.getKey() + " " + pos);
			pos++;
		}

		System.out.println("--- searching ---");

		int vectorSize = insertedVoc.size() + deletedVoc.size();

		Connection con  = null;
		Statement st = null;
		ResultSet rs = null;

		String sqlStatement = null;
		File outputFile1 = null;
		FileWriter fileWriter1 = null;
		PrintWriter printWriter1 = null;
		File outputFile2 = null;
		FileWriter fileWriter2 = null;
		PrintWriter printWriter2 = null;
		File outputFile3 = null;
		FileWriter fileWriter3 = null;
		PrintWriter printWriter3 = null;
		File outputFile4 = null;
		FileWriter fileWriter4 = null;
		PrintWriter printWriter4 = null;


		DatabaseUtils databaseUtils = new DatabaseUtils();

		try {
//			sqlStatement = "SELECT * FROM words_diff_training";
			sqlStatement = "SELECT * FROM words_diff_training ORDER BY edit_id";
//			sqlStatement = "SELECT * FROM words_diff_training ORDER BY sorting_column";
			outputFile1 = new File("..//..//vds.python//corpus//inserted_deleted_vemodel_training_input.txt");
			outputFile2 = new File("..//..//vds.python//corpus//inserted_deleted_vemodel_training_target.txt");
			outputFile3 = new File("..//..//vds.python//corpus//inserted_deleted_vemodel_training_editid.txt");
			outputFile4 = new File("..//..//vds.python//corpus//inserted_deleted_vemodel_training.arff");

			con  = databaseUtils.openConnection2();
			fileWriter1 = new FileWriter(outputFile1);
			printWriter1 = new PrintWriter(fileWriter1);
			fileWriter2 = new FileWriter(outputFile2);
			printWriter2 = new PrintWriter(fileWriter2);
			fileWriter3 = new FileWriter(outputFile3);
			printWriter3 = new PrintWriter(fileWriter3);
			fileWriter4 = new FileWriter(outputFile4);
			printWriter4 = new PrintWriter(fileWriter4);

			printWriter4.println("@relation 'diff_inserted_deleted_vemodel_training'");
			for (int i = 0; i < vectorSize; i++) {
				printWriter4.println("@attribute " + vocIndex.get(i) + "_" + i + " numeric");
			}
			printWriter4.println("@attribute class {f,t}");
			printWriter4.println("@data");

			try {
				st = con.createStatement();
				rs = st.executeQuery(sqlStatement);

				PreparedStatement pst = con.prepareStatement("SELECT COUNT(1) FROM words_diff_training");
				ResultSet rs2 = pst.executeQuery();

				int rowCount = 0;
				if (rs2.next()) {
					rowCount = rs2.getInt(1);
				}

				int actRow = 0;

				while(rs.next()) {

					actRow++;

					System.out.println(
						"Processing diff_inserted_deleted " + (int) ((float) actRow / (float) rowCount * 100) +
						"% (" + rowCount + ") on training. Edit: " + rs.getString(1));

					int[] v = new int[vectorSize];

					String revisionClass = rs.getString(2);
					String[] insertedWordsList = rs.getString(5).split("[ \n\r]");
					String[] insertedWordsFrequencyList = rs.getString(6).split("[ \n\r]");
					String[] deletedWordsList = rs.getString(7).split("[ \n\r]");
					String[] deletedWordsFrequencyList = rs.getString(8).split("[ \n\r]");

					offset = 0;
					for (int i = 0; i < insertedWordsList.length; i++) {
						if (insertedVoc.containsKey(insertedWordsList[i])) {
							int freq = Integer.parseInt(insertedWordsFrequencyList[i]);
							v[insertedVoc.get(insertedWordsList[i]) + offset] = freq;
						}
					}

					offset = insertedVoc.size();
					for (int i = 0; i < deletedWordsList.length; i++) {
						if (deletedVoc.containsKey(deletedWordsList[i])) {
							int freq = Integer.parseInt(deletedWordsFrequencyList[i]);
							v[deletedVoc.get(deletedWordsList[i]) + offset] = -freq;
						}
					}

					String dataLine = "";
					for (int i = 0; i < vectorSize; i++) {

						String binValue = "0";
						if (v[i] != 0) {
							binValue = "1";
						}

						if (i == 0) {
							dataLine = binValue;
						} else {
							dataLine = dataLine + "," + binValue;
						}
					}
					printWriter1.println(dataLine);

					if (revisionClass.equals("vandalism")) {
						printWriter2.println("1");
						printWriter4.println(dataLine + ",t");

					} else {
						printWriter2.println("0");
						printWriter4.println(dataLine + ",f");
					}

					printWriter3.println(rs.getString(1));
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
					if (con != null) {
						con.close();
					}

				} catch (SQLException ex) {
					Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
					lgr.log(Level.WARNING, ex.getMessage(), ex);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fileWriter1)
					fileWriter1.close();
				if (null != fileWriter2)
					fileWriter2.close();
				if (null != fileWriter3)
					fileWriter3.close();
				if (null != fileWriter4)
					fileWriter4.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		try {
//			sqlStatement = "SELECT * FROM words_diff_test";
			sqlStatement = "SELECT * FROM words_diff_test ORDER BY edit_id";
//			sqlStatement = "SELECT * FROM words_diff_test ORDER BY sorting_column";
			outputFile1 = new File("..//..//vds.python//corpus//inserted_deleted_vemodel_test_input.txt");
			outputFile2 = new File("..//..//vds.python//corpus//inserted_deleted_vemodel_test_target.txt");
			outputFile3 = new File("..//..//vds.python//corpus//inserted_deleted_vemodel_test_editid.txt");
			outputFile4 = new File("..//..//vds.python//corpus//inserted_deleted_vemodel_test.arff");

			con  = databaseUtils.openConnection2();
			fileWriter1 = new FileWriter(outputFile1);
			printWriter1 = new PrintWriter(fileWriter1);
			fileWriter2 = new FileWriter(outputFile2);
			printWriter2 = new PrintWriter(fileWriter2);
			fileWriter3 = new FileWriter(outputFile3);
			printWriter3 = new PrintWriter(fileWriter3);
			fileWriter4 = new FileWriter(outputFile4);
			printWriter4 = new PrintWriter(fileWriter4);

			printWriter4.println("@relation 'diff_inserted_deleted_vemodel_test'");
			for (int i = 0; i < vectorSize; i++) {
				printWriter4.println("@attribute " + vocIndex.get(i) + "_" + i  + " numeric");
			}
			printWriter4.println("@attribute class {f,t}");
			printWriter4.println("@data");

			try {
				st = con.createStatement();
				rs = st.executeQuery(sqlStatement);

				PreparedStatement pst = con.prepareStatement("SELECT COUNT(1) FROM words_diff_test");
				ResultSet rs2 = pst.executeQuery();

				int rowCount = 0;
				if (rs2.next()) {
					rowCount = rs2.getInt(1);
				}

				int actRow = 0;

				while(rs.next()) {

					actRow++;

					System.out.println(
						"Processing diff_inserted_deleted " + (int) ((float) actRow / (float) rowCount * 100) +
						"% (" + rowCount + ") on test. Edit: " + rs.getString(1));

					int[] v = new int[vectorSize];

					String revisionClass = rs.getString(2);
					String[] insertedWordsList = rs.getString(5).split("[ \n\r]");
					String[] insertedWordsFrequencyList = rs.getString(6).split("[ \n\r]");
					String[] deletedWordsList = rs.getString(7).split("[ \n\r]");
					String[] deletedWordsFrequencyList = rs.getString(8).split("[ \n\r]");

					offset = 0;
					for (int i = 0; i < insertedWordsList.length; i++) {
						if (insertedVoc.containsKey(insertedWordsList[i])) {
							int freq = Integer.parseInt(insertedWordsFrequencyList[i]);
							v[insertedVoc.get(insertedWordsList[i]) + offset] = freq;
						}
					}

					offset = insertedVoc.size();
					for (int i = 0; i < deletedWordsList.length; i++) {
						if (deletedVoc.containsKey(deletedWordsList[i])) {
							int freq = Integer.parseInt(deletedWordsFrequencyList[i]);
							v[deletedVoc.get(deletedWordsList[i]) + offset] = -freq;
						}
					}

					String dataLine = "";
					for (int i = 0; i < vectorSize; i++) {

						String binValue = "0";
						if (v[i] != 0) {
							binValue = "1";
						}

						if (i == 0) {
							dataLine = binValue;
						} else {
							dataLine = dataLine + "," + binValue;
						}
					}

					printWriter1.println(dataLine);

					if (revisionClass.equals("vandalism")) {
						printWriter2.println("1");
						printWriter4.println(dataLine + ",t");
					} else {
						printWriter2.println("0");
						printWriter4.println(dataLine + ",f");
					}

					printWriter3.println(rs.getString(1));
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
					if (con != null) {
						con.close();
					}

				} catch (SQLException ex) {
					Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
					lgr.log(Level.WARNING, ex.getMessage(), ex);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fileWriter1)
					fileWriter1.close();
				if (null != fileWriter2)
					fileWriter2.close();
				if (null != fileWriter3)
					fileWriter3.close();
				if (null != fileWriter4)
					fileWriter4.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		System.out.println("NN input size: " + vectorSize);

	}

}
