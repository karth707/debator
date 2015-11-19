package com.asu.debator.classifiers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asu.debator.objects.AnnotatedWord;
import com.asu.debator.objects.Annotation;
import com.asu.debator.util.Tokenizer;

public class NaiveBayesClassifier {
	
	private static Logger logger = LoggerFactory.getLogger(NaiveBayesClassifier.class);
	
	private String TRAIN_FILE_PATH;
	private String ANNOTATION_DELIMITER = "<-->";
	private Map<AnnotatedWord, Integer> trainedAnnotatedData = new HashMap<AnnotatedWord, Integer>();
	private Map<String, Integer> trainedWords = new HashMap<String, Integer>();
	private Map<AnnotatedWord, Double> probabilities = new HashMap<AnnotatedWord, Double>();
	private int vocabularySize = 0;
	
	public NaiveBayesClassifier(String trainingPath){
		TRAIN_FILE_PATH = trainingPath;
	}
	
	public void trainClassifier(){
		
		try {
			Files.walk(Paths.get(TRAIN_FILE_PATH)).forEach(input -> {
			    if (Files.isRegularFile(input)) {
			    	if(!input.getFileName().toString().equals(".DS_Store")){
			    		logger.info("Processing: {}", input.getFileName().toString());
			    		readData(input.toString());
			    	}
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (AnnotatedWord key : trainedAnnotatedData.keySet()) {
			vocabularySize += trainedAnnotatedData.get(key);
		}
		for (AnnotatedWord key : trainedAnnotatedData.keySet()) {
			probabilities.put(key, (trainedAnnotatedData.get(key) + 1)
					/ (double) (trainedWords.get(key.getWord()) + vocabularySize + 1));
		}
		logger.info("trainedAnnotatedData: {}; trainedWords: {};  vocabularySize: {}"
					, trainedAnnotatedData.size(), trainedWords.size(), vocabularySize);
		
		//logger.info("trainedAnnotatedData: {}", trainedAnnotatedData.toString());
		//logger.info("probabilities: {}", probabilities.toString());
	}
	
	private void readData(String input) {
		BufferedReader inputBufferedReader = null;
		try {
			inputBufferedReader = new BufferedReader(new FileReader(input));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String sentence = "", text = "", annotation = "";
		try {
			while ((sentence = inputBufferedReader.readLine()) != null) {
				if(sentence.equals("") || sentence.split(" ").length<3) continue;
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
	
	public void classify(String input, String outputPath) {

		List<String> testSentences = Tokenizer.sentenceTokenizer(input);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(outputPath)));
		
			for(String nextInput : testSentences)
			{
				Double opinionProbability = 1.0;
				Double factProbability = 1.0;
				Double noneProbability = 1.0;
				List<String> testDataTokens = Tokenizer.wordTokenizer(nextInput);
				if(nextInput.equals("") || nextInput.split(" ").length<3) continue;
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
					try {
						if(factProbability > opinionProbability && factProbability > noneProbability)
							writer.write(nextInput + ANNOTATION_DELIMITER + Annotation.FACT.toString() + System.lineSeparator());
						else if(opinionProbability > factProbability && opinionProbability > noneProbability)
							writer.write(nextInput + ANNOTATION_DELIMITER + Annotation.OPINION.toString() + System.lineSeparator());
						else
							writer.write(nextInput + ANNOTATION_DELIMITER + Annotation.NONE.toString() + System.lineSeparator());
					} catch (IOException e) {
						e.printStackTrace();
					}
					//logger.info("F: {}; O: {}, N: {}", factProbability, opinionProbability, noneProbability);
				
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addOrIncrement(AnnotatedWord word){
		if(trainedAnnotatedData.containsKey(word))
			trainedAnnotatedData.put(word, trainedAnnotatedData.get(word)+1);
		else
			trainedAnnotatedData.put(word, 1);
	}
	
	public static void main(String[] args){
		
		BasicConfigurator.configure();
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);
		
		String trainingPath = args[0];
		String testingPath = args[1];
		String outputPath = args[2] + "\\";

		NaiveBayesClassifier nbc = new NaiveBayesClassifier(trainingPath);
		nbc.trainClassifier();
		try {
			Files.walk(Paths.get(testingPath)).forEach(input -> {
			    if (Files.isRegularFile(input)) {
			    	if(!input.getFileName().toString().equals(".DS_Store")){
				    	logger.info("Processing: {}", input.getFileName().toString());
				    	nbc.classify(input.toString(), outputPath 
				    			+ input.getFileName().toString().split(".txt")[0]
				    			+ "_naivebayes.txt");
			    	}
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }
}
