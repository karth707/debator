package com.asu.debator.objects;

import com.google.gson.Gson;

public class NLPTag {

	private String word;

	private String pos;

	public NLPTag(String word) {
		setWord(word);
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getWord() {
		return word;
	}

	private void setWord(String word) {
		if (word.equals("-LRB-")) {
			this.word = "(";
		} else if (word.equals("-RRB-")) {
			this.word = ")";
		} else if (word.equals("-LSB-")) {
			this.word = "[";
		} else if (word.equals("-RSB-")) {
			this.word = "]";
		} else {
			this.word = word;
		}
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof NLPTag)) {
			return false;
		}
		return toString().equals(obj.toString());
	}

	@Override
	public String toString() {
		return new Gson().toJson(this).toString();
	}
}
