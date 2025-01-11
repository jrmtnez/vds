package jrmr.vds.model.pan;

import java.sql.Timestamp;

import jrmr.vds.model.features.AllFrequency;
import jrmr.vds.model.features.AllImpact;
import jrmr.vds.model.features.Anonymous;
import jrmr.vds.model.features.AverageTermFrequency;
import jrmr.vds.model.features.BadFrequency;
import jrmr.vds.model.features.BadImpact;
import jrmr.vds.model.features.BiasedFrequency;
import jrmr.vds.model.features.BiasedImpact;
import jrmr.vds.model.features.CharacterDistribution;
import jrmr.vds.model.features.CharacterDiversity;
import jrmr.vds.model.features.CommentLength;
import jrmr.vds.model.features.Compressibility;
import jrmr.vds.model.features.DigitRatio;
import jrmr.vds.model.features.GoodFrequency;
import jrmr.vds.model.features.GoodImpact;
import jrmr.vds.model.features.LongestCharacterSequence;
import jrmr.vds.model.features.LongestWord;
import jrmr.vds.model.features.NewAuthorityFrequency;
import jrmr.vds.model.features.NewAuthorityImpact;
import jrmr.vds.model.features.NewDegreeImpact;
import jrmr.vds.model.features.NewEigenvectorCentralityFrequency;
import jrmr.vds.model.features.NewEigenvectorCentralityImpact;
import jrmr.vds.model.features.NewPageRankFrequency;
import jrmr.vds.model.features.NewPageRankImpact;
import jrmr.vds.model.features.NewRelatedFrequency;
import jrmr.vds.model.features.NewRelatedImpact;
import jrmr.vds.model.features.NewWordsInVandalicCluster1;
import jrmr.vds.model.features.NewWordsInVandalicCluster2;
import jrmr.vds.model.features.NonAlphanumericRatio;
import jrmr.vds.model.features.PronounsFrequency;
import jrmr.vds.model.features.PronounsImpact;
import jrmr.vds.model.features.SexFrequency;
import jrmr.vds.model.features.SexImpact;
import jrmr.vds.model.features.SizeIncrement;
import jrmr.vds.model.features.SizeRatio;
import jrmr.vds.model.features.UpperToAllRatio;
import jrmr.vds.model.features.UpperToLowerRatio;
import jrmr.vds.model.features.VulgarismFrequency;
import jrmr.vds.model.features.VulgarismImpact;
import jrmr.vds.model.text.DiffUtils;
import jrmr.vds.model.text.cluster.WordClustering;

public class PanEdit {
	
	private int editId;
	private String editor;
	private int oldRevisionId;
	private int newRevisionId;
	private String diffUrl;
	private String revisionClass;
	private int annotators;
	private int totalAnnotators;
	private Timestamp editTime;
	private String editComment;
	private int articleId;
	private String articleTitle;
	
	private String oldRevisionText;
	private String newRevisionText;
	private String oldTokenizedRevisionText;
	private String newTokenizedRevisionText;	
	private String insertedText;	
	private String caseSensitiveConcatenatedInsertedWords;
	
	public PanEdit() {
		
	};

	public PanEdit(
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
	
		this.editId = editId;
		this.editor = editor;
		this.oldRevisionId = oldRevisionId;
		this.newRevisionId = newRevisionId;
		this.diffUrl = diffUrl;
		this.revisionClass = revisionClass;
		this.annotators = annotators;
		this.totalAnnotators = totalAnnotators;
		this.editTime = editTime;
		this.editComment = editComment;
		this.articleId = articleId;
		this.articleTitle = articleTitle;
	}

	public int getEditId() {
		return editId;
	}

