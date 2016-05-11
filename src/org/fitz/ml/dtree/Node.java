package org.fitz.ml.dtree;

import org.fitz.ml.Attribute;

import java.util.ArrayList;

/** This class provides capabilities for a node in the
 * decision tree.
 * Created by FitzRoi on 2/13/16.
 */
public class Node {
    private Attribute attribute = null;
    private ArrayList<Node> children = null;
    private String label = null;
    private String parentLabel = null;
    private ArrayList<Branch> branches = null;

    public Node(){
        this.children = new ArrayList<Node>();
        this.branches = new ArrayList<Branch>();
    }

    public Node(String label, String parentLabel){
        this.children = new ArrayList<Node>();
        this.branches = new ArrayList<Branch>();
        this.parentLabel = parentLabel;
        this.label = label;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getParentLabel() {
        return parentLabel;
    }

    public void setParentLabel(String parentLabel) {
        this.parentLabel = parentLabel;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void addBranch(Branch branch){
        this.branches.add(branch);
    }

    public ArrayList<Branch> getBranches() {
        return branches;
    }

    public boolean isLeaf(){
       return this.branches.size() == 0 && this.children.size() == 0;
    }


}
