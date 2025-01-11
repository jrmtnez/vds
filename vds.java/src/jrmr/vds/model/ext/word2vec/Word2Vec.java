package jrmr.vds.model.ext.word2vec;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jrmr.vds.model.config.Config;
import jrmr.vds.model.db.DatabaseUtils;
import jrmr.vds.model.text.VocabularyExtractor;

public class Word2Vec {
	
	Config config;
	private Map<String, WordNeuron> neuronMap;	
	private Map<String, Integer> vocabulary;
	private double alpha;
	private double initialAlpha;	
	private int freqThresold;
	private int vectorSize;
	private int windowSize;
	private int numOfThread;
	private int totalWordCount;
	private int currentWordCount;
	private final byte[] alphaLock = new byte[0];
	private double sample;
	private double[] expTable;
	private static final int EXP_TABLE_SIZE = 1000;
	private static final int MAX_EXP = 6;
	private long nextRandom = 5;
	
	VectorModel vectorModel; 
	
	public static enum Method{
		CBow, Skip_Gram
	}
	
	private Method trainMethod;
	
	public Word2Vec() {
		config = new Config();
		
		freqThresold = 2; // before 5
		vectorSize = config.getWvVectorSize();
		windowSize = config.getWvWindowSize();
		numOfThread = 2;		
		
		switch (config.getWvTrainMethod()) {
		case "Continuous Bag of Words" :
			trainMethod = Method.CBow;			
			break;
		case "Skip Gram" :
			trainMethod = Method.Skip_Gram;
			break;			
		}
		
		sample = 1e-4;
		alpha = 0.025; 
		initialAlpha = alpha;

		expTable = new double[EXP_TABLE_SIZE];
		computeExp();
	}
	
	public void extractVocabulary() {		
			
		VocabularyExtractor vocabularyExtractor = new VocabularyExtractor();
//		-C003				
//		vocabulary = vocabularyExtractor.getVocabularyFromRevisionText(config.getPlainTextPanArticleRevTable() + "_" + config.getPanCorpus());
		vocabulary = vocabularyExtractor.getVocabularyFromRevisionText(config.getPlainTextPanArticleRevTable() + "_all");
//		+C003				
		currentWordCount = vocabularyExtractor.getTextLength();				
		neuronMap = new HashMap<String, WordNeuron>();		
		
		for (String word : vocabulary.keySet()){			
			int freq = vocabulary.get(word);
			if (freq < freqThresold) {
				continue;
			}
			neuronMap.put(word, new WordNeuron(word, freq, vectorSize));
		}		
	}
	
	public void extractVocabulary(String revisionText) {		
		
		VocabularyExtractor vocabularyExtractor = new VocabularyExtractor();
		vocabulary = vocabularyExtractor.getVocabularyCaseSensitive(revisionText);
		currentWordCount = vocabularyExtractor.getTextLength();						
		neuronMap = new HashMap<String, WordNeuron>();		
		
		for (String word : vocabulary.keySet()){						
			int freq = vocabulary.get(word);
			if (freq < freqThresold) {
				continue;
			}
			neuronMap.put(word, new WordNeuron(word, freq, vectorSize));
		}
	}
	
