package jrmr.vds.model.ext.word2vec;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WordSimilarity {

	private final StringProperty word;	
	private final FloatProperty score;
	
	public WordSimilarity() {
		this(null, null);
	}
	
	public WordSimilarity(String word, Float score) {
		this.word = new SimpleStringProperty(word);
		this.score = new SimpleFloatProperty(score);
	}
	
    public String getWord() {
        return word.get();
    }

    public void setWord(String word) {
        this.word.set(word);
    }

    public StringProperty wordProperty() {
        return word;
    }
    
    public float getScore() {
        return score.get();
    }

    public void setScore(float score) {
        this.score.set(score);
    }

    public FloatProperty scoreProperty() {
        return score;
    }
}
