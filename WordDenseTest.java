import java.util.ArrayList;


public class WordDenseTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
