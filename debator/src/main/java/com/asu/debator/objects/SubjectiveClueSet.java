package com.asu.debator.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SubjectiveClueSet {

	private static volatile SubjectiveClueSet instance;
	
	private Set<SubjectiveClue> subjectiveClues;
	private Set<String> weakClues;
	private Set<String> strongClues;
	
	private static final String mpqa_subjective_cluesFILE = "/mpqa_subjective_clues.tff";
	
	private SubjectiveClueSet(){
		
		subjectiveClues = new HashSet<SubjectiveClue>();
		weakClues = new HashSet<String>();
		strongClues = new HashSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(mpqa_subjective_cluesFILE)));
			String line;
			while((line=reader.readLine())!=null){
				SubjectiveClue clue = new SubjectiveClue();
				String[] terms = line.split(" ");
				
				SubjectiveClueType type = SubjectiveClueType.getSubjectiveClueType(terms[0].split("=")[1]);
				clue.setSubjectiveClueType(type);
				
				clue.setLength(Integer.parseInt(terms[1].split("=")[1]));
				
				String word = terms[2].split("=")[1];
				clue.setWord(word);
				
				clue.setPos(terms[3].split("=")[1]);
				clue.setStemmed(terms[4].split("=")[1]);
				clue.setPriorPolarity(PriorPolarity.getPriorpolarity(terms[5].split("=")[1]));
				
				if(type.equals(SubjectiveClueType.STRONG)){
					strongClues.add(word);
				}else if(type.equals(SubjectiveClueType.WEAK)){
					weakClues.add(word);
				}
				subjectiveClues.add(clue);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static SubjectiveClueSet getInstance(){
		if(instance == null){
			instance = new SubjectiveClueSet();
		}
		return instance;
	}

	public Set<SubjectiveClue> getSubjectiveClues() {
		return subjectiveClues;
	}

	public Set<String> getWeakClues() {
		return weakClues;
	}

	public Set<String> getStrongClues() {
		return strongClues;
	}
}
