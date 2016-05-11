package org.fitz.ml.ga;

import org.fitz.ml.Attribute;
import org.fitz.ml.Attributes;
import org.fitz.ml.constants.GAconstants;
import org.fitz.util.Util;

import java.util.ArrayList;

/**This class provides functionality for a rule
 * Created by FitzRoi on 2/23/16.
 */
public class Rule {
    private ArrayList<Precondition> preconditions;
    private String postcondition;
    private String ruleBitString = null;

    public Rule(){
        this.preconditions = new ArrayList<Precondition>();
        this.postcondition = "";
    }


    public ArrayList<Precondition> getPreconditions() {
        return preconditions;
    }

    public void setPreconditions(ArrayList<Precondition> preconditions) {
        this.preconditions = preconditions;
    }

    public String getPostcondition() {
        return postcondition;
    }

    public void setPostcondition(String postcondition) {
        this.postcondition = postcondition;
    }
    public void addPrecondition(Precondition precondition){
        preconditions.add(precondition);
    }

    public String getRuleBitString() {
        return ruleBitString;
    }

    public void setRuleBitString(String ruleBitString) {
        this.ruleBitString = ruleBitString;
    }


    /**
     * This method creates a rule with attribute names and values from a bit string
     * @param bitStr the bit string from which the rule is to be created
     * @param attributes the attributes to use to create rule pre and post conditions
     * @return a rule
     */
    public Rule getRuleFromString(String bitStr, Attributes attributes){
        Attribute targetAttribute = attributes.getTargetAttribute();
            int index;
            int end = 0;
            Rule rule = new Rule();
            for (int j = 0; j < attributes.getAttributes().size(); j++) {
                Attribute attribute = attributes.get(j);
                int bitStrSize;
                if (j == targetAttribute.getIndex())
                    bitStrSize = targetAttribute.getValues().length - 1; //for example use 0/1 for yes/no and exclude 11
                else
                    bitStrSize = attribute.getValues().length;

                index = end;
                end = index + bitStrSize;
                String attrBitStr = bitStr.substring(index, end);
                if (j == targetAttribute.getIndex())
                    rule.setPostcondition(createPostCondition(attrBitStr, attribute));
                else {
                    Precondition precondition = createPrecondition(Precondition.conditionType.OR, attrBitStr, attribute);
                    if (precondition != null)
                        rule.addPrecondition(precondition);
                }
            }
            //do not use rules with undetermined postcondition or no preconditions
            if (rule.getPostcondition() != null && rule.getPreconditions().size() > 0) {
                rule.setRuleBitString(bitStr);
                return rule;
            }
        return null;
    }


    /**
     * This method creates a precondition from a bit string
     * @param type the type of precondition (OR or AND)
     * @param bitStr the input bit string
     * @param attribute the attribute to use to create a statement of the for x = a
     * @return a precondition of the form (x = a OR x = b) or (x=b AND x=c)
     */
    public Precondition createPrecondition(Precondition.conditionType type, String bitStr, Attribute attribute){
        Precondition precondition = new Precondition(type);
        String attrValues[] = attribute.getValues();
        for(int i = 0; i < bitStr.length(); i++){
            if(bitStr.charAt(i) == '1'){
                Statement statement = new Statement(attribute.getName(), attrValues[i]);
                precondition.addCondition(statement);
            }
        }
        if(precondition.getConditions().size() == 0)
            return null;
        return precondition;
    }

    /**
     * This method creates a postcondition from a bit string using 1 of n notation
     * for example: (0 = yes, 1 =no)
     * @param bitStr the input bit string
     * @param attribute the attribute to use to create postcondition
     * @return a postcondition that matches the input bit string
     */
    public String createPostCondition(String bitStr, Attribute attribute) {
        String attrValues[] = attribute.getValues();
        int n = attrValues.length;
        for (int i = 0; i < n; i++) {
            String attrBitStr = Util.oneOfN(i, n - 1);
            if (attrBitStr.equals(bitStr))
                return attrValues[i];
        }

        return null;
    }

