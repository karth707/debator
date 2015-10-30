package com.asu.debator.classifiers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import com.asu.debator.objects.AnnotatedWord;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

public class NaiveBayesClassifier {

	
	private static String TRAIN_FILE_PATH = "C:\\Users\\spid\\Desktop\\banHomeWork.txt";
	private static String ANNOTATION_DELIMITER = "<-->";
	private static Map<AnnotatedWord, Integer> trainedData = new HashMap<AnnotatedWord, Integer>();
	
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
			while((sentence = inputBufferedReader.readLine()) != null){
				text = sentence.split(ANNOTATION_DELIMITER)[0];
				annotation = sentence.split(ANNOTATION_DELIMITER)[1];
				PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<CoreLabel>(new StringReader(text), 
																			new CoreLabelTokenFactory(), "");
				while(ptbt.hasNext())
					addOrIncrement(new AnnotatedWord(ptbt.next(), annotation));
			}
			System.out.println(trainedData.size());
			System.out.println(trainedData.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addOrIncrement(AnnotatedWord word){
		if(trainedData.containsKey(word))
			trainedData.put(word, trainedData.get(word)+1);
		else
			trainedData.put(word, 1);
	}
	
}
