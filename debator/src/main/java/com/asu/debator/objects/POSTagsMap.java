package com.asu.debator.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POSTagsMap {

	private static Map<String, Integer> posMap;
	
	private static volatile POSTagsMap instance;
	
	private POSTagsMap(){
		posMap = new HashMap<String, Integer>();
		
		// Adjectives
		posMap.put("JJ", 0);
		posMap.put("JJR", 1);
		posMap.put("JJS", 2);
		
		// Nouns
		posMap.put("NN", 3);
		posMap.put("NNS", 4);
		posMap.put("NNP", 5);
		posMap.put("NNPS", 6);

		// Verbs
		posMap.put("VB", 7);
		posMap.put("VBD", 8);
		posMap.put("VBG", 9);
		posMap.put("VBN", 10);
		posMap.put("VBP", 11);
		posMap.put("VBZ", 12);
		
		// Adverbs
		posMap.put("RB", 13);
		posMap.put("RBR", 14);
		posMap.put("RBS", 15);
		
	}
	
	public static POSTagsMap getInstance(){
		if(instance == null){
			instance = new POSTagsMap();
		}
		return instance;
	}
	
	public List<Integer> convertSentenceToVector(List<NLPTag> sentence){
		List<Integer> vector = initializeVector();
		for(NLPTag term: sentence){
			String pos = term.getPos();
			if(posMap.containsKey(pos)){
				vector.set(posMap.get(pos), vector.get(posMap.get(pos)) + 1);
			}
		}
		return vector;
	}
	
	private List<Integer> initializeVector(){
		List<Integer> vector = new ArrayList<Integer>();
		for(int i=0; i<posMap.keySet().size(); i++){
			vector.add(0);
		}
		return vector;
	}
}
