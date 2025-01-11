package jrmr.vds.model.ext.weka;

import java.io.File;
import java.util.Random;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.RandomForest;

public class RandomForestClassifier {
	 
	public String[] evaluateModelOnTestSet(String trainFileName, String testFileName, int[] attributes, String partialArff) {
		
		String[] result = new String[2];
		
		try {
			System.out.println("-> RandomForestClassifier: loading datasets...");
			
			DataSource trainSource = new DataSource(trainFileName);
			Instances trainData = trainSource.getDataSet();
			
			DataSource testSource = new DataSource(testFileName);
			Instances testData = testSource.getDataSet();
			
			DatasetUtils datasetUtils = new DatasetUtils();
			trainData = datasetUtils.maintainAttributes(trainData, attributes, true);						
			testData = datasetUtils.maintainAttributes(testData, attributes, false);
			
			trainData.setClassIndex(trainData.numAttributes() - 1); 		
			testData.setClassIndex(testData.numAttributes() - 1); 		
									
			if (!partialArff.equals("")) {
				ArffSaver arffSaver = new ArffSaver();
				arffSaver.setInstances(trainData);
				arffSaver.setMaxDecimalPlaces(18);
				arffSaver.setFile(new File("model//" + partialArff + "_train.arff"));
				arffSaver.writeBatch();
	
				arffSaver = new ArffSaver();
				arffSaver.setInstances(testData);
				arffSaver.setMaxDecimalPlaces(18);
				arffSaver.setFile(new File("model//" + partialArff + "_test.arff"));
				arffSaver.writeBatch();
			}
			
			String[] options = weka.core.Utils.splitOptions("-I 1000 -K 5 -S 1 -num-slots 1");
			
			RandomForest randomForest = new RandomForest();
			randomForest.setOptions(options);
			
			System.out.println("-> RandomForestClassifier: building classifier...");
			randomForest.buildClassifier(trainData);
			
			System.out.println("-> RandomForestClassifier: evaluating model (train + test set)...");
			Evaluation eval = new Evaluation(trainData);
			eval.evaluateModel(randomForest, testData);
			
//			System.out.println(eval.toSummaryString("\nResults\n======\n", false));

			System.out.println(
					"Precision: " + eval.precision(1) + 				
					", Recall: " + eval.recall(1) + 
					", F-measure:" + eval.fMeasure(1) +
					", AUC ROC:" + eval.areaUnderROC(1) +
					", AUC PRC:" + eval.areaUnderPRC(1) + 
					"\n");	
			
			result[0] = "";
			if (trainData.numAttributes() == 2) {	// feature + class
				result[0] = datasetUtils.convertAttributeName(trainData.attribute(0).toString());
			} else {
				for (int i = 0; i < trainData.numAttributes() - 1; i++) {
					if (i == 0)
						result[0] = String.valueOf(attributes[i] + 1);
					else
						result[0] = result[0] + "," + (attributes[i] + 1);
				}
			}
			
			result[1] = datasetUtils.convertAttributeMeasures(
							eval.precision(1) + ";" + 
							eval.recall(1) + ";" + 
							eval.fMeasure(1) + ";" + 
							eval.areaUnderROC(1) + ";" + 
							eval.areaUnderPRC(1));
			
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		return result;
	}
	
	public String[] evaluateModelCrossValidation(String trainFileName, String testFileName, int[] attributes) {
		
		String[] result = new String[2];
		
		try {
			System.out.println("-> RandomForestClassifier: loading datasets...");
			
			DataSource trainSource = new DataSource(trainFileName);
			Instances trainData = trainSource.getDataSet();
			
			DataSource testSource = new DataSource(testFileName);
			Instances testData = testSource.getDataSet();
			
			DatasetUtils datasetUtils = new DatasetUtils();
			trainData = datasetUtils.maintainAttributes(trainData, attributes, true);						
			testData = datasetUtils.maintainAttributes(testData, attributes, false);
			
			trainData.setClassIndex(trainData.numAttributes() - 1); 		
			testData.setClassIndex(testData.numAttributes() - 1); 		
						
			String[] options = weka.core.Utils.splitOptions("-I 1000 -K 5 -S 1 -num-slots 1");
			
			RandomForest randomForest = new RandomForest();
			randomForest.setOptions(options);
			
			System.out.println("-> RandomForestClassifier: building classifier...");
			randomForest.buildClassifier(trainData);
			
			System.out.println("-> RandomForestClassifier: evaluating model (cross validation)...");
			Evaluation eval = new Evaluation(trainData);
			eval.crossValidateModel(randomForest, testData, 10, new Random(1));
			
//			System.out.println(eval.toSummaryString("\nResults\n======\n", false));

			System.out.println(
					"Precision: " + eval.precision(1) + 				
					", Recall: " + eval.recall(1) + 
					", F-measure:" + eval.fMeasure(1) +
					", AUC ROC:" + eval.areaUnderROC(1) +
					", AUC PRC:" + eval.areaUnderPRC(1) + 
					"\n");
			
			result[0] = "";
			if (trainData.numAttributes() == 2) {	// feature + class
				result[0] = datasetUtils.convertAttributeName(trainData.attribute(0).toString());
			} else {
				for (int i = 0; i < trainData.numAttributes() - 1; i++) {
					if (i == 0)
						result[0] = String.valueOf(attributes[i] + 1);
					else
						result[0] = result[0] + "," + (attributes[i] + 1);
				}
			}
			
			result[1] = datasetUtils.convertAttributeMeasures(
							eval.precision(1) + ";" + 
							eval.recall(1) + ";" + 
							eval.fMeasure(1) + ";" + 
							eval.areaUnderROC(1) + ";" + 
							eval.areaUnderPRC(1));
			
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		return result;
	}
}
