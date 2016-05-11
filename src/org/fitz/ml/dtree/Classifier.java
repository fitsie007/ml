package org.fitz.ml.dtree;

import org.fitz.ml.Attribute;
import org.fitz.ml.Attributes;
import org.fitz.util.Util;

import java.util.ArrayList;

/** This class facilitates the classification of instances
 * Created by FitzRoi on 2/24/16.
 */
public class Classifier {
    private Attributes attributes;
    private int maxLen;
    private String padding = "";

    public Classifier(Attributes attributes){
        this.attributes = attributes;
        maxLen = Util.getMaxStringLength(attributes) + 2;
    }

    /**
     * This method iterates a dataset and classifies it while printing each instance
     * as well as its classification in tabular form
     * @param instances the dataset to classify
     * @param rules the rules to use to classify the data
     * @param datasetLabel a label specifying the name of the dataset (eg. TEST SET)
     */
    public void classify(ArrayList<ArrayList<String>> instances, ArrayList<Rule> rules, String datasetLabel){

        Double totalExamples = (double) instances.size();
        Double totalCorrect = 0.0;
        int notMatched = 0;
        int targetIndex = attributes.getTargetIndex();
        String hline = new String(new char[(maxLen * ((attributes.size() + 1))-datasetLabel.length())/2]).replace("\0", "=");
        String classification="-----";
        String totalClassStr = "TOTAL CLASSIFIED: ";
        String accuracyStr = "ACCURACY (%):   ";
        int lineLen;
        System.out.print("\n\n" +hline);
        System.out.print(datasetLabel);
        System.out.print(hline +"\n");

        printClassificationHeader(attributes); //print a table header

        for (ArrayList<String> instance : instances) {
            boolean isRuleMatched = false;
            for (Rule rule : rules) {
                isRuleMatched = rule.isMatched(instance, attributes);
                if (isRuleMatched) {
                    classification = rule.getConsequent();
                    if(instance.get(targetIndex).equalsIgnoreCase(classification))
                        totalCorrect++;
                    printInstance(instance, maxLen);
                    if (maxLen > classification.length())
                        padding = new String(new char[maxLen - classification.length()]).replace("\0", " ");
                    System.out.print(classification + padding +"\n");
                    break;
                }
            }
            if(!isRuleMatched) {
                classification="-----";
                printInstance(instance, maxLen);
                if (maxLen > classification.length())
                    padding = new String(new char[maxLen - classification.length()]).replace("\0", " ");
                System.out.print(classification + padding +"\n");
                notMatched++;
            }
        }

        //print accuracy and formatting data

        hline = new String(new char[maxLen * (attributes.size() + 1)]).replace("\0", "_");
        System.out.println(hline);

        System.out.print(totalClassStr);
        lineLen = maxLen * attributes.size() - totalClassStr.length();
        System.out.print(new String(new char[lineLen]).replace("\0", " "));
        System.out.print(totalCorrect +"/" +totalExamples);

        lineLen = maxLen * attributes.size() - accuracyStr.length();
        System.out.print("\n" + accuracyStr);
        System.out.print(new String(new char[lineLen]).replace("\0", " "));

        System.out.print(String.format("%.2f", (totalCorrect/totalExamples)*100.0)+"%\n");
        if(notMatched > 0)
            System.out.print("*Could not apply any rule to " +notMatched +" instance(s)\n");
        System.out.print(hline);


    }

    /**
     * This method pretty-prints an instance during classification
     * It follows the table indentation pattern used by the classify method
     * @param instance the instance to print
     * @param maxLen the max length of string to be printed to aid in pretty-printing
     */
    public void printInstance(ArrayList<String> instance, int maxLen){
        System.out.print("\n");

        for (String attribute : instance) {
            if (maxLen > attribute.length())
                padding = new String(new char[maxLen - attribute.length()]).replace("\0", " ");
            System.out.print(attribute + padding);
        }

    }

    /**
     * This method prints a table header that precedes the list of classifications
     * @param attributes the attributes to use to label the columns
     */
    public void printClassificationHeader(Attributes attributes){
        String hline = new String(new char[maxLen * (attributes.size() + 1)]).replace("\0", "/");
        System.out.println(hline);
        String colLabel="";

        for(Attribute attribute : attributes.getAttributes()) {
            colLabel = attribute.getName();
            if(maxLen > colLabel.length())
                padding = new String(new char[maxLen - colLabel.length()]).replace("\0", " ");
            System.out.print(colLabel + padding);
        }
        colLabel ="PREDICTED";
        if(maxLen > colLabel.length())
            padding = new String(new char[maxLen - colLabel.length()]).replace("\0", " ");
        System.out.print(colLabel + padding);

        hline = new String(new char[maxLen * (attributes.size() + 1)]).replace("\0", "~");
        System.out.print("\n" + hline);
    }
}
