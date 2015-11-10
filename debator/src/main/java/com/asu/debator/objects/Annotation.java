package com.asu.debator.objects;

public enum Annotation {
	
	FACT("F"),
	OPINION("O"),
	NONE("N");
	
	private final String annotattion;
	
	private Annotation(String annotation){
		this.annotattion = annotation;
	}
	
	public String toString(){
		return annotattion;
	}
	
	public static Annotation getAnnotation(String annotation){
		switch(annotation){
		case "F":
			return FACT;
		case "O":
			return OPINION;
		case "N":
			return NONE;
		default:
			return null;
		}
	}
}