    /**
     * This method calculates the accuracy of a rule
     * @param dataset the dataset on which to test accuracy
     * @param attributes the attributes to use to map labels to rule
     * @return the accuracy of a rule on a dataset
     */
    public Double getRuleAccuracy(ArrayList<ArrayList<String>> dataset, Attributes attributes) {
        Double totalExamples = (double) dataset.size();
        Double totalCorrect = 0.0;

        for (ArrayList<String> instance : dataset) { //for each instance in dataset
            boolean isCorrect;
            isCorrect = isPredictedCorrectly(instance, attributes); //try to match rule to instance
            if (isCorrect) {
                totalCorrect++;
            }
        }

        if (totalCorrect == 0)
            return totalCorrect;
        else
            return (totalCorrect / totalExamples) * 100.0;
    }

    /**
     * This method checks if a rule matches an instance so it can classify it
     * @param instance the instance to match with the rule
     * @param attributes the attributes to use as rule labels
     * @return true if an instance is matched with a rule, false otherwise
     */
    public boolean isMatched(ArrayList<String> instance, Attributes attributes){
        //make sure all preconditions in rule match
        for(Precondition precondition : preconditions){
            if(!precondition.isMatched(instance, attributes))
                return false;
        }
        return true;
    }

    /**
     * This method checks if this rule object correctly classifies an instance
     * @param instance the example to check
     * @param attributes the attributes to use for comparison
     * @return
     */
    public boolean isPredictedCorrectly(ArrayList<String> instance, Attributes attributes){
        //make sure all preconditions in rule match
        for(Precondition precondition : preconditions){
            if(!precondition.isMatched(instance, attributes))
                return false;
        }
        //then make sure postcondition matches
        int targetIndex = attributes.getTargetAttribute().getIndex();
        return postcondition.equalsIgnoreCase(instance.get(targetIndex));

    }

    /**
     * This method prints a rule in the form precondition1 ^ precondtion2 => consequent
     *
     */
    public void printRule() {
        String ruleStr = "";
        int n = preconditions.size();
        for (int i = 0; i < n; i++) {
            Precondition precondition = preconditions.get(i);
            if (i == n - 1)
                ruleStr += precondition.toString();
            else
                ruleStr += precondition.toString() + GAconstants.AND_DELIMETER;
        }
        ruleStr += " => " + postcondition;

//        System.out.print(ruleBitString + " --> " + ruleStr + "\n");
        System.out.print(ruleStr + "\n");
    }


    /**
     * This method partitions a rule bit string based on attributes and prints
     * it with a labeled heading from the attribute names
     * @param attributes the attributes to use to divide the rule bit string
     */
    public void printDividedRuleStr(Attributes attributes) {
        Attribute targetAttribute = attributes.getTargetAttribute();
        String bitStr = "";
        String attributeLabels = "";
        int paddingLen = 0;
        for (int j = 0; j < attributes.getAttributes().size(); j++) {
            Attribute attribute = attributes.get(j);
            String attrName = attribute.getName();

            attributeLabels += attrName + "\t";
            if (attrName.length() > paddingLen)
                paddingLen = attrName.length();
        }

        int index;
        int end = 0;

        for (int j = 0; j < attributes.getAttributes().size(); j++) {
            Attribute attribute = attributes.get(j);
            int bitStrSize;
            if (j == targetAttribute.getIndex())
                bitStrSize = targetAttribute.getValues().length - 1;
            else
                bitStrSize = attribute.getValues().length;

            index = end;
            end = index + bitStrSize;
            String attrStr = ruleBitString.substring(index, end);

            String padding = new String(new char[paddingLen - attrStr.length()]).replace("\0", " ");
            bitStr += attrStr + padding;
        }
        bitStr += "\n";


        System.out.println(attributeLabels);
        System.out.println(bitStr);
    }

}
