package com.asu.debator.util;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POSTagger {

	private static MaxentTagger tagger = new MaxentTagger("taggers/left3words-distsim-wsj-0-18.tagger");

	public static String tagSentence(String sentence){
		return tagger.tagString(sentence);
	}
	
	public static void main(String[] args){
		POSTagger.tagSentence("I am batman. I hate superman");
	}
}
