package com.asu.debator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.asu.debator.objects.NLPTag;
import com.asu.debator.objects.POSTagsMap;
import com.google.gson.Gson;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class POSTagger {

	private static final StanfordCoreNLP pipeline;
	
	private static Gson gson = new Gson();
	
	static {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos");
		pipeline = new StanfordCoreNLP(props);
	}
	
	public static List<NLPTag> tag(String text) {
		List<NLPTag> nlpTags = new ArrayList<NLPTag>();
		if (text == null) {
			return nlpTags;
		}

		Annotation document = new Annotation(text);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				NLPTag tag = new NLPTag(word);
				String pos = token.get(PartOfSpeechAnnotation.class);
				tag.setPos(pos);
				nlpTags.add(tag);
			}
		}
		return nlpTags;
	}

	public static void main(String[] args){
		List<NLPTag> sentenceTags1 = POSTagger.tag("I am batman. I hate superman");
		System.out.println(gson.toJson(sentenceTags1));
		System.out.println("vec1: " + POSTagsMap.getInstance().convertSentenceToVector(sentenceTags1));
		
		List<NLPTag> sentenceTags2 = POSTagger.tag("I am unable to find an official list.");
		System.out.println(gson.toJson(sentenceTags2));
		System.out.println("vec2: " + POSTagsMap.getInstance().convertSentenceToVector(sentenceTags2));
		
		List<NLPTag> sentenceTags3 = POSTagger.tag("looking for nouns, for instance, I end up doing something {} ");
		System.out.println(gson.toJson(sentenceTags3));
		System.out.println("vec3: " + POSTagsMap.getInstance().convertSentenceToVector(sentenceTags3));
	}
}
