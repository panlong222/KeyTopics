import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class WordDensity {
	// URL of the web page to be parsed
	private String url;
	
	// Trie structure for storing phrases with their frequency
	private WordTrie wordTrie; 
	
	// To determine whether a word is a stop word or not
	private StopWordRecognizer stopWordRecognizer;
	
	// To stem a word into its prototype
	private MyStemmer stemmer;
	
	// The number of most relevant topics 
	private int topK;	
	
	// To store the most k relevant topics 
	private ArrayList<String> keyTopics;

	public WordDensity(String url, StopWordRecognizer stopWordRecognizer,
			MyStemmer stemmer, int topK) {		
		this.url = url;
		this.stopWordRecognizer = stopWordRecognizer;
		this.stemmer = stemmer;
		this.topK = topK;
		wordTrie = new WordTrie();
		keyTopics = new ArrayList<String>();
	}


	/*
	 * Check if the URL is in a valid form
	 */
	private boolean isValidURL() {
		try {
			new URL(new String(url));
		} catch (MalformedURLException e) {			
			e.printStackTrace();			
			return false;
		} 
		return true;
	}
	
	
	/*
	 * Get the web page by url using Jsoup and store all the words into an array
	 */
	private String[] parseHTML() {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		if (doc == null) {
			System.out.println("Page does not exist!");
			return null;
		}
		
		// Get all content within the body tag
		Elements body = doc.select("body");
		
		return body.text().split(" ");		
	}
	
	/*
	 * Check if a character is a punctuation 
	 */
	private boolean isPunctuation(char ch) {
		return  ch == ',' || ch == '.' || ch == '?' || ch == '!' || ch == '\'' || ch == '"' || 
				ch == ':' || ch == ';' || ch == '(' || ch == ')' || ch == '<' || ch == '>';
	}	
	
	/*
	 * Build the word trie by scanning the text content	 
	 */
	private void constructWordTrie(String[] words) {
		// To store the longest phrase as we scan the document text
		ArrayList<String> phrase = new ArrayList<String>();
		
		// Scan the document text in one pass
		for (int i = 0; i < words.length; i++) {
			// Current word
			String word = words[i];	
			if (word.length() == 0) {
				continue;
			}
			
			/* If this word begins with a punctuation, it means that we have reach
			 * the end of the longest phrase which ends at the previous word. Therefore we 
			 * use the longest phrase to update the word trie.
			 */			
			if (isPunctuation(word.charAt(0))) { 
				wordTrie.add(phrase);	
				phrase.clear();
				
				//remove all the leading punctuation 
				while (word.length() > 0 && isPunctuation(word.charAt(0))) {
					word = word.substring(1);
				}
				words[i] = word;
				
				i--;
				continue;
			} 
			
			// True if the word ends with a punctuation
			boolean endWithPunctuation = isPunctuation(word.charAt(word.length() - 1));			
			
			// Remove all the characters that are neither numbers nor letters 
			word = word.replaceAll("[^\\w\\s/*]","");	
			word = word.trim();
			
			// Turn the word into lowercase
			word = word.toLowerCase();			
			
			boolean isStopWord = stopWordRecognizer.isStopWord(word);
			
			// Get the original form of the word through stemming			
			word = stemmer.stem(word);
			
			// If the word is not a stop word, add it to the phrase				
			if (word.length() > 0 && !isStopWord) {				
				phrase.add(word);
			}			
			
			/* If the word ends with punctuation, then it is the end
			 * of the longest phrase which ends at this word, and we use this longest phrase to
			 * update the word trie.
			 */
			if (endWithPunctuation) {
				wordTrie.add(phrase);	
				phrase.clear();
			}			
		}		
	}
	
	
	/*
	 * The main process to get the most relevant topics of the web page
	 */
	private void process() {
		// 1. Check if the url if valid
		if(!isValidURL()){
			return;
		}
		
		// 2. Parse HTML
		String[] words = parseHTML();		
		if (words == null) {
			return;
		}
		
		// 3. Build the word trie
		constructWordTrie(words);
		
		// 4. Get the top K most relevant phrases
		keyTopics = wordTrie.calcKeyPhrases(topK);		
	}
	
	// Get the key topics
	public ArrayList<String> getKeyTopics() {
		return keyTopics;
	}	
	
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("No URL.");
			return;
		}		
		
		String url = args[0];		
		
		//default number of key topics is 6
		int topK = 20;
		if (args.length >= 2) {
			topK = Integer.parseInt(args[1]);
			if (topK <= 0) {
				System.out.println("The number of topics is not valid.");
				return;
			}
		}
		
        StopWordRecognizer stopWordRecognizer = new CommonStopWordRecognizer();
		MyStemmer stemmer = new MyStemmer(new Stemmer());
		
		WordDensity wordDensity = new WordDensity(url, stopWordRecognizer, stemmer, topK);
		wordDensity.process();
		ArrayList<String> res = wordDensity.getKeyTopics();
		
		System.out.println("Most relevant topics are:");
		for (String topic : res) {
			System.out.println(topic);
		}  		
	}	

}
