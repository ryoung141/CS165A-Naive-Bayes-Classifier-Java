
import java.io.FileReader;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class run_this{

    public static void main(String[] args) throws IOException{
        final Classifier<String, String> bayes =
                new BayesClassifier<String, String>();
        FileReader fr = new FileReader(args[0]);
        String words[];
        String line;
        BufferedReader br = new BufferedReader(fr);
	try{
	    while((line = br.readLine()) != null) {
		words = line.split("\\s+");
		int s = words.length;
		List<String> wordList = Arrays.asList(words);
		System.out.println(words[s-1]);
		if (words[s-1].equals("0")) {
		    bayes.learn("negative", wordList);
		    System.out.println("bad");
		}
		if (words[s-1].equals("1")){
		    bayes.learn("positive", wordList);
		    System.out.println("good");
		}
		final String[] u1 = "Movie good af".split(" ");
		final String[] u2 = "movie bad af".split(" ");
		System.out.println(bayes.classify(Arrays.asList(u1)).getCategory());
		System.out.println(bayes.classify(Arrays.asList(u2)).getCategory());
	    }}catch (NullPointerException e){
	    System.out.println(e.getMessage());
	    e.printStackTrace();
	}
    }
}

