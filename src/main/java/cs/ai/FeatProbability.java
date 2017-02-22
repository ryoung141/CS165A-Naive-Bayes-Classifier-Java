package main.java.cs.ai;


public interface FeatProbability<T, K>{
    float featureProbability(T feature, K category);
}