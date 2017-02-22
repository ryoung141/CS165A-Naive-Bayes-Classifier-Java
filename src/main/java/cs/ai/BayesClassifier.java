package main.java.cs.ai;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Collection;

import cs.165A.ai.classifier.Classification;
import cs.165A.ai.classifier.Classifier;

public class BayesClassifier<T, K> extends Classifier<T, K> {
    /**********************************************************************************
     * PRIVATE MEMBERS
     **********************************************************************************/
    private float featureProbProduct(Collection<T> feats, K category){
        float prod = 1.0f;
        for(T feat : feats)
            prod *= this.featureWeighedAverage(feat, category);
        return prod;
    }

    private float categoryProb(Collection<T> feats, K category){
        return ((float)this.getCategoryCount(category) / (float) this.getCategoriesTotal()) * featureProbProduct(feats, category);
    }

    private SortedSet<Classification<T, K>> categoryProbs(Collection<T> feats) {
        SortedSet<Classification<T, K>> probs =
                new TreeSet<Classification<T, K>>(
                        new Comparator<Classification<T, K>>() {
                            public int compare(Classificsation<T, K> o1,
                                               Classification<T, K> o2) {
                                int ret = Float.compare(
                                        o1.getProb(), o2.getProb());
                                if ((ret == 0) && !o1.getCategory().equals(o2.getCategory()))
                                    ret = -1;
                                return ret;
                            }
                        }
                );
        for (K category : this.getCategories())
            probs.add(new Classification<T, K>(
                    feats, category, this.categoryProb(feats, category)));
        return probs;
    }

    @Override
    public Classification<T, K> classify(Collection<T> feats){
        SortedSet<Classification<T, K>> probs = this.categoryProbs(feats);
        if(probs.size() > 0){
            return probs.last();
        }
        return null;
    }

    public Collection<Classification<T, K>> classifyDetail(Collection<T> feats){
        return this.categoryProbs(feats);
    }
}