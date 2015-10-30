package com.asu.debator.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;

public class Tokenizer {

	public static List<String> wordTokenizer(String input){
		List<String> output = new ArrayList<String>();
		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<CoreLabel>(new StringReader(input), 
				new CoreLabelTokenFactory(), "");
		while(ptbt.hasNext())
			output.add(ptbt.next().toString());
		return output;
	}
	
	public static List<String> sentenceTokenizer(String filePath){
		List<String> output = new ArrayList<String>();
		try {
			DocumentPreprocessor docProcessor = new DocumentPreprocessor(new FileReader(filePath));
			 for(List<HasWord> sentence : docProcessor)
				 output.add(Sentence.listToString(sentence).toString());
				
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}
}
