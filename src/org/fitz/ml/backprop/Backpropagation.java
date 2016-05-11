package org.fitz.ml.backprop;

import org.fitz.ml.constants.AnnConstants;

import java.util.ArrayList;

/**
 * This class facilitates the execution of the backpropagation algorithm
 * Created by FitzRoi on 3/3/16.
 */
public class Backpropagation {

    public Backpropagation(){}

    private double prevOutputDeltaWji = 0;
    private double prevHiddenDeltaWji = 0;
    private FeedForwardNetwork network;
    private int optimalIterations = 0;
    private double prevValidationError = Double.MAX_VALUE;
    private double accuracy = 0;

    /**
     * This method performs the stochastic version of the Backpropagation algorithm
     * @param examples the list of training examples
     * @param eta the learning rate
     * @param nIn the number of inputs
     * @param nOut the number of outputs
     * @param nHidden the number of hidden units
     * @param iterations the number of iterations
     * @param momentum the momentum used to specify the dependence on the previous weight update
     */
    public void runBackprop(ArrayList<Example> examples, double eta, int nIn, int nOut, int nHidden, int iterations, double momentum, boolean useWeightDecay ) {
        network = new FeedForwardNetwork(nIn, nHidden, nOut);
        ArrayList<Unit> hiddenLayer = network.getHiddenLayer();
        ArrayList<Unit> outputLayer = network.getOutputLayer();

        for (int i = 0; i < iterations; i++) {

            for (Example example : examples) {
            //1.  Input the instance x to the network
                network.setX(example.getX()); //propagate the input x through hidden layer
                network.setT(example.getT());
                double newX[] = getHiddenOutput(hiddenLayer);


            //2. For each output k, compute its error term δk
                for (Unit k : outputLayer) {
                    k.setX(newX); //propagate new x through output layer
                    int nodeIndex = outputLayer.indexOf(k);
                    k.setError(k.δ(nodeIndex + 1));
                }

            //3. For each hidden unit h,  calculate its error term  δh
                for (Unit h : hiddenLayer) {
                    double oh = h.getOutput();
                    int nodeIndex = hiddenLayer.indexOf(h);
                    //find ∑        wkh * δk
                    ///    k in outputs
                    double outputErrorSum = getErrorSum(outputLayer, nodeIndex + 1); //skip w0 [w1,w2,w3..wn]
                    double δh = oh * (1 - oh) * outputErrorSum;
                    h.setError(δh);
                }

            //4. Update each network weight wji
                updateOutputWeights(outputLayer, eta, momentum, useWeightDecay);
                updateHiddenWeights(hiddenLayer, eta, momentum, useWeightDecay);


            }

        }

    }


    /**
     * This method performs the stochastic version of the Backpropagation algorithm.
     * It uses a validation set to determine the number of iterations with the least error
     * @param examples the list of training examples
     * @param eta the learning rate
     * @param nIn the number of inputs
     * @param nOut the number of outputs
     * @param nHidden the number if hidden units
     * @param iterations the number of iterations
     * @param momentum the momentum used to specify the dependence on the previous weight update
     * @param validationSet validation set to use to determine number of iterations with small(est) error
     * @param useWeightDecay boolean specifying whether to use weight decay
     */
    public void runKFoldBackprop(ArrayList<Example> examples, double eta, int nIn, int nOut, int nHidden, int iterations, double momentum, ArrayList<Example> validationSet, boolean useWeightDecay) {
        network = new FeedForwardNetwork(nIn, nHidden, nOut);
        ArrayList<Unit> hiddenLayer = network.getHiddenLayer();
        ArrayList<Unit> outputLayer = network.getOutputLayer();
        double currentValidationError;

        for (int i = 0; i < iterations; i++) {

            for (Example example : examples) {
                //1.  Input the instance x to the network
                network.setX(example.getX()); //propagate the input x through hidden layer
                network.setT(example.getT());
                double newX[] = getHiddenOutput(hiddenLayer);


                //2. For each output k, compute its error term δk
                for (Unit k : outputLayer) {
                    k.setX(newX); //propagate new x through output layer
                    int nodeIndex = outputLayer.indexOf(k);
                    k.setError(k.δ(nodeIndex + 1));
                }

                //3. For each hidden unit h,  calculate its error term  δh
                for (Unit h : hiddenLayer) {
                    double oh = h.getOutput();
                    int nodeIndex = hiddenLayer.indexOf(h);
                    //find ∑        wkh * δk
                    ///    k in outputs
                    double outputErrorSum = getErrorSum(outputLayer, nodeIndex + 1); //skip w0 [w1,w2,w3..wn]
                    double δh = oh * (1 - oh) * outputErrorSum;
                    h.setError(δh);
                }

                //4. Update each network weight wji
                updateOutputWeights(outputLayer, eta, momentum, useWeightDecay);
                updateHiddenWeights(hiddenLayer, eta, momentum, useWeightDecay);

            }

            //use error of validation set to determine ideal iterations
            currentValidationError = E(validationSet);

            if(currentValidationError < prevValidationError){
                prevValidationError = currentValidationError;
//                System.out.println("error = " + prevValidationError +"\n");
                optimalIterations = i;
            }

        }

    }


