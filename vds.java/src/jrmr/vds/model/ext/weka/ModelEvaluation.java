package jrmr.vds.model.ext.weka;

import jrmr.vds.model.io.FileUtils;

public class ModelEvaluation {
	
	@SuppressWarnings("unused")
	public void evaluateFeatures() {
		
		BaselineResults baselineResults = new BaselineResults();
		J48Classifier j48Classifier;
		RandomForestClassifier randomForestClassifier;
		
		String[] output;
		String doc = "";
		

		// J48, ranking features,  test - cross validation
		doc = doc + "J48, ranking features, test - cross validation\n";		
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		for (int feature = 28; feature < 36; feature++) {
			int[] features = {feature};
			j48Classifier = new J48Classifier();			
			output = j48Classifier.evaluateModelCrossValidation(
					"model//features_all_test.arff", 
					"model//features_all_test.arff",
					features);
			doc = doc + output[0] + " (" + (feature + 1) + ");" + output[1] + "\n";
		}
	
		
		// RandomForest, ranking features, train + test
		doc = doc + "\n\nRandomForest, ranking features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features1 = {
				28, 29, 30, 31, 32, 33, 34, 35
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features1,
				"partial_ranking");

		doc = doc + output[0] + ";" + output[1] + "\n";
		
		
		// J48, word-vector features, test - cross validation
		doc = doc + "\n\nJ48, word-vector features, test - cross validation\n";		
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		for (int feature = 36; feature < 40; feature++) {
			int[] features = {feature};
			j48Classifier = new J48Classifier();			
			output = j48Classifier.evaluateModelCrossValidation(
					"model//features_all_test.arff", 
					"model//features_all_test.arff",
					features);
			doc = doc + output[0] + " (" + (feature + 1) + ");" + output[1] + "\n";
		}
	
		
		// RandomForest, word-vector features, train + test
		doc = doc + "\n\nRandomForest, word-vector features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features2 = {
				36, 37, 38, 39
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features2,
				"partial_word_vector");

		doc = doc + output[0] + ";" + output[1] + "\n";
		
		
		// J48, SDA features, test - cross validation
		doc = doc + "\n\nJ48, SDA features, test - cross validation\n";		
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features3 = {
				40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64
				};	
		j48Classifier = new J48Classifier();		
		output = j48Classifier.evaluateModelCrossValidation(
				"model//features_all_test.arff", 
				"model//features_all_test.arff",
				features3);
		doc = doc + output[0] + ";" + output[1] + "\n";
		
		
		// RandomForest, SDA features, train + test
		doc = doc + "\n\nRandomForest, SDA features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features4 = {
				40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features4,
				"partial_sda0");

		doc = doc + output[0] + ";" + output[1] + "\n";


		// RandomForest, SDA (a) features, train + test
		doc = doc + "\n\nRandomForest, SDA (a) features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features5 = {
				40, 41, 42, 43, 44, 45, 46, 47, 48, 49
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features5,
				"partial_sda1");

		doc = doc + output[0] + ";" + output[1] + "\n";
		

		// RandomForest, SDA (b) features, train + test
		doc = doc + "\n\nRandomForest, SDA (b) features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features6 = {
				50, 51, 52, 53, 54, 55, 56, 57, 58, 59
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features6,
				"partial_sda2");

		doc = doc + output[0] + ";" + output[1] + "\n";


		// RandomForest, SDA (c) features, train + test
		doc = doc + "\n\nRandomForest, SDA (c) features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features7 = {
				55, 56, 57, 58, 59, 60, 61, 62, 63, 64
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features7,
				"partial_sda3");

		doc = doc + output[0] + ";" + output[1] + "\n";		
		
		
		// J48, other features, test - cross validation
		doc = doc + "\n\nJ48, other features, test - cross validation\n";		
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		for (int feature = 0; feature < 28; feature++) {
			int[] features = {feature};
			j48Classifier = new J48Classifier();			
			output = j48Classifier.evaluateModelCrossValidation(
					"model//features_all_test.arff", 
					"model//features_all_test.arff",
					features);
			doc = doc + output[0] + " (" + (feature + 1) + ");" + output[1] + "\n";
		}
		
		
		// RandomForest, other features, train + test
		doc = doc + "\n\nRandomForest, other features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features8 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27			// 0 language dependent				
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features8,
				"partial_other");

		doc = doc + output[0] + ";" + output[1] + "\n";
		
		
		// RandomForest, 1 + 2 + 3 features, train + test
		doc = doc + "\n\nRandomForest, 1 + 2 + 3 features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features9 = {
				28, 29, 30, 31, 32, 33, 34, 35,							// 1 ranking
				36, 37, 38, 39,											// 2 word-vectors
				40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64	// 3 SDA
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features9,
				"partial_1_2_3");

		doc = doc + output[0] + ";" + output[1] + "\n";
		
		
		// RandomForest, 0 + 1 features, train + test
		doc = doc + "\n\nRandomForest, 0 + 1 features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features10 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,			// 0 language dependent
				28, 29, 30, 31, 32, 33, 34, 35							// 1 ranking				
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features10,
				"partial_0_1");

		doc = doc + output[0] + ";" + output[1] + "\n";
		
		
		// RandomForest, 0 + 2 features, train + test
		doc = doc + "\n\nRandomForest, 0 + 2 features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features11 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,			// 0 language dependent
				36, 37, 38, 39											// 2 word-vectors				
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features11,
				"partial_0_2");

		doc = doc + output[0] + ";" + output[1] + "\n";
		

		// RandomForest, 0 + 3 features, train + test
		doc = doc + "\n\nRandomForest, 0 + 3 features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features12 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,			// 0 language dependent
				40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64	// 3 SDA				
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features12,
				"partial_0_3");

		doc = doc + output[0] + ";" + output[1] + "\n";

		
		// RandomForest, 0 + 1 + 2 features, train + test
		doc = doc + "\n\nRandomForest, 0 + 1 + 2 features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features13 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,			// 0 language dependent
				28, 29, 30, 31, 32, 33, 34, 35,							// 1 ranking
				36, 37, 38, 39											// 2 word-vectors
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features13,
				"partial_0_1_2");

		doc = doc + output[0] + ";" + output[1] + "\n";
		

		// RandomForest, 0 + 1 + 2 + 3 features, train + test
		doc = doc + "\n\nRandomForest, 0 + 1 + 2 + 3 features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features14 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,			// 0 language dependent
				28, 29, 30, 31, 32, 33, 34, 35,							// 1 ranking
				36, 37, 38, 39,											// 2 word-vectors
				40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64	// 3 SDA				
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features14,
				"partial_0_1_2_3");

		doc = doc + output[0] + ";" + output[1] + "\n";


		// RandomForest, 0 + 1 + 2 + 3a features, train + test
		doc = doc + "\n\nRandomForest, 0 + 1 + 2 + 3a features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features15 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,			// 0 language dependent
				28, 29, 30, 31, 32, 33, 34, 35,							// 1 ranking
				36, 37, 38, 39,											// 2 word-vectors
				40, 41, 42, 43, 44, 45, 46, 47, 48, 49					// 3 SDA (a)				
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features15,
				"partial_0_1_2_3a");

		doc = doc + output[0] + ";" + output[1] + "\n";		
		
		
		// RandomForest, 0 + 1 + 2 + 3b features, train + test
		doc = doc + "\n\nRandomForest, 0 + 1 + 2 + 3b features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features16 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,			// 0 language dependent
				28, 29, 30, 31, 32, 33, 34, 35,							// 1 ranking
				36, 37, 38, 39,											// 2 word-vectors
				50, 51, 52, 53, 54, 55, 56, 57, 58, 59					// 3 SDA (b)				
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features16,
				"partial_0_1_2_3b");

		doc = doc + output[0] + ";" + output[1] + "\n";

		
		// RandomForest, 0 + 1 + 2 + 3c features, train + test
		doc = doc + "\n\nRandomForest, 0 + 1 + 2 + 3c features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features17 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27,			// 0 language dependent
				28, 29, 30, 31, 32, 33, 34, 35,							// 1 ranking
				36, 37, 38, 39,											// 2 word-vectors
				55, 56, 57, 58, 59, 60, 61, 62, 63, 64					// 3 SDA (c)				
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features17,
				"partial_0_1_2_3c");

		doc = doc + output[0] + ";" + output[1] + "\n";

		
		
		// RandomForest, other language-independent features, train + test
		doc = doc + "\n\nRandomForest, other language-independent features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features18 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15	// 0 language independent
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features18,
				"partial_0li");

		doc = doc + output[0] + ";" + output[1] + "\n";
		
		
		// RandomForest, 0li + 1 features, train + test
		doc = doc + "\n\nRandomForest, 0li + 1 features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features19 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				28, 29, 30, 31, 32, 33, 34, 35							// 1 ranking
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features19,
				"partial_0li_1");

		doc = doc + output[0] + ";" + output[1] + "\n";
		
		
		// RandomForest, 0li + 2 features, train + test
		doc = doc + "\n\nRandomForest, 0li + 2 features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features20 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				36, 37, 38, 39											// 2 word-vectors
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features20,
				"partial_0li_2");

		doc = doc + output[0] + ";" + output[1] + "\n";
		

		// RandomForest, 0li + 3 features, train + test
		doc = doc + "\n\nRandomForest, 0li + 3 features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features21 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64	// 3 SDA				
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features21,
				"partial_0li_3");

		doc = doc + output[0] + ";" + output[1] + "\n";

		
		// RandomForest, 0li + 1 + 2 features, train + test
		doc = doc + "\n\nRandomForest, 0li + 1 + 2 features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features22 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				28, 29, 30, 31, 32, 33, 34, 35,							// 1 ranking
				36, 37, 38, 39											// 2 word-vectors
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features22,
				"partial_0li_1_2");

		doc = doc + output[0] + ";" + output[1] + "\n";
		

		// RandomForest, 0li + 1 + 2 + 3 features, train + test
		doc = doc + "\n\nRandomForest, 0li + 1 + 2 + 3 features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features23 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				28, 29, 30, 31, 32, 33, 34, 35,							// 1 ranking
				36, 37, 38, 39,											// 2 word-vectors
				40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64	// 3 SDA				
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features23,
				"partial_0li_1_2_3");

		doc = doc + output[0] + ";" + output[1] + "\n";
		

		// RandomForest, 0li + 1 + 2 + 3a features, train + test
		doc = doc + "\n\nRandomForest, 0li + 1 + 2 + 3a features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features24 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				28, 29, 30, 31, 32, 33, 34, 35,							// 1 ranking
				36, 37, 38, 39,											// 2 word-vectors
				40, 41, 42, 43, 44, 45, 46, 47, 48, 49					// 3 SDA (a)				
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features24,
				"partial_0li_1_2_3a");

		doc = doc + output[0] + ";" + output[1] + "\n";

		
		// RandomForest, 0li + 1 + 2 + 3b features, train + test
		doc = doc + "\n\nRandomForest, 0li + 1 + 2 + 3b features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features25 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				28, 29, 30, 31, 32, 33, 34, 35,							// 1 ranking
				36, 37, 38, 39,											// 2 word-vectors
				50, 51, 52, 53, 54, 55, 56, 57, 58, 59					// 3 SDA (b)				
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features25,
				"partial_0li_1_2_3b");

		doc = doc + output[0] + ";" + output[1] + "\n";
		
		// RandomForest, 0li + 1 + 2 + 3c features, train + test
		doc = doc + "\n\nRandomForest, 0li + 1 + 2 + 3c features, train + test\n";
		doc = doc + "Feature;Precision;Recall;F-Measure;AUC ROC;AUC PRC\n";
		int[] features26 = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,	// 0 language independent
				28, 29, 30, 31, 32, 33, 34, 35,							// 1 ranking
				36, 37, 38, 39,											// 2 word-vectors
				55, 56, 57, 58, 59, 60, 61, 62, 63, 64					// 3 SDA (c)				
				};	
		randomForestClassifier = new RandomForestClassifier();		
		output = randomForestClassifier.evaluateModelOnTestSet(
				"model//features_all_training.arff", 
				"model//features_all_test.arff",
				features26,
				"partial_0li_1_2_3c");

		doc = doc + output[0] + ";" + output[1] + "\n";
		
		FileUtils fileUtils = new FileUtils();
		fileUtils.string2File(doc, "model//results_thesis.csv");
	
	}
}
