
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Dictionary;

public abstract class Classifier<T, K> implements FeatProbability<T, K>{


/********************************************************************************
 * PUBLIC MEMBERS OF CLASSIFIER
 ********************************************************************************/

    //new classifier w/o any training
    public Classifier(){
        this.reset();
    }

    //resets learned feature and category counts
    public void reset(){
        this.featCountPerCategory = new Hashtable<K, Dictionary<T, Integer>>(Classifier.INITIAL_CATEGORY_DICTIONARY_CAPACITY);
        this.totalFeatCount = new Hashtable<T, Integer>(Classifier.INITIAL_FEATURE_DICTIONARY_CAPACITY);
        this.totalCategoryCount = new Hashtable<K, Integer>(Classifier.INITIAL_CATEGORY_DICTIONARY_CAPACITY);
        this.memoryQueue = new LinkedList<Classification<T, K>>();
    }

    //returns a set of feats the classifier knows about
    public Set<T> getFeatures(){
        return ((Hashtable<T, Integer>) this.totalFeatCount).keySet();
    }

    //returns a set of categories the classifier knows about
    public Set<K> getCategories(){
        return ((Hashtable<K, Integer>) this.totalCategoryCount).keySet();
    }

    //gets # of categories the classifier knows about
    public int getCategoriesTotal(){
        int ret = 0;
        for (Enumeration<Integer> e = this.totalCategoryCount.elements(); e.hasMoreElements();) {
            ret += e.nextElement();
        }
        return ret;
    }

    //return memory cap
    public int getMemoryCapacity(){
        return memoryCapacity;
    }

    //sets mem cap, if new < old, mem will be truncated
    public void setMemoryCapacity(int memoryCapacity){
        for (int i = this.memoryCapacity; i > memoryCapacity; i--){
            this.memoryQueue.poll();
        }
        this.memoryCapacity = memoryCapacity;
    }

    //increments count of a given feature in category.
    //AKA telling classifier that feat has occurred
    public void incrementFeature(T feature, K category) {
        Dictionary<T, Integer> features = this.featCountPerCategory.get(category);
        if (features == null) {
            this.featCountPerCategory.put(category, new Hashtable<T, Integer>(Classifier.INITIAL_FEATURE_DICTIONARY_CAPACITY));
            features = this.featCountPerCategory.get(category);
        }
        Integer cnt = features.get(feature);
        if (cnt == null) {
            features.put(feature, 0);
            cnt = features.get(feature);
        }
        features.put(feature, ++cnt);

        Integer totalCnt = this.totalFeatCount.get(feature);
        if (totalCnt == null) {
            this.totalFeatCount.put(feature, 0);
            totalCnt = this.totalFeatCount.get(feature);
        }
        this.totalFeatCount.put(feature, ++totalCnt);
    }

    //increments count of a given category
    //AKA tells classifier that category has occurred again
    public void incrementCategory(K category){
        Integer cnt = this.totalCategoryCount.get(category);
        if (cnt == null) {
            this.totalCategoryCount.put(category, 0);
            cnt = this.totalCategoryCount.get(category);
        }
        this.totalCategoryCount.put(category, ++cnt);
    }

    //decrements the count of a feat in a category
    public void decrementFeature(T feature, K category){
        Dictionary<T, Integer> feats = this.featCountPerCategory.get(category);
        if (feats == null){
            return;
        }
        Integer cnt = feats.get(feature);
        if (cnt == null){
            return;
        }
        if (cnt.intValue() == 1){
            feats.remove(feature);
            if(feats.size() == 0){
                this.featCountPerCategory.remove(category);
            }
        }
        else{
            feats.put(feature, --cnt);
        }

        Integer totalCnt = this.totalFeatCount.get(feature);
        if (totalCnt == null){
            return;
        }
        if(totalCnt.intValue() == 1){
            this.totalFeatCount.remove(feature);
        }
        else{
            this.totalFeatCount.put(feature, --totalCnt);
        }
    }