	public void setEditId(int editId) {
		this.editId = editId;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public int getOldRevisionId() {
		return oldRevisionId;
	}

	public void setOldRevisionId(int oldRevisionId) {
		this.oldRevisionId = oldRevisionId;
	}

	public int getNewRevisionId() {
		return newRevisionId;
	}

	public void setNewRevisionId(int newRevisionId) {
		this.newRevisionId = newRevisionId;
	}

	public String getDiffUrl() {
		return diffUrl;
	}

	public void setDiffUrl(String diffUrl) {
		this.diffUrl = diffUrl;
	}

	public String getRevisionClass() {
		return revisionClass;
	}

	public void setRevisionClass(String revisionClass) {
		this.revisionClass = revisionClass;
	}

	public int getAnnotators() {
		return annotators;
	}

	public void setAnnotators(int annotators) {
		this.annotators = annotators;
	}

	public int getTotalAnnotators() {
		return totalAnnotators;
	}

	public void setTotalAnnotators(int totalAnnotators) {
		this.totalAnnotators = totalAnnotators;
	}

	public Timestamp getEditTime() {
		return editTime;
	}

	public void setEditTime(Timestamp editTime) {
		this.editTime = editTime;
	}

	public String getEditComment() {
		return editComment;
	}

	public void setEditComment(String editComment) {
		this.editComment = editComment;
	}

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}
	
	public String getOldRevisionText() {
		return oldRevisionText;
	}

	public void setOldRevisionText(String oldRevisionText) {
		this.oldRevisionText = oldRevisionText;
	}

	public String getNewRevisionText() {
		return newRevisionText;
	}

	public void setNewRevisionText(String newRevisionText) {
		this.newRevisionText = newRevisionText;
	}
	
	public String getOldTokenizedRevisionText() {
		return oldTokenizedRevisionText;
	}

	public void setOldTokenizedRevisionText(String oldTokenizedRevisionText) {
		this.oldTokenizedRevisionText = oldTokenizedRevisionText;
	}

	public String getNewTokenizedRevisionText() {
		return newTokenizedRevisionText;
	}

	public void setNewTokenizedRevisionText(String newTokenizedRevisionText) {
		this.newTokenizedRevisionText = newTokenizedRevisionText;
	}
	
	public Boolean isVandalism() {		
		return revisionClass.equals("vandalism");		
	}	
	
	// --- metadata-based features ---
	
	public Boolean getAnonymousFeature() {				
		Anonymous anonymous = new Anonymous();
		return anonymous.calculate(editor);
	}
	
	public int getCommentLengthFeature() {		
		CommentLength commentLength = new CommentLength();
		return commentLength.calculate(editComment);
	}
	
	public int getSizeIncrementFeature() {		
		SizeIncrement sizeIncrement = new SizeIncrement();
		return sizeIncrement.calculate(oldRevisionText, newRevisionText);
	}
	
	public double getSizeRatioFeature() {		
		SizeRatio sizeRatio = new SizeRatio();
		return sizeRatio.calculate(oldRevisionText, newRevisionText);
	}

	
	// --- text-based features ---
	
	public double getUpperToLowerRatioFeature() {				
		UpperToLowerRatio upperToLowerRatio = new UpperToLowerRatio();
		return upperToLowerRatio.calculate(caseSensitiveConcatenatedInsertedWords);
	}
	
	public double getUpperToAllRatioFeature() {		
		UpperToAllRatio upperToAllRatio = new UpperToAllRatio();
		return upperToAllRatio.calculate(caseSensitiveConcatenatedInsertedWords);
	}
	
	public double getDigitRatioFeature() {		
		DigitRatio digitRatio = new DigitRatio();
		return digitRatio.calculate(caseSensitiveConcatenatedInsertedWords);
	}
		
	public double getNonAlphanumericRatioFeature() {		
		NonAlphanumericRatio nonAlphanumericRatio = new NonAlphanumericRatio();
		return nonAlphanumericRatio.calculate(caseSensitiveConcatenatedInsertedWords);
	}
	
	public double getCharacterDiversityFeature() {		
		CharacterDiversity characterDiversity = new CharacterDiversity();
		return characterDiversity.calculate(insertedText);
	}
	
