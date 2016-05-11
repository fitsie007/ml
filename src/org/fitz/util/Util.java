package org.fitz.util;

import org.fitz.ml.Attribute;
import org.fitz.ml.Attributes;

/** This class performs auxiliary functions
 * Created by FitzRoi on 2/13/16.
 */
public class Util {

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public static boolean isContinuous(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?") || str.equalsIgnoreCase("continuous");
    }

    public static String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    /**
     * This method returns the length of the longest string in attributes or
     * possible attribute values to aid in pretty printing
     * @param attributes
     * @return
     */
    public static int getMaxStringLength(Attributes attributes){
        int max = 0;
        for(Attribute attribute : attributes.getAttributes()){
            if(attribute.getName().length() > max)
                max = attribute.getName().length();
            for(String value : attribute.getValues())
                if(value.length() > max)
                    max = value.length();
        }
        return max;
    }

    public static boolean isBoolean(String str)
    {
        return str.equalsIgnoreCase("t") || str.equalsIgnoreCase("f") || str.equalsIgnoreCase("true") || str.equalsIgnoreCase("false");
    }

    public static boolean boolVal(String str){
        return str.equalsIgnoreCase("true") || str.equalsIgnoreCase("t");
    }


    public static String oneOfN(int i, int n){
        String oneOfNstr = Integer.toString(i,2); //base 2 of an integer
        if(oneOfNstr.length() < n){
            String padding = new String(new char[n - oneOfNstr.length()]).replace("\0", "0");
            oneOfNstr = padding + oneOfNstr;

        }
        return oneOfNstr;
    }

    public static String getOneOfN(String val, Attribute attribute){
        String attributeVals[] = attribute.getValues();
        int n = attributeVals.length;
        for(int i = 0; i < n; i++)
            if(val.equals(attributeVals[i])) //if the value matches the attribute value
                return oneOfN(i+1, n);
        return "";
    }

    public static String revertOneOfN(Attribute attribute, String inputOneOfN){
        String values[] = attribute.getValues();
        int n = values.length;
        for(int i = 0; i < n; i++){
            if(oneOfN(i+1, n).equals(inputOneOfN))
                return values[i];
        }
        return "";

    }
}

