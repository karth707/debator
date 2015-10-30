package com.asu.debator.classifiers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.asu.debator.objects.AnnotatedWord;
import com.asu.debator.util.Tokenizer;

public class NaiveBayesClassifier {

	
	private static String TRAIN_FILE_PATH = "C:\\Users\\spid\\Desktop\\banHomeWork.txt";
	private static String TEST_FILE_PATH = "C:\\Users\\spid\\Desktop\\banHomeWorkTest.txt";
	private static String ANNOTATION_DELIMITER = "<-->";
	private static Map<AnnotatedWord, Integer> trainedAnnotatedData = new HashMap<AnnotatedWord, Integer>();
	private static Map<String, Integer> trainedWords = new HashMap<String, Integer>();
	private static Map<AnnotatedWord, Double> probabilities = new HashMap<AnnotatedWord, Double>();
	
	public static void trainClassifier(){
	
		BufferedReader inputBufferedReader = null;
		try {
			inputBufferedReader = new BufferedReader(new FileReader(TRAIN_FILE_PATH));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sentence = "", text = "", annotation = "";
		
		try {
			int count = 0;
			while((sentence = inputBufferedReader.readLine()) != null){
				text = sentence.split(ANNOTATION_DELIMITER)[0];
				annotation = sentence.split(ANNOTATION_DELIMITER)[1];
				List<String> tokens = Tokenizer.tokenize(text);
				for(String label : tokens){
					addOrIncrement(new AnnotatedWord(label, annotation));
					if(trainedWords.containsKey(label))
							trainedWords.put(label.toString(), trainedWords.get(label) + 1);
					else
						trainedWords.put(label.toString(), 1);
				}
					
			}
			for(AnnotatedWord key : trainedAnnotatedData.keySet()){
				count += trainedAnnotatedData.get(key);
			}
			for(AnnotatedWord key : trainedAnnotatedData.keySet()){
				probabilities.put(key, (trainedAnnotatedData.get(key) + 1)/(double)(trainedWords.get(key.getWord()) + trainedAnnotatedData.size() + 1));
			}
			
			System.out.println(trainedAnnotatedData.size() + " - " + trainedWords.size() + " - " + count);
			System.out.println(trainedAnnotatedData.toString());
			System.out.println(probabilities.toString());
			
			classify();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void classify() {

		BufferedReader brTest = null;
		try {
			brTest = new BufferedReader(new FileReader(TEST_FILE_PATH));
			
			
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	}

	public static void addOrIncrement(AnnotatedWord word){
		if(trainedAnnotatedData.containsKey(word))
			trainedAnnotatedData.put(word, trainedAnnotatedData.get(word)+1);
		else
			trainedAnnotatedData.put(word, 1);
	}
	
}
