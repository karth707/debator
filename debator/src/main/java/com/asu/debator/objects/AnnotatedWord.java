package com.asu.debator.objects;

public class AnnotatedWord {

	private String word;
	private Annotation annotation;
	
	public AnnotatedWord(String word, String annotation) {
		this.word = word;
		setAnnotation(annotation);
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public Annotation getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		
		if(Annotation.F.toString().equals(annotation))
			this.annotation = Annotation.F;
		else if (Annotation.O.toString().equals(annotation))
			this.annotation = Annotation.O;
		else 
			this.annotation = Annotation.N;
	}
	@Override
	public String toString() {
		return "AnnotatedWord [word=" + word + ", annotation=" + annotation + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotation == null) ? 0 : annotation.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnnotatedWord other = (AnnotatedWord) obj;
		if (annotation != other.annotation)
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}
	
}
