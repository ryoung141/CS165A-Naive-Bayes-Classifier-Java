
import java.io.FileReader;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class run_this{

    public static void main(String[] args) throws IOException{
	final long timeConst = 100000000;
	final Classifier<String, String> bayes =
                new BayesClassifier<String, String>();
        FileReader train = new FileReader(args[0]);
	FileReader test = new FileReader(args[1]);
	FileReader train2 = new FileReader(args[0]);
	FileReader test2 = new FileReader(args[1]);
        String words[];
        String line;
        BufferedReader br = new BufferedReader(train);
	BufferedReader br2 = new BufferedReader(train2);
	BufferedReader te = new BufferedReader(test);
	BufferedReader te2 = new BufferedReader(test2);
	long start = System.nanoTime();
	    try{
		while((line = br.readLine()) != null) {
		    words = line.split("\\s+");
		    int s = words.length;
		    List<String> wordList = Arrays.asList(words);
		    //	System.out.println(words[s-1]);
		    if (words[s-1].equals("0")) {
			bayes.learn("negative", wordList);
			//  System.out.println("bad");
		    }
		    if (words[s-1].equals("1")){
			bayes.learn("positive", wordList);
			// System.out.println("good");
		    }
		}
		double temp = 0.0;
		double count = 0.0;
		while((line = br2.readLine()) != null) {
		    String[] tWords1  = line.split("\\s+");
		    int s = tWords1.length;
		    List<String> T_wordList1 = Arrays.asList(tWords1);
		    if (tWords1[s-1].equals("0") && bayes.classify(Arrays.asList(tWords1)).getCategory() == "negative") {
			temp++;
		    }
		    if (tWords1[s-1].equals("1") && bayes.classify(Arrays.asList(tWords1)).getCategory() == "positive"){
			temp++;
		    }
		    count++;
		}
	        double train_percent = temp/count;
		long estTime1 = System.nanoTime() - start;
		//System.out.println("END OF TRAINING");
		while((line = te.readLine()) != null) {
		    String[] tWords  = line.split("\\s+");
		    int s = tWords.length;
		    List<String> T_wordList = Arrays.asList(tWords);
		    if (bayes.classify(Arrays.asList(tWords)).getCategory() == "positive"){
			System.out.println("1");}
		    if (bayes.classify(Arrays.asList(tWords)).getCategory() == "negative"){
			System.out.println("0");}
		}
		double temp2 = 0.0;
		double count2 = 0.0;
		while((line = te2.readLine()) != null) {
		    String[] tWords2  = line.split("\\s+");
		    int s = tWords2.length;
		    List<String> T_wordList2 = Arrays.asList(tWords2);
		    if (tWords2[s-1].equals("0") && bayes.classify(Arrays.asList(tWords2)).getCategory() == "negative") {
			temp2++;
		    }
		    if (tWords2[s-1].equals("1") && bayes.classify(Arrays.asList(tWords2)).getCategory() == "positive"){
			temp2++;
		    }
		    count2++;
		}
		double test_percent = temp2/count2;
		long estTime2 = System.nanoTime();
		long fin = estTime2 - start - estTime1;
		System.out.println(estTime1/1000000000 + " seconds (training)");	      		
		System.out.println(fin/100000000 + " seconds (labeling)");
		System.out.println(train_percent + " (training)");
		System.out.println(test_percent + " (testing)"); 
	   
	    }catch (NullPointerException e){
		System.out.println(e.getMessage());
		e.printStackTrace();
	    }
    }
}