	public double getCharacterDistributionFeature() {		
		CharacterDistribution characterDistribution = new CharacterDistribution();
		return characterDistribution.calculate(oldRevisionText, insertedText);
	}
	
	public double getCompressibilityFeature() {		
		Compressibility compressibility = new Compressibility();
		return compressibility.calculate(insertedText);
	}
	
	public double getAverageTermFrequencyFeature() {		
		AverageTermFrequency averageTermFrequency = new AverageTermFrequency();
		return averageTermFrequency.calculate(oldTokenizedRevisionText, newTokenizedRevisionText);
	}
	
	public int getLongestWordFeature() {		
		LongestWord longestWord = new LongestWord();
		return longestWord.calculate(insertedText);
	}
	
	public int getLongestCharacterSequenceFeature() {		
		LongestCharacterSequence longestCharacterSequence = new LongestCharacterSequence();
		return longestCharacterSequence.calculate(insertedText);
	}	
	
	public double getGoodTokensFrequencyFeature() {		
		GoodFrequency goodFrequency = new GoodFrequency();
		return goodFrequency.calculate(caseSensitiveConcatenatedInsertedWords);
	}
	
	public double getGoodTokensImpactFeature() {		
		GoodImpact goodImpact = new GoodImpact();
		return goodImpact.calculate(oldTokenizedRevisionText, newTokenizedRevisionText);
	}
	
	// --- language-based features ---
	
	public double getVulgarismFrequencyFeature() {		
		VulgarismFrequency vulgarismFrequency = new VulgarismFrequency();
		return vulgarismFrequency.calculate(caseSensitiveConcatenatedInsertedWords);
	}
	
	public double getPronounsFrequencyFeature() {		
		PronounsFrequency pronounsFrequency = new PronounsFrequency();
		return pronounsFrequency.calculate(caseSensitiveConcatenatedInsertedWords);
	}
	
	public double getBiasedFrequencyFeature() {		
		BiasedFrequency biasedFrequency = new BiasedFrequency();
		return biasedFrequency.calculate(caseSensitiveConcatenatedInsertedWords);
	}

	public double getSexFrequencyFeature() {
		SexFrequency sexFrequency = new SexFrequency();
		return sexFrequency.calculate(caseSensitiveConcatenatedInsertedWords);
	}
		
	public double getBadFrequencyFeature() {		
		BadFrequency badFrequency = new BadFrequency();
		return badFrequency.calculate(caseSensitiveConcatenatedInsertedWords);
	}
	
	public double getAllFrequencyFeature() {		
		AllFrequency allFrequency = new AllFrequency();
		return allFrequency.calculate(caseSensitiveConcatenatedInsertedWords);
	}		

	public double getVulgarismImpactFeature() {		
		VulgarismImpact vulgarismImpact = new VulgarismImpact();
		return vulgarismImpact.calculate(oldTokenizedRevisionText, newTokenizedRevisionText);
	}

	public double getPronounsImpactFeature() {		
		PronounsImpact pronounsImpact = new PronounsImpact();
		return pronounsImpact.calculate(oldTokenizedRevisionText, newTokenizedRevisionText);
	}
	
	public double getBiasImpactFeature() {		
		BiasedImpact biasedImpact = new BiasedImpact();
		return biasedImpact.calculate(oldTokenizedRevisionText, newTokenizedRevisionText);
	}
	
	public double getSexImpactFeature() {		
		SexImpact sexImpact = new SexImpact();
		return sexImpact.calculate(oldTokenizedRevisionText, newTokenizedRevisionText);
	}
	
	public double getBadImpactFeature() {		
		BadImpact badImpact = new BadImpact();
		return badImpact.calculate(oldTokenizedRevisionText, newTokenizedRevisionText);
	}
	
