package com.asu.debator;

import com.asu.debator.classifiers.NaiveBayesClassifier;

public class App {
    
	public static void main( String[] args ){
        NaiveBayesClassifier.trainClassifier();
    }
}
