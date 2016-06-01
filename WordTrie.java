/**
 * @author Long Pan
 * Implement a trie structure. The difference between 
 * this one and a common trie structure is that the value of each node is
 * a string, and the path from root to any node forms a phrase, the frequency
 * of which is stored in that node.
 */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class WordTrie {		
	// Root of the trie
	private WordTrieNode root;	
	
	WordTrie() {
		root = new WordTrieNode("");
	}
	
	/*
	 * The input is the longest phrase we can get before seeing a punctuation.
	 * Therefore, we need to add all possible phrases that can be found from the
	 * input phrase to the trie.
	 */
	public void add(ArrayList<String> phrase) {		
		for (int i = 0 ; i < phrase.size(); i++) {
			WordTrieNode cur = root;
			for (int j = i; j < phrase.size(); j++) {
				String word = phrase.get(j);
				if (!cur.getMap().containsKey(word)) {
					cur.getMap().put(word, new WordTrieNode(word));				
				}
				cur = cur.getMap().get(word);
				cur.increaseFrequence();
			}
		}
	}
	
	/*
	 * Use depth first search to traverse the trie. 
	 * Get the frequency of each phrase and sort them using partialSort.
	 * Add each phrase with their frequency into partialSort while traversing.
	 */
	public ArrayList<String> calcKeyPhrases(int topK) {
		ArrayList<String> res = new ArrayList<String>();
		if (topK <= 0) {
			return res;
		}
		
		PartialSort<HeapNode> partialSort = new PartialSort<HeapNode>(topK, new HeapNodeComparator());
		dfsToGetKeyPhrases(partialSort, "", root);		
		
		ArrayList<HeapNode> list = partialSort.getSortedTopK();
		for (int i = 0; i < list.size(); i++) {
			res.add(list.get(i).getPhrase());
		}
		
		return res;
	}
	
	/*
	 * The recursion helper method for method calcKeyPhrases.
	 * 
	 */
	private void dfsToGetKeyPhrases(PartialSort<HeapNode> partialSort, String path, WordTrieNode root) {
		if (root.getFrequence() > 0) {
			partialSort.add(new HeapNode(path, root.getFrequence()));
		}
		
		for (Map.Entry<String, WordTrieNode> entry : root.getMap().entrySet()) {
			WordTrieNode nextNode = entry.getValue();
			String word = path.length() == 0 ? nextNode.getWord() : " " + nextNode.getWord();	
			dfsToGetKeyPhrases(partialSort, path + word, nextNode);			
		}
	}	
	
	
	/*
	 * Inner class for the node of the trie
	 */
	private class WordTrieNode {
		private final String word;
		private final HashMap<String, WordTrieNode> map;
		private int frequence;
		
		WordTrieNode(String word) {
			this.word = word;
			map = new HashMap<String, WordTrieNode>();
			frequence = 0;
		}	
		
		public HashMap<String, WordTrieNode> getMap() {
			return map;
		}
		
		public void increaseFrequence() {
			frequence++;
		}
		
		public int getFrequence() {
			return frequence;
		}
		
		public String getWord() {
			return word;
		}
			
	}		
	
	/*
	 * Inner class for storing each phrase along with its frequency
	 */
	private class HeapNode{
		private final String phrase;
		private final int frequence;
		
		HeapNode(String phrase, int frequence) {			
			this.phrase = phrase;
			this.frequence = frequence;
		}

		public String getPhrase() {
			return phrase;
		}

		public int getFrequence() {
			return frequence;
		}			
	}
	
	private class HeapNodeComparator implements Comparator<HeapNode> {
		@Override
		public int compare(HeapNode o1, HeapNode o2) {
			if (o2.frequence == o1.frequence) {
				return o2.getPhrase().length() - o1.getPhrase().length();
			}
			
			return o2.getFrequence() - o1.getFrequence();			
		}		
	}
}
