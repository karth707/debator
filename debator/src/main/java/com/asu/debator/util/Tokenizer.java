package com.asu.debator.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

public class Tokenizer {

	public static List<String> tokenize(String input){
		List<String> output = new ArrayList<String>();
		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<CoreLabel>(new StringReader(input), 
				new CoreLabelTokenFactory(), "");
		while(ptbt.hasNext())
			output.add(ptbt.next().toString());
		return output;
	}
}
