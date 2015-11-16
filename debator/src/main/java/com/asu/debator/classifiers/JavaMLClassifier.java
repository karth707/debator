package com.asu.debator.classifiers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.asu.debator.objects.Annotation;
import com.asu.debator.util.Tokenizer;
import com.asu.debator.util.VectorConvertor;

import libsvm.LibSVM;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

public class JavaMLClassifier {

	private final static String TRAINING_INPUT_PATH = "C:\\Users\\spid\\Desktop\\NLPCorpora_Tagged\\_tagged.txt";
	private final static String TESTING_INPUT_PATH = "C:\\Users\\spid\\Desktop\\NLPCorpora_Tagged\\_tagged.txt";
	private static final String TESTING_OUTPUT_PATH = "C:\\Users\\spid\\Desktop\\NLPCorpora_Tagged\\_tagged_output.txt";
	
	static Dataset data = new DefaultDataset();
	static Classifier svmClassifier;
	
	private static void train() {
		VectorConvertor vc = new VectorConvertor();
		List<Instance> instances = new ArrayList<Instance>();
		List<String> sentences = Tokenizer.sentenceTokenizer(TRAINING_INPUT_PATH);
		for(String sentence : sentences){
			double[] sentenceVector = new double[50];
			List<String> words = Tokenizer.wordTokenizer(sentence.split("<-->")[0]);
			for(String word : words){
				double[] wordVector = vc.getWordVector(word);
				for(int i = 0; i < wordVector.length; i++)
					sentenceVector[i] += wordVector[i];
			}
			for(int i = 0; i < sentenceVector.length; i++)
				sentenceVector[i] /= words.size();
			instances.add(new DenseInstance(sentenceVector, Annotation.getAnnotation(sentence.split("<-->")[1])));
		}
		data.addAll(instances);
	}
	
	private static void test() {
		VectorConvertor vc = new VectorConvertor();
		List<Instance> instances = new ArrayList<Instance>();
		List<String> sentences = Tokenizer.sentenceTokenizer(TESTING_INPUT_PATH);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(TESTING_OUTPUT_PATH));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String sentence : sentences){
			double[] sentenceVector = new double[50];
			List<String> words = Tokenizer.wordTokenizer(sentence.split("<-->")[0]);
			for(String word : words){
				double[] wordVector = vc.getWordVector(word);
				for(int i = 0; i < wordVector.length; i++)
					sentenceVector[i] += wordVector[i];
			}
			for(int i = 0; i < sentenceVector.length; i++)
				sentenceVector[i] /= words.size();
			//instances.add(new DenseInstance(sentenceVector));
			Annotation classValue = (Annotation) svmClassifier.classify(new DenseInstance(sentenceVector));
			try {
				writer.write(sentence + "<-->" + classValue.toString() + System.lineSeparator());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args){
		
		train();
		svmClassifier = new LibSVM();
		svmClassifier.buildClassifier(data);
		test();
	}


	
	
}