    public double getAccuracy(ArrayList<Example> examples) {
        ArrayList<Unit> hiddenLayer = network.getHiddenLayer();
        ArrayList<Unit> outputLayer = network.getOutputLayer();
        int n = examples.get(0).getT().length; //determine size of output from first example

        double totalCorrect = 0;
        double numExamples = examples.size();
        double accuracy;

        for (Example example : examples) {
            //Input the instance x to the network
            network.setX(example.getX()); //propagate the input x through hidden layer
            network.setT(example.getT());
            double newX[] = getHiddenOutput(hiddenLayer);
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
        }

        accuracy = (totalCorrect > 0) ? (totalCorrect / numExamples) * 100.0 : 0;

        return accuracy;

    }

    /**
     * This method sums the error between the a hidden layer and output unit using the following formula:
     *   ∑        wkh * δk
     *   k in outputs
     * @param layer the output layer
     * @param k the index of the output unit
     * @return total error
     */
    public double getErrorSum( ArrayList<Unit> layer, int k){
        double errorSum = 0;
        for(Unit outputUnit : layer){
            double δk = outputUnit.getError();
            double wkh = outputUnit.getWi(k); //the weight between hidden unit h and output unit k
            errorSum += (wkh * δk);
        }

        return errorSum;
    }

    /**
     * This method determines the output for all hidden units
     * @param hiddenLayer the hidden layer
     * @return a vector of outputs from all hidden units
     */
    public double[] getHiddenOutput(ArrayList<Unit> hiddenLayer){
        int n = hiddenLayer.size();
        double x[] = new double[n + 1]; //for x0
        x[0] = 1;
        for(int i = 1; i <= n; i++)
            x[i] = hiddenLayer.get(i - 1).getOutput();
        return x;
    }

    /**
     * This method updates the weights in the hidden-layer
     * @param layer the hidden layer
     * @param eta the learning rate
     * @param α the momentum factor
     */
    public  void updateHiddenWeights(ArrayList<Unit> layer, double eta, double α, boolean useWeightDecay){
        for (Unit k : layer) {
            double w[] = k.getW();
            double x[] = k.getX();
            double δj =  k.getError();

            for (int ji = 0; ji < w.length; ji++) {
                double Δwjin = eta * δj * x[ji] + α * prevHiddenDeltaWji; //Δwji(n) = eta * δjxji +  αΔwji(n-1)

                double wji = w[ji] + Δwjin;
                if(useWeightDecay)
                    wji -= AnnConstants.WEIGHT_DECAY;
                k.setWi(wji, ji);
                prevHiddenDeltaWji = wji;
            }

        }

    }

    /**
     * This method updates the weights in the output-layer.
     * It is different from the above method in that Δwji(n-1)
     * in the hidden layer is different from the output layer
     * @param layer the output layer
     * @param eta the learning rate
     * @param α the momentum factor
     */
    public  void updateOutputWeights(ArrayList<Unit> layer, double eta, double α, boolean useWeightDecay){
        for (Unit k : layer) {
            double w[] = k.getW();
            double x[] = k.getX();
            double δj =  k.getError();

            for (int ji = 0; ji < w.length; ji++) {
                double Δwjin = eta * δj * x[ji] + α * prevOutputDeltaWji; //Δwji(n) = eta * δjxji +  αΔwji(n-1)
                double wji = w[ji] + Δwjin;
                if(useWeightDecay)
                    wji -= AnnConstants.WEIGHT_DECAY;
                k.setWi(wji, ji);
                prevOutputDeltaWji = wji;
            }

        }

    }


    /**
     * This is a backup method to update weights in a network layer without using momentum
     * @param layer the layer to update
     * @param eta the learning rate
     */
    public  void updateWeights(ArrayList<Unit> layer, double eta) {

        for (Unit k : layer) {
            double w[] = k.getW();
            double x[] = k.getX();
            double δj =  k.getError();

            for (int ji = 0; ji < w.length; ji++) {
                double Δwji = eta * δj * x[ji];
                double wji = w[ji] + Δwji;
                k.setWi(wji, ji);
            }

        }

    }

    /**
     * This method computes the error (MSE) on a set of examples.
     * It is mainly used to get the smallest error on the validation set
     * @param examples the examples to use to compute error
     * @return
     */
    public double E(ArrayList<Example> examples){
        ArrayList<Unit> outputLayer = network.getOutputLayer();
        ArrayList<Unit> hiddenLayer = network.getOutputLayer();
        double errorSum = 0;
        double n = examples.size();

        for(Example example : examples) {
            network.setX(example.getX()); //propagate the input x through hidden layer
            network.setT(example.getT());
            double newX[] = getHiddenOutput(hiddenLayer);

            double t[] = example.getT();

            int k = 1;
            for (Unit o : outputLayer) {
                o.setX(newX); //propagate new x through output layer
                double tkd = t[k++];
                double okd = o.getOutput(); //get the output for this unit
                errorSum += Math.pow(tkd - okd, 2);
            }
        }
        return (1.0 / n) * errorSum;
    }


    public FeedForwardNetwork getNetwork(){
        return network;
    }

    public int getOptimalIterations(){
        return optimalIterations;
    }
}
