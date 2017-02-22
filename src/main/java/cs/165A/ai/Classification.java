package cs.ai.classifier;

import java.util.Collection;

public class Classification<T, K> {

    /**********************************************************************************
     * PRIVATE MEMBERS
     **********************************************************************************/
    private Collection<T> features;
    private K category;
    private float prob;

    /**********************************************************************************
     * PUBLIC MEMBERS
     **********************************************************************************/
    public Classification(Collection<T> features, K category){
        this(features, category, 1.0f);
    }

    public Classification(Collection<T> features, K category, float prob){
        this.features = features;
        this.category = category;
        this.prob = prob;
    }

    public Collection<T> getFeatures(){
        return features;
    }

    public float getProb(){
        return this.prob;
    }

    public K getCategory(){
        return category;
    }

    @Override
    public String toString(){
        return "Classification | category=" + this.category + ", probability=" + this.prob + ", features=" + this.features + " | ";
    }

}