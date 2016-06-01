/**
 * @author Long Pan
 *
 * This class is a simple encapsulation of the class Stemmer that was written by
 * some researchers. The reason why I want to encapsulate it is that the old one
 * doesn't provide an useful method for a direct call. 
 */
public class MyStemmer {
	private Stemmer stemmer;
	
	MyStemmer(Stemmer stemmer) {
		this.stemmer = stemmer;
	}
	
	/*
	 * Get the prototype of an English word.
	 * e.g "interesting"-> "interest" 
	 *     "apples"     -> "apple"
	 *     "doable"     -> "do"
	 */
	public String stem(String originalWord) {	
		if (originalWord == null) {
			return null;
		}
		stemmer.add(originalWord.toCharArray(), originalWord.length());
		stemmer.stem();
		return stemmer.toString();       
	}	

}
