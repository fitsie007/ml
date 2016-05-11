package org.fitz.ml.main;

import org.fitz.ml.Attributes;
import org.fitz.ml.constants.DtreeConstants;
import org.fitz.ml.dtree.*;

import java.util.ArrayList;

/** This is the main class for doing pre-processing and
 * running the ID3 algorithm.
 * Created by FitzRoi on 2/13/16.
 */
public class RunDTree {

    public static void main(String[] args) {
        boolean isPrintBeforeProcessing = false;
        boolean isPrintAfterProcessing = false;
        boolean isCorruptData = false;

        //select tennis dataset by default
        String trainingFile = DtreeConstants.TENNIS_TRAIN_FILE;
        String attrFile = DtreeConstants.TENNIS_ATTR_FILE;
        String testFile = DtreeConstants.TENNIS_TEST_FILE;

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-experiment")) {
                    if (i + 1 < args.length)
                        if (args[i + 1].equalsIgnoreCase("testIris")) {
                            attrFile = DtreeConstants.IRIS_ATTR_FILE;
                            trainingFile = DtreeConstants.IRIS_TRAIN_FILE;
                            testFile = DtreeConstants.IRIS_TEST_FILE;
                        } else if (args[i + 1].equalsIgnoreCase("testIrisNoisy")) {
                            attrFile = DtreeConstants.IRIS_ATTR_FILE;
                            trainingFile = DtreeConstants.IRIS_TRAIN_FILE;
                            testFile = DtreeConstants.IRIS_TEST_FILE;
                            isCorruptData = true; //corrupt data
                        } else if (args[i + 1].equals("testBoolean")) {
                            attrFile = DtreeConstants.BOOL_ATTR_FILE;
                            trainingFile = DtreeConstants.BOOL_TRAIN_FILE;
                            testFile = DtreeConstants.BOOL_TEST_FILE;
                        } else if (args[i + 1].equals("enjoysport")) {
                            attrFile = DtreeConstants.ENJOY_SPORT_ATTR_FILE;
                            trainingFile = DtreeConstants.ENJOY_SPORT_TRAIN_FILE;
                        } else if (args[i + 1].equals("fitz")) {
                            attrFile = DtreeConstants.TENNIS2_ATTR_FILE;
                            trainingFile = DtreeConstants.TENNIS2_TRAIN_FILE;
                            testFile = DtreeConstants.TENNIS2_TEST_FILE;
                        }
                        else if (args[i + 1].equals("other")) {
                            i++;
                            if(i + 1 < args.length) {
                                attrFile = args[i + 1];
                                i++;
                            }
                            if(i + 1 < args.length) {
                                trainingFile = args[i + 1];
                                i++;
                            }
                            if(i + 1 < args.length) {
                                testFile = args[i + 1];
                            }
                        }

                    i++;
                } else if (args[i].equals("-p")) {
                    isPrintBeforeProcessing = true;
                } else if (args[i].equals("-a")) {
                    isPrintAfterProcessing = true;
                }
            }
        }


        Preprocessor pp = new Preprocessor();
        pp.parseAttributeFile(attrFile);
        pp.setInstances(pp.parseDataset(trainingFile));
        pp.setTestSet(pp.parseDataset(testFile));

        // print the training data if user selects option
        if (isPrintBeforeProcessing)
            pp.printTrainingData();

        //check if exist and process continuous attributes
        //for both training and test data
        pp.processContinuousAttrs();

        //select a validation set from training set
        pp.selectValidationSet();

        // print the training data if user selects option
        if (isPrintAfterProcessing)
            pp.printTrainingData();


        if (isCorruptData) { //conduct special experiment by corrupting training data
            runCorruptDataExp(pp);
        }

        else { //run regular experiments
            Learner learner = new Learner();
            learner.setAttributes(pp.getAttributes());
            Tree tree = learner.getTree();

            Node root = learner.ID3(pp.getInstances(), pp.getTargetAttribute(), pp.getOtherAttributes());
            tree.setRoot(root);
            tree.deriveRules();


            System.out.print("\n================ DECISION TREE ================");
            tree.printTree(tree.getRoot(), "");


            System.out.print("\n===================== RULES ===================\n");
            tree.printRules();


            //try post-rule pruning, printing accuracy before and after
            tree.tryPostRulePruning(pp.getValidationSet(), pp.getAttributes());

            Classifier classifier = new Classifier(pp.getAttributes());
            classifier.classify(pp.getInstances(), tree.getRules(), " TRAINING SET ");
//            classifier.classify(pp.getValidationSet(), tree.getRules(), " VALIDATION SET ");
            classifier.classify(pp.getTestSet(), tree.getRules(), " TEST SET ");
        }
        System.out.print("\n");

    }

    /**
     * This is a special method to conduct an experiment by corrupting the training data
     * (ie changing the class). Here, we randomly assign a new class from the remaining classes.
     * By default, we incrementally corrupt 2% of the instances randomly
     * up to 20%. To change these percentages, see the DTreeConstants class
     *
     * @param pp a preprocessor containing the instances, testset,
     *           validationset (for post-rule pruning) and attributes
     */
    public static void runCorruptDataExp(Preprocessor pp) {
        ArrayList<Integer> uncorruptedIndexes = new ArrayList<Integer>();
        ArrayList<ArrayList<String>> dataset = pp.getInstances();
        Attributes attributes = pp.getAttributes();
        int numInstances = dataset.size();

        Learner learner = new Learner();
        learner.setAttributes(pp.getAttributes());
        Tree tree = learner.getTree();
        Node root;

        System.out.print("\n===================== TEST CORRUPT DATA EXPERIMENT ===================\n");

        //run experiment with 0% corrupted data
        System.out.print("======== Running experiment with 0% Corrupted Data =====\n");
        root = learner.ID3(pp.getInstances(), pp.getTargetAttribute(), pp.getOtherAttributes());
        tree.setRoot(root);
        tree.deriveRules();
        tree.printRules();
        tree.tryPostRulePruning(pp.getValidationSet(), pp.getAttributes());


        //first, add all indexes to uncorrupted set
        for (int i = 0; i < dataset.size(); i++)
            uncorruptedIndexes.add(i);

        //Determine number of instances to corrupt
        int increments = (int) (DtreeConstants.CORRUPT_DATA_STEP_FACTOR * numInstances);
        int maxCorruptCount = (int) (DtreeConstants.CORRUPT_DATA_MAX_FACTOR * numInstances);

        System.out.println("Increments: (" + (DtreeConstants.CORRUPT_DATA_STEP_FACTOR * 100.0) + "% of " + numInstances + " instance(s)) = " + increments);
        System.out.println("Maximum # to corrupt: (" + (DtreeConstants.CORRUPT_DATA_MAX_FACTOR * 100.0) + "% of " + numInstances + " instance(s)) = " + maxCorruptCount);

        if(increments > 0 && maxCorruptCount > 0) {
            //run experiment on corrupted data
            //for example, keep picking 2 instances randomly 10 times to corrupt 20 instances
            for (int i = 0; i < maxCorruptCount / increments; i++) {
                for (int j = 0; j < increments; j++) {
                    int randIndex = (int) (Math.random() * uncorruptedIndexes.size());
                    //remove index of instance to corrupt so we don't corrupt the same instance multiple times
                    int instanceIndex = uncorruptedIndexes.remove(randIndex);
                    doCorruptClass(dataset.get(instanceIndex), attributes.getTargetAttribute().getValues(), attributes.getTargetIndex());
                }

                //run experiment with incrementally corrupted data

                System.out.println("\n===== Running experiment with " + ((i + 1) * increments) + " corrupted instance(s) =====");
                root = learner.ID3(pp.getInstances(), pp.getTargetAttribute(), pp.getOtherAttributes());
                tree.setRoot(root);
                tree.deriveRules();
                tree.tryPostRulePruning(pp.getValidationSet(), pp.getAttributes());
            }
        }
        else {
            System.out.println("\n>> Could not complete experiment. Corrupt % is too small");
        }

    }

    /**
     * This method corrupts the classification of a given instance
     * @param instance instance from the training dataset
     * @param classLabels list of possible classes
     * @param targetIndex the index of the target class in the instance (row)
     */
    public static void doCorruptClass(ArrayList<String> instance, String classLabels[], int targetIndex){
        String currentClass = instance.get(targetIndex);
        int otherClassCount = classLabels.length -1;
        String remainingClasses [] = new String[otherClassCount];

        //select all other classes
        for(int i =0, j=0; i <classLabels.length; i++)
            if(!currentClass.equals(classLabels[i]))
                remainingClasses[j++] = classLabels[i];
        int newRandomIndex  = (int) (Math.random() * otherClassCount); //pick a random class
//        System.out.println("Changed from " + currentClass + " to " + remainingClasses[newRandomIndex]);
        instance.set(targetIndex, remainingClasses[newRandomIndex]);

    }
}
