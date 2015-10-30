package com.asu.debator.classifiers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asu.debator.objects.Annotation;
import com.asu.debator.objects.SubjectiveClueSet;
import com.asu.debator.util.Tokenizer;

public class SubjectiveClassifier {

	final static Logger log = LoggerFactory.getLogger(SubjectiveClassifier.class);
	
	/*
	 * Rule based classification as described in the paper:
	 * Learning Extraction Patterns for Subjective Expressions {Riloff, Wiebe}
	 * https://www.cs.utah.edu/~riloff/pdfs/emnlp03.pdf
	 */
	public static Annotation classifySentence(String sentence_left, String sentence, String sentence_right){
		Integer strongClues = getNumOfStrongSubClues(sentence);
		
		Integer weakClues_left = getNumOfWeakSubClues(sentence_left);
		Integer weakClues = getNumOfWeakSubClues(sentence);
		Integer weakClues_right = getNumOfWeakSubClues(sentence_right);
		
		if(strongClues >= 2 || weakClues >=3){
			return Annotation.FACT;
		}else if((weakClues_left + weakClues + weakClues_right) > 1){
			return Annotation.OPINION;
		}else{
			return Annotation.NONE;
		}
	}
	
	public static Integer getNumOfStrongSubClues(String sentence){
		
		Integer strongClues = 0;
		if(sentence == null || sentence.equals("")){
			return strongClues;
		}
		for(String token: Tokenizer.tokenize(sentence)){
			if(SubjectiveClueSet.getInstance().getStrongClues().contains(token)){
				strongClues++;
			}
		}
		return strongClues;
	}
	
	public static Integer getNumOfWeakSubClues(String sentence){
		Integer weakClues = 0; 
		if(sentence == null || sentence.equals("")){
			return weakClues;
		}
		for(String token: Tokenizer.tokenize(sentence)){
			if(SubjectiveClueSet.getInstance().getWeakClues().contains(token)){
				weakClues++;
			}
		}
		return weakClues;
	}
	
	public static void main(String[] args){
		
		String sentence_left = "wasn't there in 3.8.1, which is the most common version of 3.x still out there.";
		String sentence = "wasn't there in 3.8.1, which is the most common version of 3.x still out there.";
		String sentence_right = "wasn't there in 3.8.1, which is the most common version of 3.x still out there.";
		
		log.info("sentence_left -> Strong -> {}", getNumOfStrongSubClues(sentence_left));
		log.info("sentence -> Strong {}", getNumOfStrongSubClues(sentence));
		log.info("sentence_right -> Strong -> {}", getNumOfStrongSubClues(sentence_right));
		
		log.info("sentence_left -> Weak -> {}", getNumOfWeakSubClues(sentence_left));
		log.info("sentence -> Weak -> {}", getNumOfWeakSubClues(sentence));
		log.info("sentence_right -> Weak -> {}", getNumOfWeakSubClues(sentence_right));
		
		log.info("Classification - > {}", classifySentence(sentence_left, sentence, sentence_right));
	}
	
}
