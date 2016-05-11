package org.fitz.ml.main;

import org.fitz.ml.Attributes;
import org.fitz.ml.backprop.Backpropagation;
import org.fitz.ml.backprop.Classifier;
import org.fitz.ml.backprop.Example;
import org.fitz.ml.backprop.Preprocessor;
import org.fitz.ml.constants.AnnConstants;

import java.util.ArrayList;

/**
 * This is the main class to run the experiments
 * Created by FitzRoi on 3/2/16.
 */
public class RunBackProp {
    public static void main(String[] args) {

        boolean isCorruptData = false;
        double eta = AnnConstants.ETA;
        double momentum = AnnConstants.MOMENTUM;
        int nHidden = AnnConstants.N_HIDDEN;
        int iterations = AnnConstants.ITERATIONS;


        //Select Identity dataset by default
        String experiment = "testIdentity";
        String trainingFile = AnnConstants.IDENTITY_TRAIN_FILE;
        String attrFile = AnnConstants.IDENTITY_ATTR_FILE;
        String testFile = AnnConstants.IDENTITY_TRAIN_FILE;

        //process program arguments
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("-experiment")) {
                    if (i + 1 < args.length)
                        if (args[i + 1].equalsIgnoreCase("testTennis")) {
                            attrFile = AnnConstants.TENNIS_ATTR_FILE;
                            trainingFile = AnnConstants.TENNIS_TRAIN_FILE;
                            testFile = AnnConstants.TENNIS_TEST_FILE;
                            experiment = "testTennis";
                        } else if (args[i + 1].equalsIgnoreCase("testIris")) {
                            attrFile = AnnConstants.IRIS_ATTR_FILE;
                            trainingFile = AnnConstants.IRIS_TRAIN_FILE;
                            testFile = AnnConstants.IRIS_TEST_FILE;
                            experiment = "testIris";
                        } else if (args[i + 1].equalsIgnoreCase("testIrisNoisy")) {
                            attrFile = AnnConstants.IRIS_ATTR_FILE;
                            trainingFile = AnnConstants.IRIS_TRAIN_FILE;
                            testFile = AnnConstants.IRIS_TEST_FILE;
                            experiment = "testIrisNoisy";
                            isCorruptData = true; //corrupt data
                        } else if (args[i + 1].equals("other")) {
                            i++;
                            if (i + 1 < args.length) {
                                attrFile = args[i + 1];
                                i++;
                            }
                            if (i + 1 < args.length) {
                                trainingFile = args[i + 1];
                                i++;
                            }
                            if (i + 1 < args.length) {
                                testFile = args[i + 1];
                                i++;
                            }
                            experiment = "other";
                        }
                    i++;
                } else if (args[i].equalsIgnoreCase("-eta")) {

                    if (i + 1 < args.length) {
                        eta = Double.parseDouble(args[i + 1]);
                        i++;
                    }
                } else if (args[i].equalsIgnoreCase("-momentum")) {
                    if (i + 1 < args.length) {
                        momentum = Double.parseDouble(args[i + 1]);
                        i++;
                    }
                } else if (args[i].equalsIgnoreCase("-iterations")) {
                    if (i + 1 < args.length) {
                        iterations = Integer.parseInt(args[i + 1]);
                        i++;
                    }
                } else if (args[i].equalsIgnoreCase("-hidden")) {
                    if (i + 1 < args.length) {
                        nHidden = Integer.parseInt(args[i + 1]);
                        i++;
                    }
                }
            }
        }


        //create a preprocessor to process datasets
        Preprocessor pp = new Preprocessor();
        pp.parseAttributeFile(attrFile);

        if (pp.isNumeric(pp.getInputAttributes())) {
            pp.setExamples(pp.parseNumericDataset(trainingFile));
            pp.setTestSet(pp.parseNumericDataset(testFile));
        } else { //requires special handling of 1-of-n
            pp.setExamples(pp.parseDataset(trainingFile));
            pp.setTestSet(pp.parseDataset(testFile));
        }

        ArrayList<Example> examples = pp.getExamples();
        ArrayList<Example> testSet = pp.getTestSet();
        Attributes targetAttributes = pp.getTargetAttributes();
        int nIn = pp.getXSize();
        int nOut = pp.getTSize() - 1; //exclude t0
        double accuracy;


        if (isCorruptData) { //conduct special experiment by corrupting training data
            runCorruptDataExp(pp, eta, nIn, nOut, nHidden, iterations, momentum, AnnConstants.DONT_USE_VALIDATION_SET);

            //reload file since data corrupted
            System.out.print("Now reloading data to run experiment with validation set...\n");
            pp.setExamples(pp.parseDataset(trainingFile));
            runCorruptDataExp(pp, eta, nIn, nOut, nHidden, iterations, momentum, AnnConstants.USE_VALIDATION_SET);

        } else {
            Backpropagation bp = new Backpropagation();
            bp.runBackprop(examples, eta, nIn, nOut, nHidden, iterations, momentum, AnnConstants.NO_WEIGHT_DECAY);
            Classifier classifier = new Classifier();
            //only training set available for testIdentity
            if (experiment.equals("testIdentity"))
                classifier.classify(examples, targetAttributes, bp, AnnConstants.DO_PRINT_CLASSIFICATIONS, experiment.toUpperCase() + " TRAINING SET");
            else {
                //classify training set
                accuracy = classifier.classify(examples, targetAttributes, bp, AnnConstants.DO_NOT_PRINT_CLASSIFICATIONS, experiment.toUpperCase() + " TRAINING SET");
                System.out.print("ACCURACY FOR " + experiment.toUpperCase() + " TRAINING SET: " + accuracy + "%\n");

                //classify test set
                accuracy = classifier.classify(testSet, targetAttributes, bp, AnnConstants.DO_NOT_PRINT_CLASSIFICATIONS, experiment.toUpperCase() + " TEST SET");
                System.out.print("ACCURACY FOR " + experiment.toUpperCase() + " TEST SET: " + accuracy + "%\n");
            }
        }
    }

    /**
     * This method runs experiments by corrupting the datasets and attributes
     * @param pp the preprocessor containing the datasets
     * @param eta the learning rate
     * @param nIn the size of the input vector
     * @param nOut the number of output units
     * @param nHidden the number of hidden units
     * @param iterations the number of iterations
     * @param momentum the momentum
     * @param useValidationSet boolean to specify whether to use validation set
     */
    public static void runCorruptDataExp(Preprocessor pp, double eta, int nIn, int nOut, int nHidden, int iterations, double momentum, boolean useValidationSet) {
        Backpropagation bp = new Backpropagation();
        ArrayList<Integer> uncorruptedIndexes = new ArrayList<Integer>();
        ArrayList<Example> examples = pp.getExamples();
        Attributes targetAttributes = pp.getTargetAttributes();
        int numInstances = examples.size();
        int idealIterations = iterations;


        if (useValidationSet) {
            System.out.print("\n========== TEST CORRUPT DATA EXPERIMENT (WITH VALIDATION SET) =========\n");

            //first run backprop and find ideal number of iterations (ie. iterations that yield smallest error on validation set)
            int k = AnnConstants.K;
            int sumIterations = 0;
            System.out.print("Now determining average iterations using " + k +"-fold cross validation\n");
            System.out.print("Note: This may take a few minutes ...\n");
            //find the average number of iterations
            for (int i = 0; i < k; i++) {
                pp.selectValidationSet(i); //separate examples into validation set and other set
                bp.runKFoldBackprop(pp.allExceptValidationSet(), eta, nIn, nOut, nHidden, AnnConstants.MAX_ITERATIONS, momentum, pp.getValidationSet(), AnnConstants.NO_WEIGHT_DECAY);
                sumIterations += bp.getOptimalIterations();
            }
            System.out.print(pp.getValidationSet().size() +" example(s) in each fold of validation set; " +pp.allExceptValidationSet().size() +" remaining for training\n");

            idealIterations = sumIterations / k;
            System.out.print("Average iterations using "+ k +"-fold validation = " + idealIterations + "\n\n");
        }

        else
            System.out.print("\n========== TEST CORRUPT DATA EXPERIMENT (WITHOUT VALIDATION SET) =========\n");

        //run experiment with 0% corrupted data
        System.out.print("======== Running experiment with 0% Corrupted Data =====\n");
        bp.runBackprop(examples, eta, nIn, nOut, nHidden, idealIterations, momentum, AnnConstants.NO_WEIGHT_DECAY);

        Classifier classifier = new Classifier();
        double accuracy = classifier.classify(pp.getTestSet(), targetAttributes, bp, AnnConstants.DO_NOT_PRINT_CLASSIFICATIONS, null);
        System.out.print("ACCURACY: " + accuracy + "%\n");

        //first, add all indexes to uncorrupted set
        for (int i = 0; i < examples.size(); i++)
            uncorruptedIndexes.add(i);

        //Determine number of instances to corrupt
        int increments = (int) (AnnConstants.CORRUPT_DATA_STEP_FACTOR * numInstances);
        int maxCorruptCount = (int) (AnnConstants.CORRUPT_DATA_MAX_FACTOR * numInstances);

        System.out.println("Increments: (" + (AnnConstants.CORRUPT_DATA_STEP_FACTOR * 100.0) + "% of " + numInstances + " instance(s)) = " + increments);
        System.out.println("Maximum # to corrupt: (" + (AnnConstants.CORRUPT_DATA_MAX_FACTOR * 100.0) + "% of " + numInstances + " instance(s)) = " + maxCorruptCount);

        if (increments > 0 && maxCorruptCount > 0) {
            //run experiment on corrupted data
            //for example, keep picking 2 instances randomly 10 times to corrupt 20 instances

            for (int i = 0; i < maxCorruptCount / increments; i++) {
                for (int j = 0; j < increments; j++) {
                    int randIndex = (int) (Math.random() * uncorruptedIndexes.size());
                    //remove index of instance to corrupt so we don't corrupt the same instance multiple times
                    int instanceIndex = uncorruptedIndexes.remove(randIndex);
                    doCorruptClass(examples.get(instanceIndex), pp.getTargetAttributes());
                }

                //run experiment with incrementally corrupted data

                System.out.println("\n===== Running experiment with " + ((i + 1) * increments) + " corrupted instance(s) =====");


                //run backprop with ideal iterations to get the best weights instead of saving the weights
                bp.runBackprop(examples, eta, nIn, nOut, nHidden, idealIterations, momentum, AnnConstants.NO_WEIGHT_DECAY);
                accuracy = classifier.classify(pp.getTestSet(), targetAttributes, bp, AnnConstants.DO_NOT_PRINT_CLASSIFICATIONS, null);
                System.out.print("ACCURACY: " + accuracy + "%\n");
            }
        } else {
            System.out.println("\n>> Could not complete experiment. Corrupt % is too small");
        }

    }

    /**
     * This method corrupts an instance by changing the target class
     * @param example the instance to corrupt
     * @param targetAttributes the target attributes to use to determine a new class
     */
    public static void doCorruptClass(Example example, Attributes targetAttributes) {

        String currentClass = example.getTargetOutput();
        String[] classLabels = targetAttributes.get(0).getValues();
        int otherClassCount = classLabels.length - 1;
        String remainingClasses[] = new String[otherClassCount];

        //select all other classes
        for (int i = 0, j = 0; i < classLabels.length; i++) {
            if (!currentClass.equals(classLabels[i])) {
                remainingClasses[j++] = classLabels[i];

            }
        }
        int newRandomIndex = (int) (Math.random() * otherClassCount); //pick a random class
        String newClass = remainingClasses[newRandomIndex];
//        System.out.println("Changed from " + currentClass + " to " + newClass);

        double newTargetVec[] = Preprocessor.getTargetVector(new String[]{newClass}, targetAttributes, example.getT().length);
        example.setT(newTargetVec);

    }
}
