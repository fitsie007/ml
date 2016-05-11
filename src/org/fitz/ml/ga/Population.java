package org.fitz.ml.ga;

import java.util.ArrayList;

/**
 * This method provides functionalities for manipulating a population
 * Created by FitzRoi on 3/29/16.
 */
public class Population {
    private ArrayList<Hypothesis> hypotheses;
    private Hypothesis fittest;// the fittest hypothesis in the population

    public Population(){
        this.hypotheses = new ArrayList<Hypothesis>();
    }

    public ArrayList<Hypothesis> getHypotheses() {
        return hypotheses;
    }

    public void setHypotheses(ArrayList<Hypothesis> hypotheses) {
        this.hypotheses = hypotheses;
    }

    public void addHypothesis(Hypothesis hypothesis) {
        this.hypotheses.add(hypothesis);
    }

    public Hypothesis getFittest() {
        return fittest;
    }

    public void setFittest(Hypothesis fittest) {
        this.fittest = fittest;
    }
}
