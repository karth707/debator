package com.asu.debator.objects;

public enum PriorPolarity {

	NEGATIVE("negative"),
	POSITIVE("positive");
	
	private final String priorPolarity;
	
	private PriorPolarity(String priorPolarity){
		this.priorPolarity = priorPolarity;
	}
	
	public String toString(){
		return priorPolarity;
	}
	
	public static PriorPolarity getPriorpolarity(String priorPolarity){
		switch(priorPolarity){
			case "negative":
				return NEGATIVE;
			case "positive":
				return POSITIVE;
			default:
				return null;
		}
	}
}
