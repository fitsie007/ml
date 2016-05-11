package org.fitz.ml.ga;

import org.fitz.ml.Attribute;
import org.fitz.ml.Attributes;
import java.util.ArrayList;

/**
 * This class provides methods and properties for an hypothesis
 * Created by FitzRoi on 3/29/16.
 */
public class Hypothesis {
    private ArrayList<Rule> ruleset;
    private Attributes attributes;
    private Attribute targetAttribute;
    private double fitness = 0; // the fitness of this hypothesis
    private double pr = 0; //the probability used for probabilistic selection

    public Hypothesis(ArrayList<Rule> ruleset, Attributes attributes){
        this.ruleset = ruleset;
        this.attributes = attributes;
        this.targetAttribute = attributes.getTargetAttribute();
    }

    public ArrayList<Rule> getRuleset() {
        return ruleset;
    }

    public void setRuleset(ArrayList<Rule> ruleset) {
        this.ruleset = ruleset;
    }

    /**
     * This method loops through the ruleset and generates a bitstring
     * @return the bitstring representation for this hypothesis
     */
    public String getBitString(){
        String bitStr ="";
        for(Rule rule : ruleset)
            bitStr += rule.getRuleBitString();
        return bitStr;
    }

    public int size(){return ruleset.size();}

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Attribute getTargetAttribute() {
        return targetAttribute;
    }

    public void setTargetAttribute(Attribute targetAttribute) {
        this.targetAttribute = targetAttribute;
    }

    public double getPr() {
        return pr;
    }

    public void setPr(double pr) {
        this.pr = pr;
    }

    public double computeFitness(ArrayList<ArrayList<String>> instances){
        fitness = Math.pow(correct(instances), 2);
        return fitness;
    }

    /**
     * This method checks the number of instances classified correctly by this hypothesis
     * @param instances examples
     * @return the percentage of instances classified correctly
     */
    public  double correct(ArrayList<ArrayList<String>> instances){
        double instanceCount = instances.size();
        double totalCorrect = 0;
        String postCondition ="";

        for(ArrayList<String> example : instances) {
            boolean matched = false;
            for (Rule rule : ruleset) {
                if (rule.isMatched(example, attributes)) {
                    matched = true;
                    postCondition = rule.getPostcondition();
                    //rule.printRule();
//                    printInstance(example);
                    break; //break if this rule can classify this instance

                }
            }

            //if the postcondition is matched, then increment totalCorrect
            if(matched) {
                int targetIndex = attributes.getTargetAttribute().getIndex();
                if(postCondition.equalsIgnoreCase(example.get(targetIndex)))
                    totalCorrect++;
            }
        }

        if(totalCorrect == 0)
            return 0;
        else
            return (totalCorrect/instanceCount) * 100.0;
    }

    public void printInstance(ArrayList<String> example){
        for(String attribute : example){
            System.out.print(attribute + "\t");
        }
        System.out.print("\n");
    }

    /**
     * This method prints the ruleset in divided form to highlight the attributes
     * with the attributes as a table header
     */
    public void printHypothesisBitStr(){
        String bitStrs = "";
        String attributeLabels ="";
        int paddingLen = 0;
        for(int j = 0; j < attributes.getAttributes().size(); j++){
            Attribute attribute = attributes.get(j);
            String attrName = attribute.getName();

            attributeLabels +=  attrName+"\t";
            if(attrName.length() > paddingLen)
                paddingLen = attrName.length();
        }

        for (Rule rule : ruleset) {
            int index;
            int end = 0;
            String ruleBitStr = rule.getRuleBitString();
            for (int j = 0; j < attributes.getAttributes().size(); j++) {
                Attribute attribute = attributes.get(j);
                int bitStrSize;
                if (j == targetAttribute.getIndex())
                    bitStrSize = targetAttribute.getValues().length - 1;
                else
                    bitStrSize = attribute.getValues().length;

                index = end;
                end = index + bitStrSize;
                String attrStr = ruleBitStr.substring(index, end);

                String padding = new String(new char[paddingLen - attrStr.length()]).replace("\0", " ");
                bitStrs += attrStr + padding;
            }
            bitStrs += "\n";
        }

        System.out.println(attributeLabels);
        System.out.println(bitStrs);
    }
}
