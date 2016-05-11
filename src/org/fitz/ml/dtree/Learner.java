package org.fitz.ml.dtree;

import org.fitz.ml.Attribute;
import org.fitz.ml.AttributeType;
import org.fitz.ml.Attributes;
import org.fitz.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** This class provides functionalities for running the ID3 algorithm
 * Created by FitzRoi on 2/13/16.
 */
public class Learner {
    private Tree dTree;
    private Attributes attributes;

    public Learner(){
        dTree = new Tree();
    }
    public Tree getTree(){return dTree;}

    public void setAttributes(Attributes attributes){this.attributes = attributes;}

    public void setTargetAttribute(int targetAttribute) {
        this.attributes.setTargetAttribute(targetAttribute);
    }


    /**
     * This method runs the recursive ID3 algorithm
     * @param examples dataset
     * @param targetAttribute the target attribute
     * @param otherAttributes attributes except the target concept
     * @return a root node to a tree or subtree
     */
    public Node ID3(ArrayList<ArrayList<String>> examples, Attribute targetAttribute, Attributes otherAttributes) {
        Node root = new Node();

        //check if all examples belong to the same class (positive, negative, etc)
        if (sameClass(examples, targetAttribute)) {
            ArrayList<String> firstExample = examples.get(0);
            int targetIndex = attributes.getTargetIndex();

            if (targetIndex >= 0) {
                String nodeLabel = firstExample.get(targetIndex); //use the class of the first example as label
                root.setLabel(nodeLabel);
            }
            return root;
        }

        //if attributes empty
        if (attributes.size() == 0) {
            String nodeLabel = getMostCommonValue(examples, targetAttribute);
            root.setLabel(nodeLabel);
            return root;
        }

        //use information gain to determine best attribute from a list of attributes
        Attribute A = Compute.getBestAttribute(examples, targetAttribute, otherAttributes);

        //information gain = 0, go no further
        if (A == null) {
            String nodeLabel = getMostCommonValue(examples, targetAttribute);
            root.setLabel(nodeLabel);
            return root;
        }

        root.setLabel(A.getName()); //label root with decision attribute
        root.setAttribute(A); //set the decision attribute for root

        for (String vi : A.getValues()) { //for each possible vi of A
            Branch branch = new Branch(vi);
            root.addBranch(branch);

            //get examples where the attribute A is equal to vi
            ArrayList<ArrayList<String>> examplesVi = getExamplesVi(examples, A, vi);

            if (examplesVi.size() == 0) {
                String label = getMostCommonValue(examples, targetAttribute);
                branch.addChild(new Node(label, branch.getLabel())); // add a leaf node with most common target value
            } else {
                Attributes attributesMinusA = removeAttribute(otherAttributes, A);
                Node subTree = ID3(examplesVi, targetAttribute, attributesMinusA);
                subTree.setParentLabel(branch.getLabel());

                if (subTree != null) {
                    branch.addChild(subTree);
                }
            }

        }

        return root;
    }

    /**
     * This method checks if a set of examples belong to the same class
     * @param S a set of examples
     * @param A attribute
     * @return true if all examples belong to the same class, false otherwise
     */

    public boolean sameClass(ArrayList<ArrayList<String>> S,  Attribute A){
        AttributeType attrType = A.getType();

        for (ArrayList<String> example : S) {
            String attr = example.get(A.getIndex());
            if (attrType == AttributeType.CONTINUOUS) {

            } else {
                if (!attr.equals(A.getValues()[0])) { //both Strings are not equal
//                    System.out.println(attr.toString() +"! = " + A.getValues()[0]);
                    return false;
                }
            }

        }

        return true;
    }

    /**
     * This method gets the most common values in the dataset based on the target concept
     * @param examples a set of examples
     * @param targetAttribute the target attribute
     * @return a target value as String
     */
    public String getMostCommonValue(ArrayList<ArrayList<String>> examples, Attribute targetAttribute) {
        //store possible values in hashmap
        Map<String, Integer> attrVals = new HashMap<String, Integer>();
        for (String attrVal : targetAttribute.getValues()) {
            attrVals.put(attrVal, 0);
        }

        //count all possible target values in examples
        for (ArrayList<String> example : examples) {
            String exampleClass = example.get(example.size() - 1);
            if (attrVals.containsKey(exampleClass)) {
                int count = attrVals.get(exampleClass);
                attrVals.put(exampleClass, count + 1);
            }
        }
        //find max value in hashmap
        Integer maxVal = Collections.max(attrVals.values());
        String maxAttr = "";

        //find maximum key in hashmap
        for (Map.Entry<String, Integer> entry : attrVals.entrySet()) {
            Integer value = entry.getValue();
            if (null != value && maxVal.equals(value)) {
                maxAttr = entry.getKey();
            }
        }

        return maxAttr;
    }


    /**
     * This method returns a subset of examples where an attribute takes on a certain value
     * @param examples set of examples
     * @param A the attribute to check for value
     * @param vi the value of the attribute (String)
     * @return subset of examples
     */
    public ArrayList<ArrayList<String>> getExamplesVi(ArrayList<ArrayList<String>> examples, Attribute A, String vi){
        ArrayList<ArrayList<String>> examplesVi = new ArrayList<ArrayList<String>>();
        int attrIndex = A.getIndex();
        for(ArrayList<String> example : examples){
            if(example.get(attrIndex).equals(vi))
                examplesVi.add(example);
        }

        return examplesVi;
    }

    /**
     * This method excludes a certain attribute from the set of attributes
     * It is used primarily by the ID3 algorithm to return [Attributes - {A}]
     * @param attributes set of attributes
     * @param A attribute to exclude
     * @return subset of attributes
     */
    public Attributes removeAttribute(Attributes attributes, Attribute A){
        Attributes attributesMinusA = new Attributes();
        for(Attribute attribute : attributes.getAttributes())
            if(attribute.getIndex() != A.getIndex())
                attributesMinusA.add(attribute);
        return attributesMinusA;
    }

}
