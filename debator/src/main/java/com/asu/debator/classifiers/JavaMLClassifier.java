package com.asu.debator.classifiers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.slf4j.LoggerFactory;

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

	private final static String TRAINING_INPUT_PATH = "C:\\Users\\spid\\Downloads\\corpora\\corpora\\NLP_SVM_Training\\_tagged_training.txt";
	private final static String TESTING_INPUT_PATH = "C:\\Users\\spid\\Downloads\\corpora\\corpora\\NLP_SVM_Test\\_tagged.txt";
	private static final String TESTING_OUTPUT_PATH = "C:\\Users\\spid\\Downloads\\corpora\\corpora\\NLP_SVM_Test_Output\\_tagged_output.txt";

	static Dataset data = new DefaultDataset();
	
	static org.slf4j.Logger logger = LoggerFactory.getLogger(JavaMLClassifier.class);

	private static void train() {
		List<Instance> instances = new ArrayList<Instance>();
		List<String> sentences = getSentencesFromFile(TRAINING_INPUT_PATH);
		for (String sentence : sentences) {
			double[] sentenceVector = new double[50];
			List<String> words = Tokenizer.wordTokenizer(sentence.split("<-->")[0]);
			for (String word : words) {
				double[] wordVector = VectorConvertor.getInstance().getWordVector(word);
				for (int i = 0; i < wordVector.length; i++)
					sentenceVector[i] += wordVector[i];
			}
			for (int i = 0; i < sentenceVector.length; i++)
				sentenceVector[i] /= words.size();
			instances.add(new DenseInstance(sentenceVector, Annotation.getAnnotation(sentence.split("<-->")[1])));
		}
		data.addAll(instances);
	}

	private static List<String> getSentencesFromFile(String filePath) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line = "";
		List<String> sentences = new ArrayList<String>();
		try {
			while ((line = br.readLine()) != null) {
				if (Tokenizer.wordTokenizer(line).size() > 4)
					sentences.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sentences;
	}

	private static float test() {

		Classifier svmClassifier = new LibSVM();
		svmClassifier.buildClassifier(data);
		logger.info("Classifier built");
		List<String> sentences = getSentencesFromFile(TESTING_INPUT_PATH);
		int correctCount = 0, wrongCount = 0;
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(TESTING_OUTPUT_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String sentence : sentences) {
			double[] sentenceVector = new double[50];
			List<String> words = Tokenizer.wordTokenizer(sentence.split("<-->")[0]);
			for (String word : words) {
				double[] wordVector = VectorConvertor.getInstance().getWordVector(word);
				for (int i = 0; i < wordVector.length; i++)
					sentenceVector[i] += wordVector[i];
			}
			for (int i = 0; i < sentenceVector.length; i++)
				sentenceVector[i] /= words.size();
			Annotation classValue = (Annotation) svmClassifier.classify(new DenseInstance(sentenceVector));
			Annotation actualClassValue = Annotation.getAnnotation(sentence.split("<-->")[1]);
			if (classValue.equals(actualClassValue)) {
				correctCount++;
			} else {
				wrongCount++;
			}
			try {
				writer.write(sentence.split("<-->")[0] + "<-->" + classValue.toString() + System.lineSeparator());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return correctCount / (float) (correctCount + wrongCount);

	}

	public static void main(String[] args) {

		BasicConfigurator.configure();
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);
		train();
		System.out.println("Trained");
		System.out.println("Accuracy - " + test());
	}

}
