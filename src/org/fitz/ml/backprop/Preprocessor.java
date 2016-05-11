package org.fitz.ml.backprop;

import org.fitz.ml.Attribute;
import org.fitz.ml.AttributeType;
import org.fitz.ml.Attributes;
import org.fitz.ml.constants.AnnConstants;
import org.fitz.util.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by FitzRoi on 3/2/16.
 */
public class Preprocessor {
    private ArrayList<Example> examples;
    private ArrayList<Example> exceptValidationSet; //examples excluding validation set
    private ArrayList<Example> validationSet;
    private ArrayList<Example> testSet;
    private Attributes inputAttributes;
    private Attributes targetAttributes;


    /**
     * This function parses an attribute file and creates a list of attributes
     * @param filename the filename of the attribute file
     */
    public void parseAttributeFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            Attributes temp;
            inputAttributes = new Attributes();
            targetAttributes = new Attributes();
            String line;
            String [] tempAttr;

            int count = 0;
            temp = inputAttributes;
            while ((line = br.readLine()) != null) {
                tempAttr = line.trim().split("\\s+");

                if(line.trim().isEmpty()){ //switch from input attributes to targetAttributes if new line encountered
                    temp = targetAttributes;
                    count = 0;
                }

                String attrVals[] = new String[tempAttr.length - 1];
                if(tempAttr.length > 1) {
                    AttributeType type;
                    String attrName = tempAttr[0];
                    if(Util.isNumeric(tempAttr[1])) {
                        type = AttributeType.NUMERIC;
                        System.arraycopy(tempAttr, 1, attrVals, 0, tempAttr.length - 1); //copy the values to the attribute

                    }
                    else {
                        type = AttributeType.STRING;
                        for(int i = 1; i < tempAttr.length; i++)
                            attrVals[i-1] = Util.capitalize(tempAttr[i]); //example from 't' to 'T'
                    }

                    Attribute attr = new Attribute(attrName, type, count++, attrVals);
                    temp.add(attr); //add the attribute to the list of attributes
                }
            }


            System.out.println("Processed Attribute File");
            System.out.println("Found " + inputAttributes.size() + " input  attribute(s)");
            System.out.println("Found " + targetAttributes.size() + " output  attribute(s)");

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * This function parses a numeric dataset file (training or test data)
     * and creates a set of instances of the form ArrayList<Example>>
     * where Example represents an instance containing vectors <x, t>.
     * @param filename the filename of the data-file
     * @return the examples parsed from the file
     */

