package org.fitz.util;

import org.fitz.ml.Attribute;
import org.fitz.ml.AttributeType;
import org.fitz.ml.Attributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** This class is used to perform computations such as entropy and information gain
 * Created by FitzRoi on 2/19/16.
 */
public class Compute {
    /**
     * This method computes the entropy of a set of examples
     * @param S the set of instances
     * @param targetAttribute the target concept
     * @return the entropy
     */
    public static Double entropy(ArrayList<ArrayList<String>> S, Attribute targetAttribute) {
        Double sum = 0.0;
        Double sSize = (double) S.size();

        if(S.size() > 0) {
            for (String ci : targetAttribute.getValues()) {
                Double pi;  //proportion of S belonging to class i
                Double count = 0.0;
                for (ArrayList<String> example : S) {
                    if (example.get(targetAttribute.getIndex()).equals(ci)) { //row[i].attribute[attrIndex]
                        count++;
                    }
                }
                pi = count / sSize;
                if (pi > 0)
                    sum += (-(pi * (Math.log(pi) / Math.log(2)))); //log2(n) = log(n)/log(2)
            }
        }

        return sum;
    }

    /**
     * This method computes the information gain --> gain(S,A)
     * where S is a set of examples
     * and A is the attribute. We also pass in target attribute
     * for the entropy function, which is also used to determine the best threshold
     * to use for continuous attributes
     * formula Gain(S,A) Entropy(S) - ∑             |Sv|/   * Entropy(Sv)
     *                               v in Values(A) |S|
     * @param S a set of examples
     * @param A the attribute in question
     * @param targetAttribute the target concept
     * @return the information gain
     */
    public static Double gain(ArrayList<ArrayList<String>> S, Attribute A, Attribute targetAttribute) {
        Double runningSum = 0.0;
        ArrayList<ArrayList<String>> sv;
        String v[] = A.getValues();
        for (String vi : v) {
            sv = Sv(S, vi, A.getIndex(), A.getType());
            Double svDivS = ((double) sv.size() / (double) S.size()); // |sv| / |s|
            runningSum += svDivS * entropy(sv, targetAttribute);
        }
        return entropy(S, targetAttribute) - runningSum;

    }

    /**
     * This method returns a list of examples with a certain value for a target attribute
     * @param S the set of examples
     * @param v the value for the attribute
     * @param attrIndex the index of the attribute, which is also the column of the dataset
     * @param attrType the type of the attribute (example: boolean, continuous, etc)
     * @return a list of examples with a certain target matching a certain value
     */
    public static ArrayList<ArrayList<String>> Sv(ArrayList<ArrayList<String>> S, String v, int attrIndex, AttributeType attrType) {
        ArrayList<ArrayList<String>> sv = new ArrayList<ArrayList<String>>();
        for (ArrayList<String> row : S) {
            String attr = row.get(attrIndex);
            if (attrType == AttributeType.CONTINUOUS) {
                if(new Double(attr).compareTo(new Double(v)) < 0 ) //A < c
                    sv.add(row);
            }
            else { //both boolean and string can use equals
                if (attr.equals(v)) {
                    sv.add(row);
                }
            }
        }

        return sv;
    }

    /**
     * This method finds the "best" attribute in dataset based on information gain
     * (i.e the attribute with the highest information gain)
     * @param examples a set of examples
     * @param targetAttribute the target concept
     * @param otherAttributes all other attributes except the target
     * @return the attribute with the highest information gain from a list of attributes
     */

    public static Attribute getBestAttribute(ArrayList<ArrayList<String>> examples, Attribute targetAttribute, Attributes otherAttributes){
        Attribute bestAttr = new Attribute();
        Double maxGain = 0.0;
        //create a hashmap of attributes and compute gain for all attributes
        Map<Attribute, Double> attrGains = new HashMap<Attribute, Double>();
        for (Attribute attribute : otherAttributes.getAttributes()) {
            Double gain = gain(examples, attribute, targetAttribute);
//            System.out.println("gain of " +attribute.getName() + ": " + gain);
            attrGains.put(attribute, gain);
        }

        //find max gain in hashmap
        if(attrGains.values().size() > 0) {
            maxGain = Collections.max(attrGains.values());
            if (maxGain == 0 && otherAttributes.size() > 1)
                return null;
        }
        else
            return null;

        //find maximum key in hashmap
        for (Map.Entry<Attribute, Double> entry : attrGains.entrySet()) {
            Double value = entry.getValue();

            if (value != null && maxGain.equals(value)) {
                bestAttr = entry.getKey();
            }
        }

        return bestAttr;
    }

}
