package com.asu.debator.objects;

public enum SubjectiveClueType {
	
	WEAK("weaksubj"),
	STRONG("strongsubj");
	
	private final String subjectiveClueType;
	
	private SubjectiveClueType(String subjectiveClueType){
		this.subjectiveClueType = subjectiveClueType;
	}

	public String toString(){
		return subjectiveClueType;
	}
	
	public static SubjectiveClueType getSubjectiveClueType(String subjectiveClueType){
		switch(subjectiveClueType){
			case "weaksubj":
				return WEAK;
			case "strongsubj":
				return STRONG;
			default:
				return null;
		}
	}
}
