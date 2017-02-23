default: jar

clean:
	rm *.class
	rm *.jar

jar:
	javac -g Classifier.java BayesClassifier.java FeatProbability.java Classification.java run_this.java
	jar cfm NaiveBayesClassifier.jar Manifest.txt *.class
