package com.asu.debator.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.EndingPreProcessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class VectorConvertor {

	private static final String INPUT_PATH = "C:\\Users\\spid\\Desktop\\NLPCorpora_Tagged\\_tagged.txt";//"/Users/KartheekGanesh/Desktop/_tagged.txt";
	private static final String GENERATED_VECTORS = "C:\\Users\\spid\\Documents\\GitHub\\debator\\Files\\trainedWord2Vec.txt";
	private final int VEC_DIMENTION = 50;
	private Double smoothingNum = null;
	
	public static volatile VectorConvertor instance;
	private Map<String, double[]> wordVectors;
	
	private Logger logger = LoggerFactory.getLogger(VectorConvertor.class);
	
	public VectorConvertor(){
		wordVectors = new HashMap<String, double[]>();
		if(new File(GENERATED_VECTORS).exists()){
			logger.info("Populating generated vectors....");
			populateWordVectors();
		}else{
			logger.info("Generating vectors....");
			generateWord2Vec();
		}
	}
	
	public static VectorConvertor getInstance(){
		if(instance==null){
			instance = new VectorConvertor();
		}
		return instance;
	}
	
	private void generateWord2Vec(){
		SentenceIterator iter = new LineSentenceIterator(new File(INPUT_PATH));
	    iter.setPreProcessor(new SentencePreProcessor() {
			private static final long serialVersionUID = -5927284347463251008L;
			@Override
	        public String preProcess(String sentence) {
				if(sentence==null || sentence.length()==0) return null;
				String line = sentence.split("<-->")[0];
				return (line == null || line.length()==0) ? null : line.toLowerCase(); 
	        }
	    });
	    
	    final EndingPreProcessor preProcessor = new EndingPreProcessor();
	    TokenizerFactory tokenizer = new DefaultTokenizerFactory();
	    tokenizer.setTokenPreProcessor(new TokenPreProcess() {
	        @Override
	        public String preProcess(String token) {
	            String base = preProcessor.preProcess(token);
	            return base;
	        }
	    });
	    
	    int batchSize = 100;
	    int iterations = 30;
	    int layerSize = VEC_DIMENTION;
	    
	    Word2Vec vec = new Word2Vec.Builder()
	            .batchSize(batchSize) 			//# words per minibatch. 
	            .sampling(1e-5) 				// negative sampling. drops words out
	            .minWordFrequency(2) 			// 
	            .useAdaGrad(false) 				//
	            .layerSize(layerSize) 			// word feature vector size
	            .iterations(iterations) 		// # iterations to train
	            .learningRate(0.025) 			// 
	            .minLearningRate(1e-2) 			// learning rate decays wrt # words. floor learning
	            .negativeSample(10) 			// sample size 10 words
	            .iterate(iter) 					//
	            .tokenizerFactory(tokenizer)
	            .build();
	    try {
			vec.fit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    for(VocabWord vWord: vec.vocab().vocabWords()){
	    	wordVectors.put(vWord.getWord(), vec.getWordVector(vWord.getWord()));
	    }
	}
	
	private void populateWordVectors() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(GENERATED_VECTORS)));
			String line;
			while((line = reader.readLine())!=null){
				String[] vectorArray = line.split(" ");
				if(vectorArray.length==VEC_DIMENTION+1){
					double[] vectorValues = getVecVals(vectorArray);
					wordVectors.put(vectorArray[0], vectorValues);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private double[] getVecVals(String[] vectorArray) {
		double[] vector = new double[VEC_DIMENTION];
		for(int i=1; i<VEC_DIMENTION+1; i++){
			vector[i-1] = Double.parseDouble(vectorArray[i]);
		}
		return vector;
	}

	public double[] getWordVector(String word){
		if(wordVectors.containsKey(word)){
			return wordVectors.get(word);
		}else if(wordVectors.containsKey(word.toLowerCase())){
			return wordVectors.get(word.toLowerCase());
		}else{
			if(smoothingNum==null){
				smoothingNum = 1.0/wordVectors.size();
			}
			double[] soothingArray = new double[50];
			Arrays.fill(soothingArray, smoothingNum);
			return soothingArray;
		}
	}
	
	public static void main(String[] args){
		BasicConfigurator.configure();
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);
		Scanner in = new Scanner(System.in);
		String line;
		System.out.print("-->");
		while((line = in.next())!="exit"){
			double[] vector = VectorConvertor.getInstance().getWordVector(line);
			System.out.println("vector: " + new Gson().toJson(vector));
			System.out.print("-->");
		}
		in.close();
	}
}
