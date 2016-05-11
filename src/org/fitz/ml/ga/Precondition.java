package org.fitz.ml.ga;

import org.fitz.ml.Attribute;
import org.fitz.ml.Attributes;
import org.fitz.ml.constants.GAconstants;

import java.util.ArrayList;

/**
 * This method provides functionalities for a precondition in a rule
 * which could be disjunctive or conjunctive
 * Created by FitzRoi on 3/29/16.
 */
public class Precondition {
    //specify if this precondtion is a conjunctive or disjunctive type
    public enum conditionType{OR, AND}
    private ArrayList<Statement> conditions;
    private conditionType type;

    public Precondition(conditionType type){
        this.conditions = new ArrayList<Statement>();
        this.type = type;
    }

    public ArrayList<Statement> getConditions() {
        return conditions;
    }

    /**
     * This method adds a condition to a list of preconditions
     * @param statement the statement of the form x = y
     */
    public void addCondition(Statement statement){
        conditions.add(statement);
    }

    public boolean isMatched(ArrayList<String> instance, Attributes attributes) {
        if(type == conditionType.AND){
            return andMatched(instance, attributes);
        }

        if(type == conditionType.OR){
            return orMatched(instance, attributes);
        }

       return false;
    }


    /**
     * This metod checks if at least one value matched in a rule precondition
     * @param instance the example to compare with condition
     * @param attributes attributes to use for getting a value
     * @return true if instance matched by precondition, false otherwise
     */
    public boolean orMatched(ArrayList<String> instance, Attributes attributes){

        for (Statement statement : conditions) {
            int columnIndex = getAttributeIndex(statement, attributes);
            //make sure attributes map to instances

            if (columnIndex >= 0 && instance.size() >= columnIndex) {
                String attrValue = instance.get(columnIndex);

                //Stop if the attribute from the instance does not equal the rule statement value
                if (attrValue.equalsIgnoreCase(statement.getValue()))
                    return true;
            }
        }
        return false;
    }

    /**
     * This method checks if all statements in conditions matched in an example
     * @param instance the example to check for match
     * @param attributes attributes to use for getting a value
     * @return true if all statements match; false otherwise
     */
    public boolean andMatched(ArrayList<String> instance, Attributes attributes){

        for (Statement statement : conditions) {
            int columnIndex = getAttributeIndex(statement, attributes);
            //make sure attributes map to instances

            if (columnIndex >= 0 && instance.size() >= columnIndex) {
                String attrValue = instance.get(columnIndex);

                //Stop if the attribute from the instance does not equal the rule statement value
                if (!attrValue.equalsIgnoreCase(statement.getValue()))
                    return false;
            }
        }
        return true;
    }

    /**
     * This method finds the index of an attribute in a rule
     * @param statement the precondition of a rule
     * @param attributes the set of attributes to determine rule labels
     * @return the index of an attribute in a rule
     */
    private int getAttributeIndex(Statement statement, Attributes attributes){
        int index = -1;

        for(Attribute attribute : attributes.getAttributes()){
            if(attribute.getName().equals(statement.getAttributeName()))
                return attributes.getAttributes().indexOf(attribute);
        }
        return index;
    }

    /**
     * This method converts a precondition to a string
     * @return precondition as string
     */
    public String toString(){
        String out ="";
        int n = conditions.size();
        for(int i = 0; i < n; i++){
            String attribute = conditions.get(i).getAttributeName();
            String value = conditions.get(i).getValue();
            String delimeter="";
            if(type == conditionType.AND)
                delimeter = GAconstants.AND_DELIMETER;
            if(type == conditionType.OR)
                delimeter = GAconstants.OR_DELIMETER;

            if(i == n - 1) out += attribute +" = " + value;
            else out += attribute +" = " + value  + delimeter;

        }

        return out;
    }

}
