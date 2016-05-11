package org.fitz.ml.dtree;


import org.fitz.ml.Attribute;
import org.fitz.ml.AttributeType;
import org.fitz.ml.Attributes;
import org.fitz.ml.constants.DtreeConstants;
import org.fitz.util.Compute;
import org.fitz.util.DataSorter;
import org.fitz.util.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class is used to do pre-processing such as parsing the dataset file and attribute file
 * Created by FitzRoi on 2/13/16.
 */

public class Preprocessor {
    private ArrayList<ArrayList<String>> instances;
    private ArrayList<ArrayList<String>> validationSet;
    private ArrayList<ArrayList<String>> testSet;
    private Attributes attributes;

    public Preprocessor(){}

    /**
     * This function parses a dataset file (training or test data)
     * and creates a set of instances of the form ArrayList<ArrayList<String>>
     * where ArrayList<String> represents an instance or table row
     * and each String is a data/attribute value.
     * @param filename the filename of the data-file
     */

    public ArrayList<ArrayList<String>> parseDataset(String filename) {
        ArrayList<ArrayList<String>> examples = new ArrayList<ArrayList<String>>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            String vals[];

            while ((line = br.readLine()) != null) {
                vals = line.trim().split("\\s+");
                if(vals.length == attributes.size()) {
                    ArrayList<String> row = new ArrayList<String>();
                    for(int i = 0; i < attributes.size(); i++){
                        if(attributes.get(i).getType() == AttributeType.CONTINUOUS)
                            row.add(vals[i]);
                        else
                            row.add(Util.capitalize(vals[i])); //example from 't' to 'T'
                    }

                    examples.add(row);
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Processed dataset File (" + filename +")");
        System.out.println("Found " + examples.size() + " instances");
        return examples;
    }

    /**
     * This function parses an attribute file and creates a list of attributes
     * @param filename the filename of the attribute file
     */
    public void parseAttributeFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            attributes = new Attributes();
            String line;
            String [] tempAttr;

            int count = 0;

            while ((line = br.readLine()) != null) {
                tempAttr = line.trim().split("\\s+");
                String attrVals[] = new String[tempAttr.length - 1];
                if(tempAttr.length > 1) {
                    AttributeType type;
                    String attrName = tempAttr[0];
                    if(Util.isContinuous(tempAttr[1])) {
                        type = AttributeType.CONTINUOUS;
                        System.arraycopy(tempAttr, 1, attrVals, 0, tempAttr.length - 1); //copy the values to the attribute

                    }
                    else {
                        type = AttributeType.STRING;
                        for(int i = 1; i < tempAttr.length; i++)
                            attrVals[i-1] = Util.capitalize(tempAttr[i]); //example from 't' to 'T'
//                        System.arraycopy(tempAttr, 1, attrVals, 0, tempAttr.length - 1);
                    }

                    Attribute attr = new Attribute(attrName, type, count++, attrVals);
                    attributes.add(attr); //add the attribute to the list of attributes
                }
            }

            attributes.setTargetAttribute(attributes.size() - 1); //set last attribute as target concept by default

            System.out.println("Processed Attribute File");
            System.out.println("Found " + attributes.size() + " attributes");
            System.out.println("Target attribute: " + attributes.getTargetAttribute().getName() + "\n");

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void selectValidationSet(){
        int validationSetSize = (int)(DtreeConstants.VALIDATION_SET_FACTOR * instances.size());
        int datasetSize = instances.size();
        validationSet = new ArrayList<ArrayList<String>>();

        for(int i = 0; i < validationSetSize; i++) {
            int randInstance = (int) (Math.random() * instances.size());
            validationSet.add(instances.remove(randInstance));
        }

        System.out.println("\nSelected " + validationSet.size() +" of "
                + datasetSize +" examples for validation set");
    }



    public ArrayList<ArrayList<String>> getInstances() {
        return instances;
    }

    public void setInstances(ArrayList<ArrayList<String>> instances) {
        this.instances = instances;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public Attributes getOtherAttributes() {
        return attributes.getOtherAttributes();
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Attribute getTargetAttribute(){
        return attributes.getTargetAttribute();
    }

    public ArrayList<ArrayList<String>> getValidationSet() {
        return validationSet;
    }

    public void setValidationSet(ArrayList<ArrayList<String>> validationSet) {
        this.validationSet = validationSet;
    }

    public ArrayList<ArrayList<String>> getTestSet() {
        return testSet;
    }

    public void setTestSet(ArrayList<ArrayList<String>> testSet) {
        this.testSet = testSet;
    }


    /**
     * This method is used to convert continuous-valued attributes to discrete attributes.
     * It first sorts the data by the continuous attribute, finds a list of thresholds,
     * and finds the best threshold using information gain.
     */
    public void processContinuousAttrs(){
        DataSorter sorter = new DataSorter();
        ArrayList<Attribute> attributeList = attributes.getAttributes();

        for(Attribute attribute : attributeList) {
            Attributes thresholdAttr = new Attributes();
            if(attribute.getType() == AttributeType.CONTINUOUS) {
                int attributeIndex = attribute.getIndex(); //specify the column index to sort by
                sorter.setSortingIndex(attributeIndex);
                Collections.sort(instances, sorter);
                ArrayList<Double> thresholds = getContinuousThresholds(attributeIndex); //get a list of thresholds from data
                String attrName = attribute.getName();

                //treat each threshold as an attribute to compute information gain
                for (Double threshold : thresholds) {
                    String attrVals[] = {threshold.toString()};
                    Attribute attr = new Attribute(attrName, AttributeType.CONTINUOUS, attributeIndex, attrVals);
                    thresholdAttr.add(attr);
                }

                //use information gain to determine the best threshold (attribute)
                Attribute bestThreshold = Compute.getBestAttribute(instances, attributes.getTargetAttribute(), thresholdAttr);

                //update attribute label (for example, from temperature to temperature>54)
                Double thresholdVal = 0.0;
                String newAttrName = attrName;
                if(bestThreshold!= null && bestThreshold.getValues().length > 0) {
                    String thresholdStr = bestThreshold.getValues()[0];
                    thresholdVal = new Double(thresholdStr);
                    newAttrName += ">" + thresholdStr;
                }

                //convert continuous attribute to boolean
                attribute.setName(newAttrName);
                attribute.setType(AttributeType.STRING);
                attribute.setValues(new String[]{"T", "F"});

                //update this continuous attribute for all training examples to boolean
                for(ArrayList<String> example : instances){
                    String field = example.get(attributeIndex);
                    Double fieldVal = new Double(field);
                    if(fieldVal.compareTo(thresholdVal) > 0)
                        example.set(attributeIndex, "T");
                    else
                        example.set(attributeIndex, "F");

                }

                //update this continuous attribute for all test examples to boolean
                for(ArrayList<String> example : testSet){
                    String field = example.get(attributeIndex);
                    Double fieldVal = new Double(field);
                    if(fieldVal.compareTo(thresholdVal) > 0)
                        example.set(attributeIndex, "T");
                    else
                        example.set(attributeIndex, "F");

                }

            }
        }

    }

    /**
     * This method goes though the dataset (dataset should be sorted),
     * and computes a list of thresholds.
     * To do this, it identifies adjacent examples that differ in their target classification
     * and generates a set of candidate thresholds midway between the corresponding values
     * of the attribute.
     * @param columnIndex an integer identifying the column or attribute
     * @return an Arraylist of thresholds (double)
     */
    public ArrayList<Double> getContinuousThresholds(int columnIndex) {
        ArrayList<Double> thresholds = new ArrayList<Double>();
        int targetIndex = attributes.getTargetIndex();
        for (int i = 1; i < instances.size(); i++) {
            Double firstValue = new Double(instances.get(i - 1).get(columnIndex));
            Double nextValue = new Double(instances.get(i).get(columnIndex));

            String firstTarget = instances.get(i - 1).get(targetIndex);
            String nextTarget = instances.get(i).get(targetIndex);

            if (firstValue.compareTo(nextValue) != 0 && !firstTarget.equals(nextTarget)) {
                thresholds.add((firstValue + nextValue) / 2);
//                System.out.println(firstValue + "/" + nextValue + " = " + (firstValue + nextValue)/2);
            }
        }

        return thresholds;
    }



    /**
     * This method prints each attribute along with its possible values
     */
    public void printAttributes(){
        for(Attribute attribute: attributes.getAttributes()){
            int count=0;
            String values[] = new String[attribute.getValues().length];
            for(String value: attribute.getValues())
                values[count++] = value;
            System.out.print(attribute.getName() + " = {" + String.join(", ", values) +"}");
            System.out.print("\n");
        }
    }

    /**
     * This method outputs a tabular view of the examples with a table header
     */
    public void printTrainingData(){
        if(instances != null && attributes != null){
            int maxLen = Util.getMaxStringLength(attributes) + 2;
            String hline = new String(new char[maxLen * attributes.size()]).replace("\0", "_");
            System.out.println(hline);

            for(Attribute attribute : attributes.getAttributes()) {
                String padding="";
                String colLabel = attribute.getName();
                if(maxLen > colLabel.length())
                    padding = new String(new char[maxLen - colLabel.length()]).replace("\0", " ");
                System.out.print(colLabel + padding);
            }
            hline = new String(new char[maxLen * attributes.size()]).replace("\0", "=");
            System.out.println("\n" + hline);
            for (ArrayList<String> row : instances) {
                for (int j = 0; j < attributes.size(); j++) {
                    String padding="";
                    String dataVal = row.get(j);
                    if(maxLen > dataVal.length())
                        padding = new String(new char[maxLen - dataVal.length()]).replace("\0", " ");

                    System.out.print( dataVal+ padding);
                }
                System.out.print("\n");
            }
            hline = new String(new char[maxLen * attributes.size()]).replace("\0", "-");
            System.out.print(hline +"\n");

        }

    }
}
