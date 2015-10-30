package com.asu.debator.classifiers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.asu.debator.objects.AnnotatedWord;
import com.asu.debator.objects.Annotation;
import com.asu.debator.util.Tokenizer;

public class NaiveBayesClassifier {
	
	private static String TRAIN_FILE_PATH = "C:\\Users\\spid\\Desktop\\banHomeWork.txt";
	private static String TEST_FILE_PATH = "C:\\Users\\spid\\Desktop\\banHomeWorkTest.txt";
	private static String ANNOTATION_DELIMITER = "<-->";
	private static Map<AnnotatedWord, Integer> trainedAnnotatedData = new HashMap<AnnotatedWord, Integer>();
	private static Map<String, Integer> trainedWords = new HashMap<String, Integer>();
	private static Map<AnnotatedWord, Double> probabilities = new HashMap<AnnotatedWord, Double>();
	private static int vocabularySize = 0;
	public static void trainClassifier(){
	
		readData();
		
		for (AnnotatedWord key : trainedAnnotatedData.keySet()) {
			vocabularySize += trainedAnnotatedData.get(key);
		}
		for (AnnotatedWord key : trainedAnnotatedData.keySet()) {
			probabilities.put(key, (trainedAnnotatedData.get(key) + 1)
					/ (double) (trainedWords.get(key.getWord()) + vocabularySize + 1));
		}
		System.out.println(trainedAnnotatedData.size() + " - " + trainedWords.size() + " - " + vocabularySize);
		System.out.println(trainedAnnotatedData.toString());
		System.out.println(probabilities.toString());
		classify();
		
	}
	
	private static void readData() {
		BufferedReader inputBufferedReader = null;
		try {
			inputBufferedReader = new BufferedReader(new FileReader(TRAIN_FILE_PATH));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String sentence = "", text = "", annotation = "";
		try {

			while ((sentence = inputBufferedReader.readLine()) != null) {
				text = sentence.split(ANNOTATION_DELIMITER)[0];
				annotation = sentence.split(ANNOTATION_DELIMITER)[1];
				List<String> tokens = Tokenizer.wordTokenizer(text);
				for (String label : tokens) {
					addOrIncrement(new AnnotatedWord(label, annotation));
					if (trainedWords.containsKey(label))
						trainedWords.put(label.toString(), trainedWords.get(label) + 1);
					else
						trainedWords.put(label.toString(), 1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void classify() {

		List<String> testSentences = Tokenizer.sentenceTokenizer(TEST_FILE_PATH);
			List<Annotation> result = new ArrayList<Annotation>(); 
			for(String nextInput : testSentences)
			{
				Double opinionProbability = 1.0;
				Double factProbability = 1.0;
				Double noneProbability = 1.0;
				List<String> testDataTokens = Tokenizer.wordTokenizer(nextInput);
				for(String token : testDataTokens)
				{
					if(probabilities.containsKey(new AnnotatedWord(token, Annotation.FACT)))
						factProbability *= probabilities.get(new AnnotatedWord(token, Annotation.FACT));
					else
						factProbability *= (1/(double) (((null == trainedWords.get(token)) ? trainedWords.size() : trainedWords.get(token)) + vocabularySize + 1));
					if(probabilities.containsKey(new AnnotatedWord(token, Annotation.OPINION)))
						opinionProbability *= probabilities.get(new AnnotatedWord(token, Annotation.OPINION));
					else
						opinionProbability *= (1/(double) (((null == trainedWords.get(token)) ? trainedWords.size() : trainedWords.get(token)) + vocabularySize + 1));
					if(probabilities.containsKey(new AnnotatedWord(token, Annotation.NONE)))
						noneProbability *= probabilities.get(new AnnotatedWord(token, Annotation.NONE));
					else
						noneProbability *= (1/(double) (((null == trainedWords.get(token)) ? trainedWords.size() : trainedWords.get(token)) + vocabularySize + 1));
				}
				if(factProbability > opinionProbability && factProbability > noneProbability)
					result.add(Annotation.FACT);
				else if(opinionProbability > factProbability && opinionProbability > noneProbability)
					result.add(Annotation.OPINION);
				else
					result.add(Annotation.NONE);
			}
			System.out.println(result.toString());
	}

	public static void addOrIncrement(AnnotatedWord word){
		if(trainedAnnotatedData.containsKey(word))
			trainedAnnotatedData.put(word, trainedAnnotatedData.get(word)+1);
		else
			trainedAnnotatedData.put(word, 1);
	}
	
}
