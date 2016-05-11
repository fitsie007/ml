package org.fitz.ml.main;

import org.fitz.ml.constants.GAconstants;
import org.fitz.ml.ga.GA;
import org.fitz.ml.ga.Hypothesis;
import org.fitz.ml.ga.Preprocessor;
import org.fitz.ml.ga.SelectionStrategy;

/**
 * This is the main class for running the algorithm
 * Created by FitzRoi on 3/28/16.
 */
public class RunGA {
    public static void main(String[] args) {

        //select Iris dataset by default
        String trainingFile = GAconstants.IRIS_TRAIN_FILE;
        String attrFile = GAconstants.IRIS_ATTR_FILE;
        String testFile = GAconstants.IRIS_TEST_FILE;
        String experiment = "TESTIRIS";

        int p = GAconstants.P; //number of hypotheses
        int g = GAconstants.NUM_GENERATIONS; //number of generations
        int x = GAconstants.MIN_RULES; //minimum number of rules in each hypothesis
        int y = GAconstants.MAX_RULES; //maximum number of rules in each hypothesis
        double r = GAconstants.REPLACEMENT_RATE; //hypotheses to replace in crossover
        double m = GAconstants.MUTATION_RATE; //% hypotheses to mutate in each step
        double fitnessThreshold = GAconstants.FITNESS_THRESHOLD;
        SelectionStrategy strategy = SelectionStrategy.RANK;


        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-experiment")) {
                    if (i + 1 < args.length)
                        if (args[i + 1].equalsIgnoreCase("testTennis")) {
                            attrFile = GAconstants.TENNIS_ATTR_FILE;
                            trainingFile = GAconstants.TENNIS_TRAIN_FILE;
                            testFile = GAconstants.TENNIS_TEST_FILE;
                            experiment = args[i + 1].toUpperCase();
                            i++;
                        } else if (args[i + 1].equalsIgnoreCase("testIrisReplacement")) {
                            experiment = args[i + 1].toUpperCase();
                            i++;
                        } else if (args[i + 1].equalsIgnoreCase("testIrisSelection")) {
                            experiment = args[i + 1].toUpperCase();
                            i++;
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
                            experiment = "UNKNOWN DATASET";
                        }
                } else if (args[i].equalsIgnoreCase("-p")) {
                    p = Integer.parseInt(args[i + 1]);
                    i++;
                } else if (args[i].equalsIgnoreCase("-r")) {
                    r = Double.parseDouble(args[i + 1]);
                    i++;
                } else if (args[i].equalsIgnoreCase("-m")) {
                    m = Double.parseDouble(args[i + 1]);
                    i++;
                } else if (args[i].equalsIgnoreCase("-x")) {
                    x = Integer.parseInt(args[i + 1]);
                    i++;
                } else if (args[i].equalsIgnoreCase("-y")) {
                    y = Integer.parseInt(args[i + 1]);
                    i++;
                } else if (args[i].equalsIgnoreCase("-g")) {
                    g = Integer.parseInt(args[i + 1]);
                    i++;
                } else if (args[i].equalsIgnoreCase("-f")) { //accept fitnessThreshold as percent and square it
                    fitnessThreshold = Math.pow(Double.parseDouble(args[i + 1]) * 100.0, 2);
                    i++;
                } else if (args[i].equalsIgnoreCase("-s")) {
                    switch (Integer.parseInt(args[i + 1])) {
                        case 1:
                            strategy = SelectionStrategy.FITNESS_PROPORTIONATE;
                            break;
                        case 2:
                            strategy = SelectionStrategy.TOURNAMENT;
                            break;
                        case 3:
                            strategy = SelectionStrategy.RANK;
                            break;
                    }
                    i++;
                }
            }
        }

        Preprocessor pp = new Preprocessor();
        pp.parseAttributeFile(attrFile);
        pp.setInstances(pp.parseDataset(trainingFile));
        pp.setTestSet(pp.parseDataset(testFile));
        pp.processContinuousAttrs();
        GA genAlgo = new GA();
        genAlgo.init(pp.getInstances(),
                pp.getAttributes(),
                pp.getTestSet(),
                x,
                y);

        //run the selected experiment
        System.out.println("\n=== RUNNING " + experiment + " EXPERIMENT === \n");

        if (experiment.equalsIgnoreCase("testIrisSelection")) {
            testIrisSelection(genAlgo, p, r, m);
        } else if (experiment.equalsIgnoreCase("testIrisReplacement")) {
            testIrisReplacement(genAlgo, p, m, g);
        } else {
            Hypothesis fittest;
            if (g > 0) //if number of generation specified (>0), use this as stopping criterion
                fittest = genAlgo.runGA(p, r, m, g, strategy);
            else //use fitness threshold as stopping criterion
                fittest = genAlgo.runGA(p, r, m, fitnessThreshold, strategy);

            System.out.println("\n=====RULES=====");
            genAlgo.printRules(fittest);

            System.out.print("\nACCURACY ON TRAINING SET (" + experiment + "): ");
            System.out.print(fittest.correct(pp.getInstances()) + "%\n");

            System.out.print("\nACCURACY ON TEST SET (" + experiment + "): ");
            System.out.print(fittest.correct(pp.getTestSet()) + "%\n");
        }

    }

    /**
     * This method is used to run the testIrisSelection experiment which
     * varies generation number and outputs generation number and test
     * set accuracy for each of the three selection strategies (fitness proportionate, tournament, rank)
     * @param ga a genetic algorithm object initialized with datasets
     * @param p the number of hypotheses
     * @param r the replacement rate
     * @param m the mutation rate
     */
    public static void testIrisSelection(GA ga, int p, double r, double m){
        Hypothesis fittest;
        for(int genNum = GAconstants.MIN_NUM_GENERATIONS; genNum <= GAconstants.MAX_NUM_GENERATIONS; genNum += GAconstants.MIN_NUM_GENERATIONS) {

            for(SelectionStrategy strategy: SelectionStrategy.values()) {

                fittest = ga.runGA(p, r, m, genNum, strategy);

                System.out.print("\nUSING " + strategy +" SELECTION STRATEGY ON " + genNum +" GENERATIONS\n");

                System.out.print("Accuracy on training set: ");
                System.out.print(fittest.correct(ga.getInstances()) + "%\n");

                System.out.print("Accuracy on test set: ");
                System.out.print(fittest.correct(ga.getTestSet()) + "%\n");
            }
            System.out.println("////////////");
        }

    }


    /**

     * This method is used to run the testIrisSelection experiment which
     * varies replacement rate and outputs replacement rate and test
     * set accuracy for each of the three selection strategies (fitness proportionate, tournament, rank)
     * @param ga a genetic algorithm object initialized with datasets
     * @param p the number of hypotheses
     * @param m the mutation rate
     * @param g number of generations to use as stopping criterion
     */
    public static void testIrisReplacement(GA ga, int p, double m, int g) {
        Hypothesis fittest;
        //replace from .1 to .9 percent of hypothesis during crossover
            for (double r = GAconstants.MIN_REPLACEMENT_RATE; r <= GAconstants.MAX_REPLACEMENT_RATE; r += GAconstants.REPLACEMENT_STEP_FACTOR ) {
                for (SelectionStrategy strategy : SelectionStrategy.values()) {
                    fittest = ga.runGA(p, r, m, g, strategy);
                    System.out.format("Replacement Rate: %.2f\n", r);

                    System.out.print("Accuracy on test set: ");
                    System.out.print(fittest.correct(ga.getTestSet()) + "%\n\n");
                }
            }
            System.out.println("//////////");
        }
}
