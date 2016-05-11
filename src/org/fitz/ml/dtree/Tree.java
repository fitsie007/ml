package org.fitz.ml.dtree;

import org.fitz.ml.Attributes;
import org.fitz.ml.constants.DtreeConstants;

import java.util.ArrayList;
import java.util.Collections;

/** This function facilitates the creation and processing of a tree
 * Created by FitzRoi on 2/13/16.
 */

public class Tree {
    private Node root = null;
    ArrayList<Rule> rules = new ArrayList<Rule>();


    public Tree() {
        root = new Node();
    }

    public Tree(Node node) {
        root = node;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    /**
     * This method derives rules from all paths in a tree
     */
    public void deriveRules() {
        ArrayList<ArrayList<Node>> paths = new ArrayList<ArrayList<Node>>();
        ArrayList<Node> currentPath = new ArrayList<Node>();

        traversePath(root, currentPath, paths); //recursively traverse tree and save paths in paths arraylist

        //generate rules from paths
        for (ArrayList<Node> path : paths) {
            int pathLength = path.size();
            String attributeName;
            String attributeValue;

            Rule rule = new Rule();
            for (int i = 0; i < path.size(); i++) {

                if (i % 2 == 1) {
                    attributeName = path.get(i - 1).getLabel();
                    attributeValue = path.get(i).getParentLabel();
                    Statement statement = new Statement(attributeName, attributeValue);
                    rule.addStatement(statement);
                }
                if (i == pathLength - 1) {
                    rule.setConsequent(path.get(i).getLabel());
                    rules.add(rule);
                }

            }
        }

    }


    /**
     * This method attempts rule post-pruning on a set of rules
     * @param validationSet the set to use to determine accuracy
     * @param attributes the list of attributes to provide labels for matching a rule
     */
    public void tryPostRulePruning(ArrayList<ArrayList<String>> validationSet, Attributes attributes) {
        Double classificationAccuracy = getAccuracy(validationSet, attributes);
        ArrayList<Double> ruleAccuracies = new ArrayList<Double>();
        ArrayList<Rule> sortedRules = new ArrayList<Rule>();
        System.out.println("\nAccuracy Before Pruning: " + String.format("%.2f", classificationAccuracy) + "%");
        boolean isPruningSuccessful = false;
        for (Rule rule : rules) {
            if (rule.getAntecedent().size() > 0) {
                //check if a pruning a precondtion would increase rule acccuracy
                int preconditionIndex = isRuleWorthPruning(rule, validationSet, attributes);
                if (preconditionIndex != -1) {
                    while (isRuleWorthPruning(rule, validationSet, attributes) !=-1) {
                        pruneRule(rule, preconditionIndex);
                    }
                    isPruningSuccessful = true;
                }
            }
        }
        classificationAccuracy = getAccuracy(validationSet, attributes);

        //get the accuracy of each rule
        for(Rule rule : rules){
            Double ruleAccuracy = getRuleAccuracy(validationSet, attributes, rule);
            ruleAccuracies.add(ruleAccuracy);
        }
        //sort rules by rule accuracy
        while(ruleAccuracies.size() > 0){
            int index=0;
            Double max = Collections.max(ruleAccuracies, null);
            for(Double value: ruleAccuracies)
                if(max.equals(value)) {
                    index = ruleAccuracies.indexOf(value);
                    sortedRules.add(rules.remove(index));
                    break;
                }
            ruleAccuracies.remove(index);
        }


        if (!isPruningSuccessful) {
            if(classificationAccuracy.equals(DtreeConstants.MAXIMUM_ACCURACY))
                System.out.println(">> Note: Accuracy already maximum (no pruning necessary)");
            else
                System.out.println(">> Note: Pruning would not improve accuracy");
        } else {

            System.out.print("\n=============== PRUNED RULES ===================\n");
            printRules();
            System.out.println("\nAccuracy after pruning: " + String.format("%.2f", getAccuracy(validationSet, attributes)) + "%");
        }

    }

    /**
     * This method checks if a rule is worth pruning (ie, if the accuracy increases after pruning)
     * @param rule the rule to check
     * @param validationSet the dataset to use to measure accuracy
     * @param attributes the list of attributes
     * @return index of precondition with highest accuracy if removed
     */
    public int isRuleWorthPruning(Rule rule,
                                      ArrayList<ArrayList<String>> validationSet,
                                      Attributes attributes) {
        int ruleIndex = rules.indexOf(rule);
        Double currentAccuracy = getRuleAccuracy(validationSet, attributes, rule);
        Double newAccuracy =0.0;
        int indexOfHighestAccuracy=-1;

        if (ruleIndex > -1) {
            Rule ruleBeforePrune = rule.copy(); //create a copy of the rule before modifying it
            ArrayList<Statement> antecedent = rule.getAntecedent();
            int ruleSize = antecedent.size();
            //start at last part of rule and move closer to top since top part has highest info gain
            for(int i = antecedent.size()-1; i >= 0; i--){
                antecedent.remove(i); //remove each part of antecedent, one by one

                rules.get(ruleIndex).setAntecedent(antecedent); //update original rules to get new accuracy
                newAccuracy = getRuleAccuracy(validationSet, attributes, rule);
                if(newAccuracy > currentAccuracy) {
                    rules.get(ruleIndex).setAntecedent(ruleBeforePrune.getAntecedent()); //undo removal
                    indexOfHighestAccuracy = i;
                    currentAccuracy = newAccuracy;
                }

                rules.get(ruleIndex).setAntecedent(ruleBeforePrune.getAntecedent()); //undo removal
            }
        }

        return indexOfHighestAccuracy;

    }

    /**
     * This method prunes a rule by removing a certain precondition
     * It may be called multiple times depending on improvement of accuracy
     * @param rule the rule to prune
     */
    public void pruneRule(Rule rule, int preconditionIndex){
        int ruleIndex = rules.indexOf(rule);
        ArrayList<Statement> antecedent = rule.getAntecedent();
        int ruleSize = antecedent.size();
        if (ruleSize >= preconditionIndex)
            antecedent.remove(preconditionIndex);

        rules.get(ruleIndex).setAntecedent(antecedent);
    }

    /**
     * This method calculates the accuracy of the ruleset based on a certain dataset
     *
     * @param dataset the dataset to use to measure accuracy
     * @param attributes the attributes to use to match rule labels
     * @return the accuracy
     */
    public Double getAccuracy(ArrayList<ArrayList<String>> dataset, Attributes attributes) {
        Double totalExamples = (double) dataset.size();
        Double totalCorrect = 0.0;
        int targetIndex = attributes.getTargetIndex();

        for (ArrayList<String> instance : dataset) { //for each instance in dataset
            boolean isRuleMatched;
            for (Rule rule : rules) {   //for each rule
                isRuleMatched = rule.isMatched(instance, attributes); //try to match rule to instance
                if (isRuleMatched) {
                    String classification = rule.getConsequent();
                    if(instance.get(targetIndex).equalsIgnoreCase(classification))
                        totalCorrect++;
                    break;
                }
            }

        }

        if (totalCorrect == 0)
            return totalCorrect;
        else
            return (totalCorrect / totalExamples) * 100.0;
    }


    /**
     * This method calculates the accuracy of a rule to aid in post-pruning
     * @param dataset the dataset on which to test accuracy
     * @param attributes the attributes to use to map labels to rule
     * @param rule the rule for which the accuracy must be computed
     * @return
     */
    public Double getRuleAccuracy(ArrayList<ArrayList<String>> dataset, Attributes attributes, Rule rule) {
        Double totalExamples = (double) dataset.size();
        Double totalCorrect = 0.0;
        int targetIndex = attributes.getTargetIndex();

        for (ArrayList<String> instance : dataset) { //for each instance in dataset
            boolean isRuleMatched;
                isRuleMatched = rule.isMatched(instance, attributes); //try to match rule to instance
                if (isRuleMatched) {
                    String classification = rule.getConsequent();
                    if(instance.get(targetIndex).equalsIgnoreCase(classification))
                        totalCorrect++;
                    break;
                }
            }



        if (totalCorrect == 0)
            return totalCorrect;
        else
            return (totalCorrect / totalExamples) * 100.0;
    }

    /**
     * This method prints an instance in angle bracket notation
     * (example : <Sunny, Hot, High, Weak, No>)
     * @param instance the instance to print
     */
    public void printInstance(ArrayList<String> instance){
        System.out.print("\n<");
        for(int i = 0; i < instance.size(); i++)
            if(i < instance.size() - 1)
                System.out.print(instance.get(i) + ", ");
        else
                System.out.print(instance.get(i) + ">");
    }


    /**
     * This method traverses a tree recursively and saves all paths
     * @param node the node to begin traversal
     * @param currentPath the current path
     * @param paths the list of paths saved from traversal
     */
    public void traversePath(Node node, ArrayList<Node> currentPath, ArrayList<ArrayList<Node>> paths){
        if(currentPath == null)
            return;

        currentPath.add(node);

        if(node.isLeaf()) {
            paths.add(buildPath(currentPath)); //after reaching the leaf, build a path
        }

        ArrayList<Branch> branches = node.getBranches();

        for (Branch branch : branches) {
            ArrayList<Node> branchNodes = branch.getNodes();

            for (Node child : branchNodes)
                traversePath(child, currentPath, paths);
        }

        int pathIndex = currentPath.indexOf(node);

        for(int i = pathIndex; i < currentPath.size(); i++) //remove sub-path travelled from the currentpath
            currentPath.remove(pathIndex);
    }

    /**
     * This method builds a path by copying/cloning the data from a path of nodes to be used later
     * to generate rules.
     * @param path the path to clone or copy
     * @return a new copy of a path
     */
    private ArrayList<Node> buildPath(ArrayList<Node> path) {
        ArrayList<Node> newList = new ArrayList<Node>();

        for (Node node : path) {
            newList.add(new Node(node.getLabel(), node.getParentLabel()));

            if(!node.isLeaf() && node.getParentLabel()!=null) //add the branch label to the path as a node
                newList.add(new Node(node.getLabel(),node.getParentLabel()));
        }

        return newList;
    }

    /**
     * This method prints the decision tree using pre-order traversal
     * @param node the root node
     * @param stem indentation stem for each node
     */
    public void printTree(Node node, String stem) {
        if (node.isLeaf())
            System.out.print(" : " + node.getLabel() + "\n");
        else
            System.out.print("\n");
        ArrayList<Branch> branches = node.getBranches();

        for (Branch branch : branches) {
            System.out.print(stem + node.getLabel() + " = " + branch.getLabel());
            ArrayList<Node> branchNodes = branch.getNodes();

            for (Node child : branchNodes)
                printTree(child, stem + "|    ");
        }
    }

    /**
     * This method prints the list of rules generated
     */
    public void printRules() {
        for (Rule rule : rules) {
            if(rule.getAntecedent().size()>0) {
                rule.printRule();
                System.out.print("\n");
            }
        }
    }
}