    public ArrayList<Example> parseNumericDataset(String filename) {
        ArrayList<Example> examples = new ArrayList<Example>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            String vals[];

            while ((line = br.readLine()) != null) {
                vals = line.trim().split("\\s+");

                if (vals.length >= inputAttributes.size()) {
                    int nIn = getVectorSize(inputAttributes); //number of attributes
                    int nOut = getVectorSize(targetAttributes);
                    Example example = new Example(nIn, nOut);

                    String inputs[] = Arrays.copyOfRange(vals, 0, inputAttributes.size());
                    String outputs[] = Arrays.copyOfRange(vals, inputAttributes.size(), vals.length);

                    double x[] = getXvector(inputs, inputAttributes, nIn);
                    double t[] = getTargetVector(outputs, targetAttributes, nOut);

                    example.setX(x);
                    example.setT(t);
                    examples.add(example);
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Processed dataset file (" + filename + ")");
        System.out.println("Found " + examples.size() + " instance(s)");
        return examples;
    }

    /**
     * This method parses a non-numeric dataset file
     * and creates a set of instances of the form ArrayList<Example>>
     * where Example represents an instance containing vectors <x, t>.
     * @param filename the dataset file
     * @return the examples parsed from the file
     */

    public ArrayList<Example> parseDataset(String filename) {
        ArrayList<Example> examples = new ArrayList<Example>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            String vals[];

            while ((line = br.readLine()) != null) {
                vals = line.trim().split("\\s+");

                if (vals.length >= inputAttributes.size()) {
                    int nIn = getVectorSize(inputAttributes); //number of attributes
                    int nOut = getVectorSize(targetAttributes);
                    Example example;

                    ArrayList<String> dataInstance = new ArrayList<String>();
                    int numAttributes = inputAttributes.size();
                    dataInstance.addAll(Arrays.asList(vals).subList(0, numAttributes)); //copy attributes except target attribute

                    if(isNumeric(targetAttributes)) {
                        example = new Example(nIn, nOut, dataInstance);
                    }
                    else {
                        example = new Example(nIn, nOut, dataInstance, vals[numAttributes]);
                    }


                    String inputs[] = Arrays.copyOfRange(vals, 0, inputAttributes.size());
                    String outputs[] = Arrays.copyOfRange(vals, inputAttributes.size(), vals.length);

                    double x[] = getXvector(inputs, inputAttributes, nIn);
                    double t[] = getTargetVector(outputs, targetAttributes, nOut);

                    example.setX(x); //store the x vector
                    example.setT(t); //store the target vector
                    examples.add(example);
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Processed dataset file (" + filename + ")");
        System.out.println("Found " + examples.size() + " instances");
        return examples;
    }

    /**
     * This method partitions the examples into validation-set versus except-validation-set
     * based on a fold number.
     * @param fold the current fold of k (0 - k-1) folds to use for validation set
     */
    public void selectValidationSet(int fold){

        int validationSize = examples.size() / AnnConstants.K;
        int start = fold * validationSize;
        int stop = (fold == AnnConstants.K) ? examples.size() : (start + validationSize - 1);

        validationSet = new ArrayList<Example>();
        exceptValidationSet = new ArrayList<Example>();

        for(int i = 0; i < examples.size(); i++ ){
            if(i < start || i > stop){ //add examples not in the validation set to another training set
                exceptValidationSet.add(examples.get(i));
            }
            else
            validationSet.add(examples.get(i));
        }
    }

    /**
     * This method determines the size of the 1-of-n x vector to be created
     * based on the non-numeric attributes.
     *
     * @return the size of a vector
     */
    public int getVectorSize(Attributes attributes){
        int n = 1;//skip x0 or t0
        for(Attribute attribute : attributes.getAttributes()){
            if(attribute.getType() == AttributeType.NUMERIC) //if value is numeric, it takes up one array slot
                n += 1;
            else
                n += attribute.getValues().length; //otherwise we use 1-of-n (example: Outlook{Sunny, Overcast, Rain} -> Outlook{001, 010, 011}
        }
        return n;
    }

    /**
     * This method checks if the attributes are numeric
     * @param attributes the attributes to check
     * @return true if numeric attribute found, false otherwise
     */
    public boolean isNumeric(Attributes attributes) {
        boolean isNumericVals = false;
        for (Attribute attribute : attributes.getAttributes()) {
            if (attribute.getType() == AttributeType.NUMERIC){
                isNumericVals = true;
            }

        }
        return isNumericVals;
    }

    /**
     * This method creates a X vector from the input string
     * @param vals the string of values (e.g. string parsed from data file)
     * @param attributes the attributes
     * @param size the size of the x vector
     * @return X vector
     */
    public double[] getXvector(String vals[], Attributes attributes, int size){
        double x[] = new double[size];
        x[0] = 1; //x0

        int count = 1;
        for(int i = 0; i < vals.length; i++){
            if(Util.isNumeric(vals[i])) {
                x[count++] = Double.parseDouble(vals[i]);
            }
            else{ //get 1-of-n representation
                String vecStr = Util.getOneOfN(vals[i], attributes.getAttributes().get(i));
                int vecSize = vecStr.length();
                int c = 0;
                for(int j = count; j < (count + vecSize); j++) {
                    x[j] = Double.parseDouble(vecStr.charAt(c++) +"");
                }
                count += vecSize;
            }
        }

        return x;
    }


    /**
     * This method creates a target vector from the input string
     * @param vals the string of values (e.g. string parsed from data file)
     * @param attributes the attributes
     * @param size the size of the target vector
     * @return target vector T
     */
    public static double[] getTargetVector(String vals[], Attributes attributes, int size){
        double t[] = new double[size];
        t[0] = 1; //t0

        int count = 1;
        for(int i = 0; i < vals.length; i++){
            if(Util.isNumeric(vals[i])) {
                t[count++] = Double.parseDouble(vals[i]);
            }
            else{ //convert string value to 1-of-n
                String vecStr = Util.getOneOfN(vals[i], attributes.getAttributes().get(i));
                int vecSize = vecStr.length();
                int c = 0;
                //set the target close to 0 or one since to be more compatible with sigmoid
                for(int j = count; j < (count + vecSize); j++) {
                    char outChar = vecStr.charAt(c++);
                    if(outChar == '1')
                        t[j] = AnnConstants.CLOSE_TO_ONE;
                    else if(outChar == '0')
                        t[j] = AnnConstants.CLOSE_TO_0;
                }

                count += vecSize;
            }
        }

        return t;
    }


    public void printExamples(){
        for (Example example : examples) {
            double x[] = example.getX();
            double t[] = example.getT();
            for (int j = 1; j < example.getX().length; j++) { //skip x0
                System.out.print(String.format("%.0f",x[j]) + "\t");
            }

            System.out.print("\t");
            for (int j = 1; j < example.getT().length; j++) { //skip t0
                System.out.print(String.format("%.0f",t[j]) + "\t");
            }

            System.out.println();
        }
    }



    public Attributes getInputAttributes(){return inputAttributes;}
    public Attributes getTargetAttributes(){return targetAttributes;}

    public int getXSize(){
        return getVectorSize(inputAttributes);
    }
    public int getTSize(){
        return getVectorSize(targetAttributes);
    }

    public ArrayList<Example> getExamples() {
        return examples;
    }
    public void setExamples(ArrayList<Example> examples) {
        this.examples = examples;
    }

    public void setTestSet(ArrayList<Example> testSet) {
        this.testSet = testSet;
    }
    public ArrayList<Example> getTestSet(){return testSet;}

    public ArrayList<Example> getValidationSet(){return  validationSet;}

    public ArrayList<Example> allExceptValidationSet(){return exceptValidationSet;}





}
