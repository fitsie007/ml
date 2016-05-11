package org.fitz.ml.dtree;

import org.fitz.ml.Attribute;
import org.fitz.ml.Attributes;

import java.util.ArrayList;

/**This class provides functionality for a rule
 * Created by FitzRoi on 2/23/16.
 */
public class Rule {
    private ArrayList<Statement> antecedent;
    private String consequent;

    public Rule(){
        this.antecedent = new ArrayList<Statement>();
        this.consequent = "";
    }

    public ArrayList<Statement> getAntecedent() {
        return antecedent;
    }

    public void setAntecedent(ArrayList<Statement> antecedent) {
        this.antecedent = antecedent;
    }

    public String getConsequent() {
        return consequent;
    }

    public void setConsequent(String consequent) {
        this.consequent = consequent;
    }

    public void addStatement(Statement statement){
        antecedent.add(statement);
    }

    /**
     * This method checks if a rule matches an instance so it can classify ut
     * @param instance the instance to match with the rule
     * @param attributes the attributes to use as rule labels
     * @return
     */
    public boolean isMatched(ArrayList<String> instance, Attributes attributes){

        for(Statement statement : antecedent){
            int columnIndex = getAttributeIndex(statement, attributes);
            //make sure attributes map to instances
            if(columnIndex >= 0 && instance.size() >= columnIndex ) {
                String attrValue = instance.get(columnIndex);

                //Stop if the attribute from the instance does not equal the rule statement value
                if (!attrValue.equalsIgnoreCase(statement.getValue()))
                    return false;
            }

        }

        int targetIndex = attributes.getTargetAttribute().getIndex();
        return consequent.equals(instance.get(targetIndex));

    }

    /**
     * This method returns the index of an attribute in a rule
     * @param statement the precondition of a rule
     * @param attributes the set of attributes to determine rule labels
     * @return
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
     * This method creates a copy of a rule
     * Used particularly when attempting pruning so we can return to the original rule
     * @return a copy of the rule
     */
    public Rule copy(){
        Rule newRule = new Rule();
        for(Statement statement : antecedent){
            Statement newStatement = new Statement(statement.getAttributeName(), statement.getValue());
            newRule.addStatement(newStatement);
        }
        newRule.setConsequent(consequent);

        return newRule;
    }

    /**
     * This method prints a rule in the form precondition1 ^ precondtion2 => consequent
     */
    public void printRule(){
        for (int i = 0; i < antecedent.size(); i++) {
            System.out.print(antecedent.get(i).getAttributeName() + " = ");
            System.out.print(antecedent.get(i).getValue());
            if (i < antecedent.size() - 1)
                System.out.print(" ^ ");
            else
                System.out.print(" => " + consequent); //⇒
        }
    }

}
