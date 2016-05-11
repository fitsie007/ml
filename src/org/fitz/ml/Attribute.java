package org.fitz.ml;

/**
 * Created by FitzRoi on 2/13/16.
 */
public class Attribute {
    private String values[];
    private String name;
    private AttributeType type; //example Boolean, Continuous, String
    private int index;

    public Attribute(String name, AttributeType type, int index, String values[]){
        this.name = name;
        this.values = values;
        this.type = type;
        this.index = index;
    }

    public Attribute(){}

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttributeType getType() {
        return type;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
