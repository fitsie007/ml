package org.fitz.ml.backprop;

import java.util.ArrayList;

/**
 * This class provides functionalities for a data instance or example
 * Created by FitzRoi on 3/7/16.
 */
public class Example {
    private double x[];
    private double t[];
    private ArrayList<String> instance = null;
    private String targetOutput = null;

    public Example(int nIn, int nOut){
        this.x = new double[nIn];
        this.t = new double[nOut];
    }

    //when the instance string or list of attributes specified
    public Example(int nIn, int nOut, ArrayList<String> instance){
        this.x = new double[nIn];
        this.t = new double[nOut];
        this.instance = instance;
    }

    //when the instance string or list of attributes and the target attribute specified
    public Example(int nIn, int nOut, ArrayList<String> instance, String targetOutput){
        this.x = new double[nIn];
        this.t = new double[nOut];
        this.instance = instance;
        this.targetOutput = targetOutput;
    }

    public double[] getX() {
        return x;
    }

    public void setX(double[] x) {
        this.x = x;
    }

    public double[] getT() {
        return t;
    }

    public void setT(double[] t) {
        this.t = t;
    }

    public ArrayList<String> getInstance() {
        return instance;
    }

    public void setInstance(ArrayList<String> instance) {
        this.instance = instance;
    }

    /**
     * This method groups all the attributes belonging to the example into one output string
     * @return string of concatenated attributes
     */
    public String toString(){
        String str="";
        if(instance!=null){
            int len = instance.size();
            for(int i=0; i<len; i++)
                if(i < len - 1)
                    str += instance.get(i) + ", ";
                else
                    str += instance.get(i);
        }
        return str;
    }

    public boolean targetOutputSaved(){
        return !(targetOutput == null);
    }

    public String getTargetOutput(){
        return targetOutput;
    }
    public boolean instanceSaved(){
        return !(this.instance == null);
    }
}