	public void train() {
		
		System.out.println("Extracting vocabulary...");
		DatabaseUtils databaseUtils = new DatabaseUtils();		
		extractVocabulary();
		
		System.out.println("Training model...");
		HuffmanTree.make(neuronMap.values());

		totalWordCount = currentWordCount;
		currentWordCount = 0;
		ExecutorService threadPool = Executors.newFixedThreadPool(numOfThread);
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;										
			
		try {
			BlockingQueue<LinkedList<String>> corpusQueue = new ArrayBlockingQueue<LinkedList<String>>(numOfThread);
			@SuppressWarnings("rawtypes")
			LinkedList<Future> futures = new LinkedList<Future>();

			for (int thi = 0; thi < numOfThread; thi++){
				futures.add(threadPool.submit(new Trainer(corpusQueue)));
			}
			
			LinkedList<String> corpus = new LinkedList<String>();
			int trainBlockSize = 500;
//			-C003				
//			String sqlStatement = "SELECT revision_id FROM " + config.getPlainTextPanArticleRevTable() + "_" + config.getPanCorpus();
			String sqlStatement = "SELECT revision_id FROM " + config.getPlainTextPanArticleRevTable() + "_all";
//			+C003				
			con = databaseUtils.openConnection2();
			pst = con.prepareStatement(sqlStatement);
			rs = pst.executeQuery();
			
			databaseUtils.openConnection();
			
			while (rs.next()) {
				
				int revisionId = rs.getInt(1);
				
//				-C003				
//				String revisionText = databaseUtils.getRevisionText(config.getPlainTextPanArticleRevTable() + "_" + config.getPanCorpus(), revisionId);	
				String revisionText = databaseUtils.getRevisionText(config.getPlainTextPanArticleRevTable() + "_all", revisionId);	
				revisionText = revisionText.toLowerCase();
//				+C003				
				
				String[] textLines  = revisionText.split("[\n\r]");

				for (int i = 0; i < textLines.length; i++) {					
					corpus.add(textLines[i]);
					if (corpus.size() == trainBlockSize){
						corpusQueue.put(corpus);
						corpus = new LinkedList<>();	
					}		
				}
			}
			
			databaseUtils.closeConnection();
			
			corpusQueue.put(corpus);
			
			for (@SuppressWarnings("rawtypes") Future future : futures){
				future.get();
			}
			threadPool.shutdown(); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
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
				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DatabaseUtils.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		
		System.out.println("Done.");
	}
	
	public void train(String revisionText) {
		
		System.out.println("Extracting vocabulary...");	
		extractVocabulary(revisionText);
		
		System.out.println("Training model...");
		HuffmanTree.make(neuronMap.values());

		totalWordCount = currentWordCount;
		currentWordCount = 0;
		ExecutorService threadPool = Executors.newFixedThreadPool(numOfThread);
					
		try {
			BlockingQueue<LinkedList<String>> corpusQueue = new ArrayBlockingQueue<LinkedList<String>>(numOfThread);
			@SuppressWarnings("rawtypes")
			LinkedList<Future> futures = new LinkedList<Future>();

			for (int thi = 0; thi < numOfThread; thi++){
				futures.add(threadPool.submit(new Trainer(corpusQueue)));
			}
			
			LinkedList<String> corpus = new LinkedList<String>();
			int trainBlockSize = 500;
									
			String[] textLines  = revisionText.split("[\n\r]");			
			for (int i = 0; i < textLines.length; i++) {					
				if (!textLines[i].equals("")) { 
					corpus.add(textLines[i]);
				}
				
				if (corpus.size() == trainBlockSize){
					corpusQueue.put(corpus);
					corpus = new LinkedList<>();	
				}		
			}

			corpusQueue.put(corpus);
			
			for (@SuppressWarnings("rawtypes") Future future : futures){
				future.get();
			}
			threadPool.shutdown(); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		System.out.println("Done.");
	}
	
	public class Trainer implements Runnable{

		private BlockingQueue<LinkedList<String>> corpusQueue;

		private LinkedList<String> corpusToBeTrained;
		int trainingWordCount;
		double tempAlpha;

		public Trainer(LinkedList<String> corpus){
			corpusToBeTrained = corpus;
			trainingWordCount = 0;
		}

		public Trainer(BlockingQueue<LinkedList<String>> corpusQueue){
			this.corpusQueue = corpusQueue;
		}

		private void computeAlpha(){
			synchronized (alphaLock){
				currentWordCount += trainingWordCount;
				alpha = initialAlpha * (1 - currentWordCount / (double) (totalWordCount + 1));
				if (alpha < initialAlpha * 0.0001) {
					alpha = initialAlpha * 0.0001;
				}
				System.out.println("alpha:" + tempAlpha + "\tProgress: "
						+ (int) (currentWordCount / (double) (totalWordCount + 1) * 100)
						+ "%\t");
			}
		}

		private void training(){
			for(String line : corpusToBeTrained) {				
				List<WordNeuron> sentence = new ArrayList<WordNeuron>();				
				
				String[] lineTokens = line.split(" ");				
				trainingWordCount += lineTokens.length;
								
				for (int i = 0; i < lineTokens.length; i++) {
					
					String token = lineTokens[i];
					WordNeuron entry = neuronMap.get(token);
					
					if (entry == null) {
						continue;
					}
					
					// The subsampling randomly discards frequent words while keeping the ranking same
					if (sample > 0) {
						double ran = (Math.sqrt(entry.getFrequency() / (sample * totalWordCount)) + 1)
								* (sample * totalWordCount) / entry.getFrequency();
						nextRandom = nextRandom * 25214903917L + 11;
						if (ran < (nextRandom & 0xFFFF) / (double) 65536) {
							continue;
						}
						sentence.add(entry);
					}
				}
				
				for (int index = 0; index < sentence.size(); index++) {
					nextRandom = nextRandom * 25214903917L + 11;
					switch (trainMethod){
					case CBow:
						cbowGram(index, sentence, (int) nextRandom % windowSize, tempAlpha);
						break;
					case Skip_Gram:
						skipGram(index, sentence, (int) nextRandom % windowSize, tempAlpha);
						break;
					}
				}

			}
		}

		@Override
		public void run() {
			boolean hasCorpusToBeTrained = true;

			try {
				while (hasCorpusToBeTrained){					
					corpusToBeTrained = corpusQueue.poll(60, TimeUnit.SECONDS);					
					if (null != corpusToBeTrained) {
						tempAlpha = alpha;
						trainingWordCount = 0;
						training();
						computeAlpha();
					} else {
						hasCorpusToBeTrained = false;
					}
				}
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}

		}
	}
	
	
	private void skipGram(int index, List<WordNeuron> sentence, int b, double alpha) {

		WordNeuron word = sentence.get(index);
		int a, c = 0;
		for (a = b; a < windowSize * 2 + 1 - b; a++) {
			if (a == windowSize) {
				continue;
			}
			c = index - windowSize + a;
			if (c < 0 || c >= sentence.size()) {
				continue;
			}

			double[] neu1e = new double[vectorSize];
			//Hierarchical Softmax
			List<HuffmanNode> pathNeurons = word.getPathNeurons();
			WordNeuron we = sentence.get(c);
			for (int neuronIndex = 0; neuronIndex < pathNeurons.size() - 1; neuronIndex++){
				HuffmanNeuron out = (HuffmanNeuron) pathNeurons.get(neuronIndex);
				double f = 0;
				// Propagate hidden -> output
				for (int j = 0; j < vectorSize; j++) {
					f += we.vector[j] * out.vector[j];
				}
				if (f <= -MAX_EXP || f >= MAX_EXP) {

					continue;
				} else {
					f = (f + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2);
					f = expTable[(int) f];
				}
				// 'g' is the gradient multiplied by the learning rate
				HuffmanNeuron outNext = (HuffmanNeuron) pathNeurons.get(neuronIndex+1);
				double g = (1 - outNext.code - f) * alpha;
				for (c = 0; c < vectorSize; c++) {
					neu1e[c] += g * out.vector[c];
				}
				// Learn weights hidden -> output
				for (c = 0; c < vectorSize; c++) {
					out.vector[c] += g * we.vector[c];
				}
			}
			// Learn weights input -> hidden
			for (int j = 0; j < vectorSize; j++) {
				we.vector[j] += neu1e[j];
			}
		}
	}

	private void cbowGram(int index, List<WordNeuron> sentence, int b, double alpha) {

		WordNeuron word = sentence.get(index);
		int a, c = 0;

		double[] neu1e = new double[vectorSize];
		double[] neu1 = new double[vectorSize];
		WordNeuron last_word;

		for (a = b; a < windowSize * 2 + 1 - b; a++)
			if (a != windowSize) {
				c = index - windowSize + a;
				if (c < 0)
					continue;
				if (c >= sentence.size())
					continue;
				last_word = sentence.get(c);
				if (last_word == null)
					continue;
				for (c = 0; c < vectorSize; c++)
					neu1[c] += last_word.vector[c];
			}
		//Hierarchical Softmax
		List<HuffmanNode> pathNeurons = word.getPathNeurons();
		for (int neuronIndex = 0; neuronIndex < pathNeurons.size() - 1; neuronIndex++){
			HuffmanNeuron out = (HuffmanNeuron) pathNeurons.get(neuronIndex);

			double f = 0;
			// Propagate hidden -> output
			for (c = 0; c < vectorSize; c++)
				f += neu1[c] * out.vector[c];
			if (f <= -MAX_EXP)
				continue;
			else if (f >= MAX_EXP)
				continue;
			else
				f = expTable[(int) ((f + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2))];
			// 'g' is the gradient multiplied by the learning rate
			HuffmanNeuron outNext = (HuffmanNeuron) pathNeurons.get(neuronIndex+1);
			double g = (1 - outNext.code - f) * alpha;
			//
			for (c = 0; c < vectorSize; c++) {
				neu1e[c] += g * out.vector[c];
			}
			// Learn weights hidden -> output
			for (c = 0; c < vectorSize; c++) {
				out.vector[c] += g * neu1[c];
			}
		}
		
		for (a = b; a < windowSize * 2 + 1 - b; a++) {
			if (a != windowSize) {
				c = index - windowSize + a;
				if (c < 0)
					continue;
				if (c >= sentence.size())
					continue;
				last_word = sentence.get(c);
				if (last_word == null)
					continue;
				for (c = 0; c < vectorSize; c++)
					last_word.vector[c] += neu1e[c];
			}

		}
	}
	
	public void saveModel(File file) {

		DataOutputStream dataOutputStream = null;
		try {
			dataOutputStream = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(file)));
			dataOutputStream.writeInt(neuronMap.size());
			dataOutputStream.writeInt(vectorSize);
			for (Map.Entry<String, WordNeuron> element : neuronMap.entrySet()) {
				dataOutputStream.writeUTF(element.getKey());
				for (double d : element.getValue().vector) {
					dataOutputStream.writeFloat(((Double) d).floatValue());
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (dataOutputStream != null){
					dataOutputStream.close();
				}
			}catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public VectorModel outputVector(){

		Map<String, float[]> wordMapConverted = new HashMap<>();
		String wordKey;
		float[] vector;
		double vectorLength;
		double[] vectorNorm;

		for (Map.Entry<String, WordNeuron> element : neuronMap.entrySet()) {

			wordKey = element.getKey();

			vectorNorm = element.getValue().vector;
			vector = new float[vectorSize];
			vectorLength = 0;

			for (int vi = 0; vi < vectorNorm.length ; vi++){
				vectorLength += (float) vectorNorm[vi] * vectorNorm[vi];
				vector[vi] = (float) vectorNorm[vi];
			}

			vectorLength = Math.sqrt(vectorLength);

			for (int vi = 0; vi < vector.length; vi++) {
				vector[vi] /= vectorLength;
			}
			wordMapConverted.put(wordKey, vector);
		}

		return new VectorModel(wordMapConverted, vectorSize);
	}
	
	private void computeExp() {
		for (int i = 0; i < EXP_TABLE_SIZE; i++) {
			expTable[i] = Math.exp(((i / (double) EXP_TABLE_SIZE * 2 - 1) * MAX_EXP));
			expTable[i] = expTable[i] / (expTable[i] + 1);
		}
	}
	
    public void testVector(String modelFilePath, String word){

    	if (vectorModel == null) {
    		vectorModel = VectorModel.loadFromFile(modelFilePath);
    	}
    	
        Set<VectorModel.WordScore> result = Collections.emptySet();

        result = vectorModel.similar(word);
        
        System.out.println("Words close to: " + word);
        for (VectorModel.WordScore we : result){
            System.out.println(we.name + " : " + we.score);
        }
        System.out.println();
    }
    
    public ObservableList<WordSimilarity> getSimilarityList(String modelFilePath, String word){

    	if (vectorModel == null) {
            vectorModel = VectorModel.loadFromFile(modelFilePath);    		
    	}

        Set<VectorModel.WordScore> result = Collections.emptySet();

        result = vectorModel.similar(word);
        
        ObservableList<WordSimilarity> similarityList = FXCollections.observableArrayList();
        
        for (VectorModel.WordScore we : result) {
        	similarityList.add(new WordSimilarity(we.name, we.score));
        }
        
        return similarityList;
    }
}
