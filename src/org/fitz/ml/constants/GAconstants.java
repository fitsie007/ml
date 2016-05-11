package org.fitz.ml.constants;

/**
 * This class initializes various constants used in the program
 * Created by FitzRoi on 3/28/16.
 */
public class GAconstants {
    public static String TENNIS_ATTR_FILE= "./data/ga/tennis-attr.txt";
    public static String TENNIS_TRAIN_FILE= "./data/ga/tennis-train.txt";
    public static String TENNIS_TEST_FILE= "./data/ga/tennis-test.txt";

    public static String IRIS_ATTR_FILE= "./data/ga/iris-attr.txt";
    public static String IRIS_TRAIN_FILE= "./data/ga/iris-train.txt";
    public static String IRIS_TEST_FILE= "./data/ga/iris-test.txt";

    public static Double REPLACEMENT_RATE = 0.6;
    public static Double MUTATION_RATE = 0.001;
    public static Double REPLACEMENT_STEP_FACTOR = 0.1;
    public static Double MIN_REPLACEMENT_RATE = 0.1;
    public static Double MAX_REPLACEMENT_RATE = 0.9;
    public static Double FITNESS_THRESHOLD = 95.0 * 95.0; //at least 95% accuracy
    public static int P = 500;
    public static Double TOURNAMENT_SELECTION_PROBABILITY = 0.6;

    public static String AND_DELIMETER = " ∧ ";
    public static String OR_DELIMETER = " ∨ ";

    public static int MAX_RULES = 5;
    public static int MIN_RULES = 2;

    public static int MIN_NUM_GENERATIONS = 10;
    public static int MAX_NUM_GENERATIONS = 100;
    public static int NUM_GENERATIONS = 0;


}
