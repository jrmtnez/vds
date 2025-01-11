package jrmr.vds.model.ext.weka;

public class BaselineResults {
	
	DatasetUtils datasetUtils;
	
	BaselineResults() {
		datasetUtils = new DatasetUtils();		
	}
	
	private String[] featureNames = {
		"@attribute anonymous {f,t}",
		"@attribute comment_length numeric",
		"@attribute size_increment numeric",
		"@attribute size_ratio numeric",
		"@attribute upper_to_lower_ratio numeric",
		"@attribute upper_to_all_ratio numeric",
		"@attribute digit_ratio numeric",
		"@attribute non_alphanumeric_ratio numeric",
		"@attribute character_diversity numeric",
		"@attribute character_distribution numeric",
		"@attribute compressibility numeric",
		"@attribute good_tokens_frequency numeric",
		"@attribute good_tokens_impact numeric",
		"@attribute average_term_frequency numeric",
		"@attribute longest_word numeric",
		"@attribute longest_character_sequence numeric",
		"@attribute vulgarism_frequency numeric",
		"@attribute vulgarism_impact numeric",
		"@attribute pronouns_frequency numeric",
		"@attribute pronouns_impact numeric",
		"@attribute bias_frequency numeric",
		"@attribute bias_impact numeric",
		"@attribute sex_frequency numeric",
		"@attribute sex_impact numeric",
		"@attribute bad_frequency numeric",
		"@attribute bad_impact numeric",
		"@attribute all_frequency numeric",
		"@attribute all_impact numeric"};

	// Wikipedia Vandalism Detection Through Machine Learning: Feature Review and New Proposals: Lab Report for PAN at CLEF 2010
	// Mola-Velasco, 2012
	private double[][] featureMesures = {
		{0.166, 0.872, 0.278, 0.780},
		{0.102, 0.663, 0.177, 0.661},
		{0.111, 0.669, 0.191, 0.690},
		{0.100, 0.195, 0.132, 0.572},
		{0.131, 0.629, 0.217, 0.755},
		{0.129, 0.621, 0.214, 0.731},
		{0.128, 0.718, 0.217, 0.750},
		{0.117, 0.629, 0.197, 0.729},
		{0.121, 0.689, 0.206, 0.741},
		{0.110, 0.698, 0.190, 0.710},
		{0.095, 0.290, 0.143, 0.627},
		{0.000, 0.000, 0.000, 0.545},
		{0.149, 0.106, 0.124, 0.584},
		{0.207, 0.021, 0.037, 0.503},
		{0.124, 0.712, 0.211, 0.742},
		{0.115, 0.514, 0.189, 0.628},
		{0.904, 0.214, 0.346, 0.597},
		{0.618, 0.227, 0.332, 0.599},
		{0.588, 0.152, 0.241, 0.562},
		{0.311, 0.173, 0.223, 0.561},
		{0.623, 0.082, 0.145, 0.536},
		{0.311, 0.173, 0.223, 0.561},
		{0.717, 0.041, 0.078, 0.517},
		{0.537, 0.048, 0.087, 0.518},
		{0.747, 0.080, 0.145, 0.532},
		{0.348, 0.111, 0.169, 0.539},
		{0.762, 0.353, 0.482, 0.661},
		{0.501, 0.377, 0.430, 0.662}};
	
	private double[] featureMesuresAllJ48CrossValidationTrain = {0.773, 0.543, 0.638, 0.930, 0.0};
	private double[] featureMesuresAllRandomForestCrossValidationTrain = {0.861, 0.568, 0.684, 0.963, 0.0};
	
	// Overview of the 2nd International Competition on Wikipedia Vandalism Detection
	// Martin Potthast, Teresa Holfeld, 2011
	private double[] featureMesuresAllRandomForestTrainTest = {0.0, 0.0, 0.0, 0.92236, 0.66522};

	public String getFeatureName(int feature) {
		String result = featureNames[feature];		
		
		return datasetUtils.convertAttributeName(result);
	}

	public String getFeatureMesures(int feature) {
		String result = 
			Double.toString(featureMesures[feature][0]) + ";" +
			Double.toString(featureMesures[feature][1]) + ";" +
			Double.toString(featureMesures[feature][2]) + ";" +
			Double.toString(featureMesures[feature][3]);	
		
		return datasetUtils.convertAttributeMeasures(result);
	}
	
	public String getFeatureMesuresAllRandomForestCrossValidationTrain() {
		String result = 
			Double.toString(featureMesuresAllRandomForestCrossValidationTrain[0]) + ";" +
			Double.toString(featureMesuresAllRandomForestCrossValidationTrain[1]) + ";" +
			Double.toString(featureMesuresAllRandomForestCrossValidationTrain[2]) + ";" +
			Double.toString(featureMesuresAllRandomForestCrossValidationTrain[3]) + ";" +
			Double.toString(featureMesuresAllRandomForestCrossValidationTrain[4]);	
		
		return datasetUtils.convertAttributeMeasures(result);
	}

	public String getFeatureMesuresAllRandomForestTrainTest() {
		String result = 
			Double.toString(featureMesuresAllRandomForestTrainTest[0]) + ";" +
			Double.toString(featureMesuresAllRandomForestTrainTest[1]) + ";" +
			Double.toString(featureMesuresAllRandomForestTrainTest[2]) + ";" +
			Double.toString(featureMesuresAllRandomForestTrainTest[3]) + ";" +
			Double.toString(featureMesuresAllRandomForestTrainTest[4]);	
		
		return datasetUtils.convertAttributeMeasures(result);
	}

	public String getFeatureMesuresAllJ48CrossValidationTrain() {
		String result = 
			Double.toString(featureMesuresAllJ48CrossValidationTrain[0]) + ";" +
			Double.toString(featureMesuresAllJ48CrossValidationTrain[1]) + ";" +
			Double.toString(featureMesuresAllJ48CrossValidationTrain[2]) + ";" +
			Double.toString(featureMesuresAllJ48CrossValidationTrain[3]) + ";" +
			Double.toString(featureMesuresAllJ48CrossValidationTrain[4]);	
		
		return datasetUtils.convertAttributeMeasures(result);
	}
	
	
}
