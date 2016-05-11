package org.fitz.ml.ga;

/**
 * This enum is used to facilitate easy selection of a strategy
 * to be used for probabilistic selections of hypotheses for a population
 * Created by FitzRoi on 3/28/16.
 */
public enum SelectionStrategy {
    FITNESS_PROPORTIONATE, TOURNAMENT, RANK;
}
