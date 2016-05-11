package org.fitz.ml.backprop;

import java.util.ArrayList;

/**
 * This class facilitates the creation and manipulation
 * of a feed-forward network
 * Created by FitzRoi on 3/3/16.
 */
public class FeedForwardNetwork {

    private ArrayList<Unit> outputLayer;
    private ArrayList<Unit> hiddenLayer;

    public FeedForwardNetwork(int nIn, int nHidden, int nOut){
        this.hiddenLayer = new ArrayList<Unit>();
        this.outputLayer = new ArrayList<Unit>();
        init(nIn, nHidden, nOut);
    }

    public ArrayList<Unit> getOutputLayer() {
        return outputLayer;
    }

    public ArrayList<Unit> getHiddenLayer() {
        return hiddenLayer;
    }

    /**
     * This method initializes the network units and weights
     * @param nIn the number of inputs
     * @param nHidden the number of hidden units
     * @param nOut the number of output units
     */
    public void init(int nIn, int nHidden, int nOut){
        for(int i = 0 ; i < nHidden; i++) { //make nHidden hidden units
            Unit h = new Unit(nIn, nOut); //xSize, targetSize
            h.initWeights();
            hiddenLayer.add(h);
        }
        for(int i = 0; i < nOut; i++) { //make nOut output units
            int xSize = nHidden + 1; //+1 for x0
            Unit o = new Unit(xSize, nOut);
            o.initWeights();
            outputLayer.add(o);
        }

    }

    public void setX(double[] x) {
        for(Unit unit : hiddenLayer)
            unit.setX(x);
    }

    public void setT(double[] t){
        for(Unit unit : outputLayer)
            unit.setT(t);
    }
}
