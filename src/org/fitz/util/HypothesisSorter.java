package org.fitz.util;

import org.fitz.ml.ga.Hypothesis;

import java.util.Comparator;

/**This class is used to sort hypotheses (e.g.: by probability or fitness)
 * Created by FitzRoi on 3/31/16.
 */
public class HypothesisSorter implements Comparator<Hypothesis> {

    public enum sortBy{FITNESS, PROBABILITY};
    public sortBy sortingParameter = sortBy.FITNESS;

    @Override
    public int compare(Hypothesis h1, Hypothesis h2) {
        Double o1 = 0.0, o2 = 0.0;
        if(sortingParameter == sortBy.PROBABILITY) {
            o1 = h1.getPr();
            o2 = h2.getPr();
        }

        if(sortingParameter == sortBy.FITNESS) {
            o1 = h1.getFitness();
            o2 = h2.getFitness();
        }

        return o2.compareTo(o1);
    }

    public void setSortingParameter(sortBy sortingParameter) {
        this.sortingParameter = sortingParameter;
    }
}
