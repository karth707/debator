package com.asu.debator.objects;

public enum Annotation {
	
	F("fact"),
	O("opinion"),
	N("none");
	
	private final String annotattion;
	
	private Annotation(String annotation){
		this.annotattion = annotation;
	}
	
	public String toString(){
		return annotattion;
	}
	
	public static Annotation getAnnotation(String annotation){
		switch(annotation){
		case "fact":
			return F;
		case "opinion":
			return O;
		case "none":
			return N;
		default:
			return null;
		}
	}
}
