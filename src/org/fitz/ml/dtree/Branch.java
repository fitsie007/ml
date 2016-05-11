package org.fitz.ml.dtree;

import java.util.ArrayList;

/** This class provides functionalities for a branch on the decision tree
 * Created by FitzRoi on 2/17/16.
 */
public class Branch {
    private String label;
    private ArrayList<Node> nodes;

    public Branch(){
        nodes = new ArrayList<Node>();
    }

    public Branch(String label){
        this.label = label;
        nodes = new ArrayList<Node>();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public void addChild(Node node){
        this.nodes.add(node);
    }
}
