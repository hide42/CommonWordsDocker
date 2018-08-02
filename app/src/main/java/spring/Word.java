package spring;

import java.io.Serializable;

/**
 * Word class to represent row in Cassandra
 * 
 * @author abaghel
 *
 */
public class Word implements Serializable{
	public Word(String word, Integer count) {
		this.word = word;
		this.count = count;
	}

	String word;
    Integer count;

	public Word() {
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
}
