package com.asu.debator.objects;

public enum Annotation {
	
	FACT("fact"),
	OPINION("opinion"),
	NONE("none");
	
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
			return FACT;
		case "opinion":
			return OPINION;
		case "none":
			return NONE;
		default:
			return null;
		}
	}
}
