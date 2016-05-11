package org.fitz.ml.backprop;

import org.fitz.ml.Attribute;
import org.fitz.ml.Attributes;
import org.fitz.util.Util;

import java.util.ArrayList;

/**
 * This class facilitates the classification of examples
 * Created by FitzRoi on 3/9/16.
 */
public class Classifier {
    public Classifier(){}

    /**
     *
     * @param examples the examples to classify
     * @param targetAttributes the target classes
     * @param bp backpropagation object containing learned weights
     * @param isPrintFlagOn flag to specify printing of classifications
     * @return the accuracy of the classification
     */
    public double classify(ArrayList<Example> examples, Attributes targetAttributes, Backpropagation bp, boolean isPrintFlagOn, String datasetLabel) {
        FeedForwardNetwork network = bp.getNetwork();
        ArrayList<Unit> hiddenLayer = network.getHiddenLayer();
        ArrayList<Unit> outputLayer = network.getOutputLayer();
        int n = examples.get(0).getT().length; //determine size of output from first example

        if (isPrintFlagOn)
            printHeader(datasetLabel);


        double totalCorrect = 0;
        double numExamples = examples.size();

        for (Example example : examples) {
            //Input the instance x to the network
            network.setX(example.getX()); //propagate the input x through hidden layer
            network.setT(example.getT());
            double newX[] = bp.getHiddenOutput(hiddenLayer);
            double output[] = new double[n - 1]; //number of output units is less than n because of x0
            String targetStr = "";
            String outputStr = "";

            //get output from hidden layer and then get final output from network
            for (Unit k : outputLayer) {
                int index = outputLayer.indexOf(k);
                k.setX(newX); //propagate new x through output layer
                output[index] = k.getOutput();

            }

            double targetOutput[] = example.getT();
            for (Double val : output)
                outputStr += String.format("%.0f", val);

            for (int i = 1; i < targetOutput.length; i++) {
                targetStr += String.format("%.0f", targetOutput[i]);
            }

            if (outputStr.equals(targetStr)) {
                totalCorrect++;
//                System.out.println(outputStr +"=" +targetStr +"\n");
            }

            //only print classifications if print flag turned on
            if (isPrintFlagOn) {
                if (example.instanceSaved()) {
                    Attribute targetAttribute = targetAttributes.get(0);//only one attribute exist when instance saved
                    printResult(example.getX(), example.toString(), newX, output, Util.revertOneOfN(targetAttribute, outputStr), example.getTargetOutput());
                } else
                    printNumericResult(example.getX(), newX, output);
            }

        }

        double accuracy = (totalCorrect > 0) ? (totalCorrect / numExamples) * 100.0 : 0;

        if (isPrintFlagOn)
            System.out.format("ACCURACY: \t %.0f/%.0f = %.2f%% %n", totalCorrect, numExamples, accuracy);

        return accuracy;
    }



    public void printHeader(String datasetLabel){
        System.out.print("\t\t\tRESULTS FOR " +datasetLabel);
        System.out.println("\n\tInput                      Hidden Values                     Output\n");
    }


    /**
     * This method prints the result of a classification
     * @param x the x vector
     * @param inputStr a concatenated string of attributes
     * @param hiddenValues an array of hidden values from the hidden layer
     * @param outputVec the output vector from the hidden layer
     * @param outputStr a mapping of the output vector to the actual class
     * @param targetOutput the expected target
     */
    public void printResult(double x[], String inputStr, double hiddenValues[], double outputVec[], String outputStr, String targetOutput){
        for (int i = 1; i < x.length; i++) //skip x0
            System.out.format("%.0f ", x[i]);
        System.out.print(" --> \t");

        for (int i = 1; i < hiddenValues.length; i++)
            System.out.format("%.2f\t",hiddenValues[i]);

        System.out.print(" --> \t");

        for (double value : outputVec)
            System.out.format("%.0f ", value);

        if(!outputStr.equals(""))
            System.out.print("(" +outputStr +")");
        if(!inputStr.equals(""))
            System.out.print("\nRaw Input: (" + inputStr + ")");

        if(!targetOutput.equals(""))
            System.out.println(" Target: (" + targetOutput + ")");

        System.out.println();

    }

    /**
     * This method prints the results of a classification of numeric data
     * @param x the input x vector
     * @param hiddenValues a resulting vector from the hidden layer
     * @param output a resulting vector from the output layer
     */
    public void printNumericResult(double x[], double hiddenValues[], double output[]){
        for (int i = 1; i < x.length; i++) //skip x0
            System.out.format("%.0f ", x[i]);
        System.out.print(" --> \t");

        for (int i = 1; i < hiddenValues.length; i++)
            System.out.format("%.2f\t",hiddenValues[i]);

        System.out.print(" --> \t");

        for (double value : output)
            System.out.format("%.0f ", value);

        System.out.println();
    }


}
