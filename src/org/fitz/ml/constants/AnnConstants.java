package org.fitz.ml.constants;

/**
 * This class contains various constants used in the program
 * Created by FitzRoi on 3/2/16.
 */
public class AnnConstants {
    public static String IDENTITY_ATTR_FILE= "./data/ann/identity-attr.txt";
    public static String IDENTITY_TRAIN_FILE= "./data/ann/identity-train.txt";

    public static String TENNIS_ATTR_FILE= "./data/ann/tennis-attr.txt";
    public static String TENNIS_TRAIN_FILE= "./data/ann/tennis-train.txt";
    public static String TENNIS_TEST_FILE= "./data/ann/tennis-test.txt";

    public static String IRIS_ATTR_FILE= "./data/ann/iris-attr.txt";
    public static String IRIS_TRAIN_FILE= "./data/ann/iris-train.txt";
    public static String IRIS_TEST_FILE= "./data/ann/iris-test.txt";


    public static double MIN_WEIGHT = -0.05;
    public static double MAX_WEIGHT = 0.05;
    public static double ETA = 0.05; //learning rate
    public static int N_HIDDEN = 3;
    public static int ITERATIONS = 10000;
    public static int MAX_ITERATIONS = 30000;
    public static double MOMENTUM = 0;
    public static double CLOSE_TO_ONE = 0.9; //close to 1 since sigmoid cannot represent 1
    public static double CLOSE_TO_0 = 0.1; //close to 0 since sigmoid cannot represent 0

    public static Double VALIDATION_SET_FACTOR = 0.3;
    public static int K = 10;
    public static Double CORRUPT_DATA_MAX_FACTOR = 0.2;
    public static Double CORRUPT_DATA_STEP_FACTOR = 0.02;
    public static Double WEIGHT_DECAY = 0.01;

    public static boolean DO_NOT_PRINT_CLASSIFICATIONS = false;
    public static boolean DO_PRINT_CLASSIFICATIONS = true;

    public static boolean USE_VALIDATION_SET = true;
    public static boolean DONT_USE_VALIDATION_SET = false;
    public static boolean NO_WEIGHT_DECAY = false;
    public static boolean USE_WEIGHT_DECAY = true;
}
