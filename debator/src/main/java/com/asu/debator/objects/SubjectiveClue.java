package com.asu.debator.objects;

public class SubjectiveClue {

	private SubjectiveClueType subjectiveClueType;
	private Integer length;
	private String word;
	private String  pos;
	private String stemmed;
	private PriorPolarity priorPolarity;
	
	public SubjectiveClueType getSubjectiveClueType() {
		return subjectiveClueType;
	}
	public void setSubjectiveClueType(SubjectiveClueType subjectiveClueType) {
		this.subjectiveClueType = subjectiveClueType;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getStemmed() {
		return stemmed;
	}
	public void setStemmed(String stemmed) {
		this.stemmed = stemmed;
	}
	public PriorPolarity getPriorPolarity() {
		return priorPolarity;
	}
	public void setPriorPolarity(PriorPolarity priorPolarity) {
		this.priorPolarity = priorPolarity;
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj==null || !(obj instanceof SubjectiveClue)){
			return false;
		}
		return this.toString().equals(obj.toString());
	}
	@Override
	public String toString() {
		return this.word.toString() + "-" 
				+ this.pos.toString() + "-" 
				+ this.subjectiveClueType.toString();
	}
}