    //decrements count of a category
    public void decrementCategory(K category){
        Integer cnt = this.totalCategoryCount.get(category);
        if (cnt == null){
            return;
        }
        if (cnt.intValue() == 1){
            this.totalCategoryCount.remove(category);
        }
        else{
            this.totalCategoryCount.put(category, --cnt);
        }
    }

    //returns # of occurrences of given feat in category
    public int getFeatureCount(T feature, K category){
        Dictionary<T, Integer> feats = this.featCountPerCategory.get(category);
        if(feats == null) return 0;
        Integer cnt = feats.get(feature);
        return (cnt == null) ? 0 : cnt.intValue();
    }

    //returns # of occurences of given feat
    public int getFeatureCount(T feature){
        Integer cnt = this.totalFeatCount.get(feature);
        return (cnt == null) ? 0 : cnt.intValue();
    }

    public int getCategoryCount(K category){
	Integer cnt = this.totalCategoryCount.get(category);
	return (cnt == null) ? 0 : cnt.intValue();
    }

    //part of FeatProbability.java class
    public float featureProbability(T feature, K category){
        final float totalFeatCnt = this.getFeatureCount(feature);
        if (totalFeatCnt == 0) return 0;
        else {
            return this.getFeatureCount(feature, category) / (float) this.getFeatureCount(feature);
        }
    }

    public float featureWeighedAverage(T feature, K category){
        return this.featureWeighedAverage(feature, category, null, 1.0f, 0.5f);
    }

    public float featureWeighedAverage(T feature, K category, FeatProbability<T, K> calculator){
        return this.featureWeighedAverage(feature, category, calculator, 1.0f, 0.5f);
    }

    public float featureWeighedAverage(T feature, K category, FeatProbability<T, K> calculator, float weight){
        return this.featureWeighedAverage(feature, category, calculator, weight, 0.5f);
    }

    public float featureWeighedAverage(T feature, K category, FeatProbability<T, K> calculator, float weight, float assumedProbability){
        final float basicProb = (calculator == null) ? this.featureProbability(feature, category)
                : calculator.featureProbability(feature, category);

        Integer totals = this.totalFeatCount.get(feature);
        if(totals == null) totals = 0;
        return (weight* assumedProbability + totals * basicProb) / (weight + totals);
    }

    //train classifier
    public void learn(K category, Collection<T> feats){
        this.learn(new Classification<T, K>(feats, category));
    }

    public void learn(Classification<T, K> classification){
        for (T feature : classification.getFeatures())
            this.incrementFeature(feature, classification.getCategory());
        this.incrementCategory(classification.getCategory());
        this.memoryQueue.offer(classification);
        if(this.memoryQueue.size() > this.memoryCapacity){
            Classification<T, K> forget = this.memoryQueue.remove();
            for (T feature : forget.getFeatures())
                this.decrementFeature(feature, forget.getCategory());
            this.decrementCategory(forget.getCategory());
        }
    }

    //retrieve most likely category for feat given
    public abstract Classification<T, K> classify(Collection<T> features);

    /*****************************************************************
     * PRIVATE MEMBERS OF CLASSIFIER
     ********************************************************************/
    //initial cap of category dictionaries
    private static final int INITIAL_CATEGORY_DICTIONARY_CAPACITY = 16;

    //initial cap of feature dicts
    private static final int INITIAL_FEATURE_DICTIONARY_CAPACITY = 32;

    //initial memory capacity-- how many classifications can be memorized
    private int memoryCapacity = 1000;

    //Dictionary that maps features to # of occurrences in each category
    private Dictionary<K, Dictionary <T, Integer>> featCountPerCategory;

    //Dictionary mapping features to # of occurrences
    private Dictionary<T, Integer> totalFeatCount;

    //A dictionary mapping categories to # of occurrences
    private Dictionary<K, Integer> totalCategoryCount;

    //Memory queue facilitating forgetful memory
    private Queue<Classification<T, K>> memoryQueue;

}
