package main.java.cs.ai;


import java.io.Reader;
import java.io.File;
import java.util.Arrays;

import main.java.cs.ai.BayesClassifier;
import main.java.cs.ai.Classifier;

public class run_this{

    public static void main(String[] args){
        final Classifier<String, String> bayes =
                new BayesClassifier<String, String>();
        File f = new File(args[0]);
        String words[];
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
        while((line=br.readLine() != null)){
            words = line.split("//s");
            int s = Array.getlength(words);
            if (words[s-1] == 0) {
                bayes.learn("negative", Arrays.asList(words));
            }
            if (words[s-1] == 1){
                bayes.learn("positive", Arrays.asList(words));
            }
        }
    }
}