	public double getAllImpactFeature() {		
		AllImpact allImpact = new AllImpact();
		return allImpact.calculate(oldTokenizedRevisionText, newTokenizedRevisionText);
	}
		
	// --- new language-based features ---
	
	public double getRelatedFrequencyFeature() {		
		NewRelatedFrequency newRelatedFrequency = new NewRelatedFrequency();
		return newRelatedFrequency.calculate(caseSensitiveConcatenatedInsertedWords);
	}

	public double getRelatedImpactFeature() {		
		NewRelatedImpact newRelatedImpact = new NewRelatedImpact();
		return newRelatedImpact.calculate(oldTokenizedRevisionText, newTokenizedRevisionText);
	}
	
	public int getWordsInVandalicCluster1Feature(WordClustering wordClustering) {		
		NewWordsInVandalicCluster1 newWordsInVandalicCluster1 = new NewWordsInVandalicCluster1();
		return newWordsInVandalicCluster1.calculate(
			caseSensitiveConcatenatedInsertedWords, wordClustering, false);
	}
	
	public int getWordsInVandalicCluster2Feature(WordClustering wordClustering) {		
		NewWordsInVandalicCluster2 newWordsInVandalicCluster2 = new NewWordsInVandalicCluster2();
		return newWordsInVandalicCluster2.calculate(
			caseSensitiveConcatenatedInsertedWords, wordClustering, false);
	}
	
	public double getPageRankFrequencyFeature() {		
		NewPageRankFrequency newPageRankFrequency = new NewPageRankFrequency();
		return newPageRankFrequency.calculate(caseSensitiveConcatenatedInsertedWords);
	}

	public double getPageRankImpactFeature() {		
		NewPageRankImpact newPageRankImpact = new NewPageRankImpact();
		return newPageRankImpact.calculate(oldTokenizedRevisionText, newTokenizedRevisionText);
	}

	public double getDegreeFrequencyFeature() {		
		NewPageRankFrequency newPageRankFrequency = new NewPageRankFrequency();
		return newPageRankFrequency.calculate(caseSensitiveConcatenatedInsertedWords);
	}

	public double getDegreeImpactFeature() {		
		NewDegreeImpact newDegreeImpact = new NewDegreeImpact();
		return newDegreeImpact.calculate(oldTokenizedRevisionText, newTokenizedRevisionText);
	}
	
	public double getEigenvectorCentralityFrequencyFeature() {		
		NewEigenvectorCentralityFrequency newEigenvectorCentralityFrequency = new NewEigenvectorCentralityFrequency();
		return newEigenvectorCentralityFrequency.calculate(caseSensitiveConcatenatedInsertedWords);
	}

	public double getEigenvectorCentralityImpactFeature() {		
		NewEigenvectorCentralityImpact newEigenvectorCentralityImpact = new NewEigenvectorCentralityImpact();
		return newEigenvectorCentralityImpact.calculate(oldTokenizedRevisionText, newTokenizedRevisionText);
	}
	
	public double getAuthorityFrequencyFeature() {		
		NewAuthorityFrequency newAuthorityFrequency = new NewAuthorityFrequency();
		return newAuthorityFrequency.calculate(caseSensitiveConcatenatedInsertedWords);
	}

	public double getAuthorityImpactFeature() {		
		NewAuthorityImpact newAuthorityImpact = new NewAuthorityImpact();
		return newAuthorityImpact.calculate(oldTokenizedRevisionText, newTokenizedRevisionText);
	}
	
	public void calcTextRepresentations(WordClustering wordClustering1, WordClustering wordClustering2) {
		DiffUtils diffUtils = new DiffUtils();	
		insertedText = diffUtils.calcInsertedText(oldRevisionText, newRevisionText);
		diffUtils.calcInsertedAndDeletedWords(oldTokenizedRevisionText, newTokenizedRevisionText);
		caseSensitiveConcatenatedInsertedWords = diffUtils.getConcatenatedInsertedWords();
	}
}
