package org.fitz.ml;

import java.util.ArrayList;

/** This class provides functionalities for attributes
 * Created by FitzRoi on 2/19/16.
 */
public class Attributes {
    private ArrayList<Attribute> attributes;
    private int targetAttribute;

    public Attributes(){attributes = new ArrayList<Attribute>();}

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttribute(int index, Attribute attribute){
        if(attributes.size() >= index)
            attributes.set(index, attribute);
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Attribute getTargetAttribute() {
        if(attributes.size() >= targetAttribute)
        return attributes.get(targetAttribute);
        else
            return null;
    }

    public void setTargetAttribute(int targetAttribute) {
        this.targetAttribute = targetAttribute;
    }

    /**
     * This function returns a collection of attributes except the target attribute
     * @return attributes except target
     */
    public Attributes getOtherAttributes() {
        Attributes otherAttributes = new Attributes();
        ArrayList<Attribute> attrExceptClass = new ArrayList<Attribute>();
        for(int i = 0; i < attributes.size() - 1; i++)
            if(i != targetAttribute)
            otherAttributes.add(attributes.get(i));
        return otherAttributes;
    }

    public int size(){
        return attributes.size();
    }

    public Attribute get(int index){
        if(attributes.size() >= index)
            return attributes.get(index);
        else
            return null;
    }

    public void add(Attribute attribute){
        attributes.add(attribute);
    }

    public int getTargetIndex(){
        return targetAttribute;
    }
}
