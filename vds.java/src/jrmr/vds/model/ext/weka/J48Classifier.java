package jrmr.vds.model.ext.weka;


import java.util.Random;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.CostSensitiveClassifier;

public class J48Classifier {
	
	public String[] evaluateModelOnTestSet(String trainFileName, String testFileName, int[] attributes) {
		
		String[] result = new String[2];
		
		try {
			System.out.println("-> J48Classifier: loading datasets...");
						
			DataSource trainSource = new DataSource(trainFileName);
			Instances trainData = trainSource.getDataSet();
			
			DataSource testSource = new DataSource(testFileName);
			Instances testData = testSource.getDataSet();
			
			DatasetUtils datasetUtils = new DatasetUtils();
			trainData = datasetUtils.maintainAttributes(trainData, attributes, true);						
			testData = datasetUtils.maintainAttributes(testData, attributes, false);
						
			trainData.setClassIndex(trainData.numAttributes() - 1); 
			testData.setClassIndex(testData.numAttributes() - 1); 				
			
			String[] options = weka.core.Utils.splitOptions(" -cost-matrix \"[0.0 1.0; 10.0 0.0]\" -S 1 -W weka.classifiers.trees.J48 -- -C 0.25 -M 6 -A");
			CostSensitiveClassifier costSensitiveClassifier = new CostSensitiveClassifier();
			costSensitiveClassifier.setClassifier(new weka.classifiers.trees.J48());			
			costSensitiveClassifier.setOptions(options);
			
			System.out.println("-> J48Classifier: building classifier...");
			costSensitiveClassifier.buildClassifier(trainData);
			
			System.out.println("-> J48Classifier: evaluating model (train + test set)...");
			Evaluation eval = new Evaluation(trainData);
			eval.evaluateModel(costSensitiveClassifier, testData);
			
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
			System.out.println("-> J48Classifier: loading datasets...");
			
			DataSource trainSource = new DataSource(trainFileName);
			Instances trainData = trainSource.getDataSet();
			
			DataSource testSource = new DataSource(testFileName);
			Instances testData = testSource.getDataSet();
			
			DatasetUtils datasetUtils = new DatasetUtils();
			trainData = datasetUtils.maintainAttributes(trainData, attributes, true);						
			testData = datasetUtils.maintainAttributes(testData, attributes, false);
						
			trainData.setClassIndex(trainData.numAttributes() - 1); 
			testData.setClassIndex(testData.numAttributes() - 1); 	
			
			String[] options = weka.core.Utils.splitOptions(" -cost-matrix \"[0.0 1.0; 10.0 0.0]\" -S 1 -W weka.classifiers.trees.J48 -- -C 0.25 -M 6 -A");
			CostSensitiveClassifier costSensitiveClassifier = new CostSensitiveClassifier();
			costSensitiveClassifier.setClassifier(new weka.classifiers.trees.J48());			
			costSensitiveClassifier.setOptions(options);
			
			System.out.println("-> J48Classifier: building classifier...");
			costSensitiveClassifier.buildClassifier(trainData);
			
			System.out.println("-> J48Classifier: evaluating model (cross validation)...");
			Evaluation eval = new Evaluation(trainData);
			eval.crossValidateModel(costSensitiveClassifier, testData, 10, new Random(1));
						
//			System.out.println(eval.toSummaryString("\nResults\n======\n", false));
						
			System.out.println(
				"Precision:   " + eval.precision(1) + 				
				"\nRecall:      " + eval.recall(1) + 
				"\nF-measure:   " + eval.fMeasure(1) +
				"\nAUC ROC:     " + eval.areaUnderROC(1) +
				"\nAUC PRC:     " + eval.areaUnderPRC(1) + 
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
