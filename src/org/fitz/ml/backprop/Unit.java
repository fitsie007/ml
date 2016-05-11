package org.fitz.ml.backprop;


import org.fitz.ml.constants.AnnConstants;
import java.util.Random;

/**
 * This class provides functionalities and properties for a unit within a network layer
 * Created by FitzRoi on 3/3/16.
 */
public class Unit {
    private double w[];//weights
    private double x[];
    private double t[]; //target values
    private double error;

    public Unit(int xSize, int targetSize){
        this.x = new double[xSize];
        this.w = new double[xSize];
        this.t = new double[targetSize];
        initWeights();
    }

    /**
     * This method initializes random weights to start off the training process
     */
    public void initWeights(){
        Random rand = new Random();
        for (int i = 0; i < w.length; i++)
            w[i] = AnnConstants.MIN_WEIGHT + (AnnConstants.MAX_WEIGHT - AnnConstants.MIN_WEIGHT) * rand.nextDouble();
    }

    public double[] getX() {
        return x;
    }

    public void setX(double[] x) {
        this.x = x;
    }

    public void setT(double[] t) {
        this.t = t;
    }

    public double[] getW() {
        return w;
    }


    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public void setWi(double wi, int i){
            w[i] = wi;
    }

    public double getWi(int i){
        return w[i];
    }

    /**
     * This method returns the output of the unit using sigmoid
     * @return
     */
    public double getOutput(){
        double out = 0;
        for(int i = 0; i < x.length; i++)
            out += (w[i] * x[i]);
        return σ(out);
    }

    /**
     * This is the sigmoid or logistic function, which outputs a value between 0 and 1
     * @param net the value for which the sigmoid must be calculated
     * @return the sigmoid value
     */
    public double σ(double net){
        double divisor = (1 + Math.exp(-net));
        if(divisor == 0)
            return 0;
        return (1 / divisor); //σ(y) = 1 / (1 + e^(-y))
    }

    /**
     * This method calculates the error term associated with the unit
     * @param k the target index
     * @return the unit error
     */
    public double δ(int k){
        double ok = getOutput();
        double δk  = (ok * ( 1 - ok )) * (t[k] - ok); // δk <-- ok(1 - ok)(tk - ok)
        return δk;
    }
}
