package com.asu.debator.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.morph.WordnetStemmer;

public class Dictionary {

	final static Logger log = LoggerFactory.getLogger(Dictionary.class);

	private IDictionary dict;
	private WordnetStemmer stemmer;
	
	private Set<String> stopwords;

	private static final String stopwordsDir = "/stopwords.en";
	
	public Dictionary(String dictionaryLocation){
		URL url = null;
		try{ 
			/* Open the WordNet Dictionary */
			url = new URL("file", null, dictionaryLocation); 			
			dict = new edu.mit.jwi.Dictionary(url);
			dict.open();
			
			/* Word-net stemmer */
			stemmer = new WordnetStemmer(dict);
			
			/* Open and read the stop words */
			stopwords = readFromFile(stopwordsDir);
			
		}catch(IOException e){
			log.error(e.getMessage(), e); 
			log.error("Either the dictionary location is invalid...");
		}
	}
	
	private Set<String> readFromFile(String fileName) throws IOException {
		Set<String> termSet = new HashSet<String>();
		InputStream fileStream = Dictionary.class.getResourceAsStream(fileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
		String line = null;
		while((line = reader.readLine())!=null){
			termSet.add(line.toLowerCase());
		}
		reader.close();
		return termSet;
	}
	
	public String getStemmedTerm(String term){
		List<String> stemmedterms = new ArrayList<String>();
		if(term.equals("")){
			return term;
		}
		stemmedterms.addAll(stemmer.findStems(term, POS.ADJECTIVE));
		stemmedterms.addAll(stemmer.findStems(term, POS.ADVERB));
		stemmedterms.addAll(stemmer.findStems(term, POS.NOUN));
		stemmedterms.addAll(stemmer.findStems(term, POS.VERB));
		if(stemmedterms.size()!=0){
			return stemmedterms.get(0);
		}else{
			return term;
		}
	}
	
	/* Find out if a word exists in the English Dictionary */
	public boolean isDictionaryWord(String word){
		Set<IIndexWord> words = new HashSet<IIndexWord>();
		words.addAll(Arrays.asList(dict.getIndexWord(word, POS.ADJECTIVE)
				, dict.getIndexWord(word, POS.ADVERB), dict.getIndexWord(word, POS.NOUN)
				, dict.getIndexWord(word, POS.VERB)));
		
		if(words.size()==1 & words.contains(null)){
			return false;
		}else{
			return true;
		}
	}
	
	/* Find out if a word is a stop word */
	public boolean isStopWord(String input) {
		if(stopwords.contains(input)){
			return true;
		}else{
			return false;
		}
	}
	
	/* True if there exists at-least one dictionary term in the input set */
	public boolean atleastOneDictionaryWord(Set<String> words){
		for(String word: words){
			if(word==null || word.length()==0){
				continue;
			}
			if(this.isDictionaryWord(word)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isValidDictionaryWord(String word) {
		/* no stop words */
		if(stopwords.contains(word)){
			return true;
		}
		/* Essential to check length before we hit the dictionary */
		if(word.length() < 3){
			return false;
		}
		if(isDictionaryWord(word)){
			return true;
		}
		return false;
	}
	
	
	/*
	 * Generates Hypernyms or Hyponyms based on input
	 * type = 1 indicates hypernums
	 * type = 2 indicates hyponyms  
	 */
	public Set<String> generateSemanticTerms(String term, int type){
		Set<String> semanticTerms = new LinkedHashSet<String>();
		
		IIndexWord idxWord = dict.getIndexWord(term, POS.NOUN);
		if(idxWord==null){
			return semanticTerms;
		}
		IWordID wordId = idxWord.getWordIDs().get(0);
		IWord word = dict.getWord(wordId);
		ISynset synset = word.getSynset(); 
		
		// get the Hypernyms or Hyponyms
		List<ISynsetID> relatedSynsets;
		if(type==1){
			relatedSynsets = synset.getRelatedSynsets(Pointer.HYPERNYM);
		}else{
			relatedSynsets = synset.getRelatedSynsets(Pointer.HYPONYM);
		}
		for(ISynsetID sid : relatedSynsets){
			for(IWord iword: dict.getSynset(sid).getWords()){
				String cleanWord = iword.getLemma().replace("_", " ");
				if(isDictionaryWord(cleanWord)){
					if(!term.toLowerCase().equals(cleanWord.toLowerCase())){
						semanticTerms.add(cleanWord);
					}
				}
			}
		}
		return semanticTerms;
	}
	
	public Set<String> getPOS(String term){
		Set<IIndexWord> indexWords = new HashSet<IIndexWord>();
		indexWords.addAll(Arrays.asList(dict.getIndexWord(term, POS.NOUN)
				, dict.getIndexWord(term, POS.ADVERB)
				, dict.getIndexWord(term, POS.ADJECTIVE)
				, dict.getIndexWord(term, POS.VERB)
				));
		Set<String> pos = new HashSet<String>();
		for(IIndexWord indexWord: indexWords){
			if(indexWord==null){
				continue;
			}
			IWordID wordId = indexWord.getWordIDs().get(0);
			IWord word = dict.getWord(wordId);
			pos.add(word.getPOS().toString());
		}
		return pos;
	}
	
	
	/* Generates Synonyms for an input word */
	public Set<String> generateSynonyms(String word){
		
		Set<String> synonyms = new LinkedHashSet<String>();
		if(word.length() < 3){
			return synonyms;
		}
		Set<IIndexWord> indexWords = new HashSet<IIndexWord>();
		
		/* Ideally, over here, POS must be found first, instead of using all the possible POS 
		 * Using only nouns
		 */		 
		indexWords.addAll(Arrays.asList(dict.getIndexWord(word, POS.NOUN)
//				, dict.getIndexWord(word, POS.ADVERB)
//				, dict.getIndexWord(word, POS.ADJECTIVE)
//				, dict.getIndexWord(word, POS.VERB)
				));

		for(IIndexWord indexWord: indexWords){
			if(indexWord==null){
				continue;
			}
			IWordID wordID = indexWord.getWordIDs().get(0);
			ISynset synset = dict.getWord(wordID).getSynset();
			for(IWord iword: synset.getWords()){
				String cleanWord = iword.getLemma().replace("_", " ");
				if(isDictionaryWord(cleanWord)){
					if(!word.toLowerCase().equals(cleanWord.toLowerCase())){
						synonyms.add(cleanWord);
					}
				}
			}
		}
		return synonyms;
	}
	
	public List<Set<String>> getListOfSynSets(Set<String> terms) {
		List<Set<String>> synsetList = new ArrayList<Set<String>>();
		for(String term: terms){
			synsetList.add(generateSynonyms(term));
		}
		return synsetList;
	}
	
	public static void main(String[] args){
		
		BasicConfigurator.configure();
		org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);
		
		// args[0] = wordnet/dict location
		
		Dictionary dict = new Dictionary(args[0]);
		
		// Stemmer Test
		log.info("{}", dict.getPOS("athlete"));
		log.info("{}", dict.getPOS("awesome"));
		log.info("{}", dict.getPOS("play"));
		log.info("{}", dict.getPOS("by"));
		
		
//		log.info("{}", dict.isDictionaryWord("discounts"));
//		log.info("{}", dict.isDictionaryWord("and"));
//		log.info("{}", dict.isDictionaryWord("allowances"));
//
//		// Stemmer test
//		log.info("\nStemmer test");
//		log.info("{}", dict.getStemmedTerm("members"));
//		log.info("{}", dict.getStemmedTerm("atheletes"));
//
//		// Synonyms Test
//		log.info("\nSynonyms test");
//		log.info("{}", dict.generateSynonyms("road"));
//		log.info("{}", dict.generateSynonyms("dog"));
//
//		// Hypernyms Test
//		log.info("\nHypernyms test");
//		log.info("{}", dict.generateSemanticTerms("red", 1));
//		log.info("{}", dict.generateSemanticTerms("athlete", 1));
//		log.info("{}", dict.generateSemanticTerms("sportsman", 1));
//		log.info("{}", dict.generateSemanticTerms("player", 1));
//
//		// Hyponyms Test
//		log.info("\nHyponyms test");
//		log.info("{}", dict.generateSemanticTerms("red", 2));
//		log.info("{}", dict.generateSemanticTerms("athlete", 2));
//		log.info("{}", dict.generateSemanticTerms("sportsman", 2));
//		log.info("{}", dict.generateSemanticTerms("player", 2));
		
	}
}
