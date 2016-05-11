package org.fitz.ml.ga;

/**
 * This method provides functionality for a rule statement
 * Created by FitzRoi on 2/23/16.
 */
public class Statement {
    private String attributeName;
    private String value;

    public Statement(String attributeName, String value){
        this.attributeName = attributeName;
        this.value = value;
    }



    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
