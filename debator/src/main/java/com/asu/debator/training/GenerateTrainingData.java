package com.asu.debator.training;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asu.debator.classifiers.SubjectiveClassifier;
import com.asu.debator.objects.Annotation;
import com.asu.debator.util.Tokenizer;

public class GenerateTrainingData {

	private static Logger logger = LoggerFactory.getLogger(GenerateTrainingData.class);
	
	private static final String DELIMITER = "<-->";
	
	public static void generate(String inputPath, String outputPath){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputPath)));
			List<String> inputSentences = Tokenizer.sentenceTokenizer(inputPath);
			if(inputSentences.size()<3){
				logger.error("Not enough training data");
				writer.close();
				return;
			}
			for(int index=0; index<inputSentences.size(); index++){
				String sentence_left = (index==0) ? null : inputSentences.get(index-1);
				String sentence = inputSentences.get(index);
				if(sentence.split(" ").length < 3){
					continue;
				}
				String sentence_right = (index==inputSentences.size()-1) ? null : inputSentences.get(index+1);
				Annotation annotation = SubjectiveClassifier.classifySentence(sentence_left, sentence, sentence_right);
				writer.write(sentence + DELIMITER + annotation.toString() + "\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		BasicConfigurator.configure();
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);
		String outputPath = "/Users/KartheekGanesh/Desktop/NLPCorpora_Tagged/";
		try {
			Files.walk(Paths.get("/Users/KartheekGanesh/Desktop/NLPCorpora")).forEach(inputPath -> {
			    if (Files.isRegularFile(inputPath)) {
			    	logger.info("Processing: {}", inputPath.getFileName().toString());
			    	GenerateTrainingData.generate(inputPath.toString(), outputPath 
			    			+ inputPath.getFileName().toString().split(".txt")[0]
			    			+ "_tagged.txt");
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
