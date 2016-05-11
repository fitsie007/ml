package org.fitz.ml.ga;

import org.fitz.ml.Attribute;
import org.fitz.ml.Attributes;
import org.fitz.ml.constants.GAconstants;
import org.fitz.util.HypothesisSorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * This class provides the main functions for running the genetic algorithm
 * Created by FitzRoi on 3/28/16.
 */
public class GA {
    public GA(){}
    private ArrayList<String> ruleBitStrings;
    private ArrayList<Rule> generatedRules;
    private Attributes attributes;
    private Attribute targetAttribute;
    private ArrayList<ArrayList<String>> instances;
    private ArrayList<ArrayList<String>> testSet;
    private int N = 0; //the length of a rule based on attributes and attribute values
    private int min; //the minimum number of rules per hypothesis
    private int max; //the maximum number of rules per hypothesis

    /**
     * This method initializes various parameters used by the genetic algorithm
     * @param instances examples
     * @param attributes the attributes
     * @param testSet the testset
     * @param min the minimum number of rules per hypothesis
     * @param max the maximum number of rules per hypothesis
     */
    public void init(ArrayList<ArrayList<String>> instances, Attributes attributes, ArrayList<ArrayList<String>> testSet, int min, int max){
        this.instances = instances;
        this.testSet = testSet;
        this.attributes = attributes;
        this.targetAttribute = attributes.getTargetAttribute();
        this.min = min;
        this.max = max;
        for(int i=0; i < attributes.getAttributes().size(); i++){
            if(i == targetAttribute.getIndex())
                N += targetAttribute.getValues().length - 1;
            else N += attributes.get(i).getValues().length;
        }

        this.ruleBitStrings = new ArrayList<String>();
        this.generatedRules = new ArrayList<Rule>();

        String bitStr = new String(new char[N]).replace("\0", "0");
        generateBitStrings(0, new StringBuilder(bitStr));
        generateRules();
//        printRules();
    }

    public ArrayList<ArrayList<String>> getInstances() {
        return instances;
    }

    public ArrayList<ArrayList<String>> getTestSet() {
        return testSet;
    }

    /**
     * This method runs the genetic algorithm by using fitnessThreshold as a stopping criteria
     * @param p the number of random hypotheses to generate per population
     * @param r the replacement rate to be used for crossover
     * @param m the mutation rate
     * @param fitnessThreshold the fitness threshold stopping criteria
     * @param strategy the selection strategy (fitness proportionate, tournament, or rank)
     * @return the fittest hypothesis in the population
     */
    public Hypothesis runGA(int p, double r, double m, double fitnessThreshold, SelectionStrategy strategy) {
        //initialize population: P <-- Generate p hypotheses at random
        Population P = new Population();
        P.setHypotheses(generateHypotheses(p));
        ArrayList<Hypothesis> hypotheses = P.getHypotheses();
        System.out.println(">>>> Now running Genetic Algorithm with the following parameters:");
        System.out.format("\t Hypotheses:%12d\n",hypotheses.size());
        System.out.format("\t Replacement Rate: %6.2f\n", r);
        System.out.format("\t Mutation Rate: %10.3f\n", m);
        System.out.format("\t Fitness Threshold %10.2f (Accuracy: %.2f%%)\n", fitnessThreshold, Math.sqrt(fitnessThreshold) );
        System.out.println("\t Selection Strategy: " + strategy +"\n");

        // Evaluate: for each h in P , compute Fitness(h)
        double maxFitness = evaluateHypotheses(P);

        while (maxFitness < fitnessThreshold) {
            //create a new generation Ps
            Population Ps = new Population();

            //1. Select: Probabilistically select (1 - r )p members of P to add to Ps
            int nMembers = (int) ((1 - r) * p);
            ArrayList<Hypothesis> subH = selectMembers(nMembers, P, strategy);
            Ps.setHypotheses(subH);

            //2. Crossover: Probabilistically select r.p/2 pairs of hypotheses from P, according to Pr(hi)
            int nPairs = (int) (r * p / 2);
            ArrayList<Hypothesis[]> hpairs = selectPairs(nPairs, P);
            twoPointCrossover(hpairs, Ps);

            //3. Mutate: Choose m percent of the members of P, with uniform probability.
            // For each, invert one randomly selected bit in its representation.
            mutate(m, Ps);
//                h.printHypothesisBitStr();
            //4. Update: P <-- Ps
            P = Ps;
            //5. Evaluate: for each h in P , compute Fitness(h)
            maxFitness = evaluateHypotheses(P);
//            System.out.print("max fitness = " + maxFitness +"\n");

        }

        return P.getFittest();

    }

    /**
     * This method runs the genetic algorithm by using number of generations as a stopping criteria
     * @param p the number of random hypotheses to generate per population
     * @param r the replacement rate to be used for crossover
     * @param m the mutation rate
     * @param numGenerations the number of hypotheses to use as a stopping criteria
     * @param strategy the selection strategy (fitness proportionate, tournament, or rank)
     * @return the fittest hypothesis in the population
     */

    public Hypothesis runGA(int p, double r, double m, int numGenerations, SelectionStrategy strategy) {
        //initialize population: P <-- Generate p hypotheses at random
        Population P = new Population();
        P.setHypotheses(generateHypotheses(p));
        ArrayList<Hypothesis> hypotheses = P.getHypotheses();

        String runMsg = ">>>> Now running Genetic Algorithm with the following parameters:";
        String line = new String(new char[runMsg.length()]).replace("\0", "-");
        System.out.println("\n" +line);
        System.out.println(runMsg);
        System.out.println(line);

        System.out.format("\t Hypotheses:%12d\n",hypotheses.size());
        System.out.format("\t Replacement Rate: %6.2f\n", r);
        System.out.format("\t Mutation Rate: %10.3f\n", m);
        System.out.format("\t Generations: %9d\n", numGenerations );
        System.out.println("\t Selection Strategy: " + strategy + "\n");

        // Evaluate: for each h in P , compute Fitness(h)
        evaluateHypotheses(P);

        for(int i = 0; i < numGenerations; i++) {
            //create a new generation Ps
            Population Ps = new Population();

            //1. Select: Probabilistically select (1 - r )p members of P to add to Ps
            int nMembers = (int) ((1 - r) * p);
            ArrayList<Hypothesis> subH = selectMembers(nMembers, P, strategy);
            Ps.setHypotheses(subH);

            //2. Crossover: Probabilistically select r.p/2 pairs of hypotheses from P, according to Pr(hi)
            int nPairs = (int) (r * p / 2);
            ArrayList<Hypothesis[]> hpairs = selectPairs(nPairs, P);
            twoPointCrossover(hpairs, Ps);

            //3. Mutate: Choose m percent of the members of P, with uniform probability.
            // For each, invert one randomly selected bit in its representation.
            mutate(m, Ps);

            //4. Update: P <-- Ps
            P = Ps;

            //5. Evaluate: for each h in P , compute Fitness(h)
            evaluateHypotheses(P);

        }

        return P.getFittest();

    }


    /**
     * This method performs a summation of the fitness of all hypotheses in a population
     * @param hypotheses the hypothesis over which fitness should be summed
     * @return the sum of fitness
     */
    public double totalFitness(ArrayList<Hypothesis> hypotheses){
        double sumFitness = 0;
        for(Hypothesis hypothesis : hypotheses){
            sumFitness += hypothesis.getFitness();
        }
        return sumFitness;
    }

    /**
     * This method computes the probability of a hypothesis being selected
     * from a population based on fitness using the following formula:
     *  pr(hi) =fitness(hi) /
     *           p
     *          Σ  Fitness(hj)
     *          j=1
     * @param hi the hypothesis for which ths probability is to be computed
     * @param hypotheses the set of hypotheses
     * @return probability of a hypothesis being selected
     */
    public double Pr(Hypothesis hi, ArrayList<Hypothesis> hypotheses){
        double fitnessHi = hi.getFitness();
        if(fitnessHi == 0)return 0;
//        System.out.print("fitness: " +fitnessHi +"; totalfitness:" +totalFitness(hypotheses) +"=" + fitnessHi / totalFitness(hypotheses) +"\n");
        return fitnessHi / totalFitness(hypotheses);
    }

    /**
     * This method evaluates the hypotheses in a population by computing fitness
     * and finding the fittest
     * @param P the population
     * @return the maximum fitness
     */
    public double evaluateHypotheses(Population P){
        //For each h in P , compute Fitness(h)
        double maxFitness = Double.MIN_VALUE;
        for (Hypothesis h : P.getHypotheses()) {
            h.computeFitness(instances);

            //determine fittest and maxFitness while computing fitness
            if(h.getFitness() > maxFitness) {
                maxFitness = h.getFitness();
                P.setFittest(h);
            }
        }
        return maxFitness;
    }

    /**
     * This method select members from a population using a selection strategy
     * @param m number of members to select from the population
     * @param P the population
     * @param strategy the selection strategy
     * @return an ArrayList of hypotheses selected from the population
     */
    public ArrayList<Hypothesis> selectMembers(int m, Population P, SelectionStrategy strategy){

        switch(strategy){
            case FITNESS_PROPORTIONATE:
                return selectByFitnessProportionate(m, P);
            case TOURNAMENT:
                return selectByTournament(m, P);
            case RANK:
                return selectByRank(m, P);
        }
        return null;
    }

    /**
     * This method probabilistically selects members from a population using fitness proportionate
     * also known as roulette wheel. In this strategy, members are selected based on their probability
     * computed by pr(hi)
     * @param m the number of members to select
     * @param P the population
     * @return an ArrayList of selected members
     */
    public ArrayList<Hypothesis> selectByFitnessProportionate(int m, Population P){
        ArrayList<Hypothesis> hypotheses = P.getHypotheses();
        ArrayList<Hypothesis> subP = new ArrayList<Hypothesis>();
        for (Hypothesis hi : hypotheses)  //compute Pr(hi) for each hypothesis in P
            hi.setPr(Pr(hi, hypotheses));

        //probabilistically select m hypotheses, where m = (1 - r )p
        for(int i = 0; i < m; i++) {
            Hypothesis hi = probabilisticallySelectOne(hypotheses);
            if(hi != null)
            subP.add(hi);
        }
        return subP;
    }

    /**
     * This method probabilistically selects members from a population using tournament
     * selection. In this strategy, members are selected based on winning a tournament where
     * two members are drawn at random.
     * @param m the number of members to select
     * @param P the population
     * @return an ArrayList of selected members
     */
    public ArrayList<Hypothesis> selectByTournament(int m, Population P){
        ArrayList<Hypothesis> subP = new ArrayList<Hypothesis>();
        ArrayList<Hypothesis> hypotheses = P.getHypotheses();

        for(int i = 0; i < m; i++) {
            Hypothesis h = getFitterOfTwo(hypotheses);
            subP.add(h);
        }
        return subP;
    }

    /**
     * This method selects two individuals and pick the fitter probabilistically
     * with probability p and loser p-1
     * @param P the population from which to choose individuals
     * @return an individual (hypothesis)
     */
    public Hypothesis getFitterOfTwo(ArrayList<Hypothesis> P){
        int index1, index2;
        double pr; //pr is the probability
        Random rand = new Random();
        index1 = rand.nextInt(P.size());
        index2 = rand.nextInt(P.size());
        pr = Math.random();

        //make sure random numbers are different
        while (index1 == index2) {
            index1 = rand.nextInt(P.size());
            index2 = rand.nextInt(P.size());
        }

        Hypothesis individual1 = P.get(index1);
        Hypothesis individual2 = P.get(index2);
        Hypothesis fitter = individual2;
        if (individual1.getFitness() > individual2.getFitness()) //determine fitter candidate
            fitter = individual1;

        //determine winner by pr and loser by pr-1
        if(pr <= GAconstants.TOURNAMENT_SELECTION_PROBABILITY)
            return fitter;

        return individual2; //less fit
    }

    /**
     * This method probabilistically selects members from a population using rank
     * selection. In this strategy, members are sorted based on fitness and then ranked
     * probabilistically
     * @param m the number of members to select
     * @param P the population
     * @return an ArrayList of selected members
     */
    public ArrayList<Hypothesis> selectByRank(int m, Population P){
        ArrayList<Hypothesis> subP = new ArrayList<Hypothesis>();
        ArrayList<Hypothesis> hypotheses = P.getHypotheses();
        HypothesisSorter sorter = new HypothesisSorter();

        //sort by Fitness(h)
        sorter.setSortingParameter(HypothesisSorter.sortBy.FITNESS);
        Collections.sort(P.getHypotheses(), sorter);

        int n = hypotheses.size() - 1; //eg from 0-29
        double sum = n * ( n + 1) / 2; //eg sum of 0-29 = 29(30)/2 = 435
        for(int i = 0; i < n; i++ ){
            Hypothesis h = hypotheses.get(i);
            h.setPr((n - i) / sum);
        }

        //probabilistically select m hypotheses, where m = (1 - r )p
        // (similar to fitness proportionate)
        for(int i = 0; i < m; i++) {
            Hypothesis hi = probabilisticallySelectOne(hypotheses);
            if(hi != null)
                subP.add(hi);
        }
        return subP;
    }


    /**
     * This method select m pairs of hypotheses in order to apply crossover
     * @param m number of pairs to select where m = r*p/2
     * @param P the pppulation from which the pairs of hypotheses should be selected
     * @return m pairs of hypotheses in the form ArrayList<Hypothesis[2]>
     */
    public ArrayList<Hypothesis[]> selectPairs(int m, Population P) {
        ArrayList<Hypothesis[]> pairs = new ArrayList<Hypothesis[]>();
        ArrayList<Hypothesis> hypotheses = P.getHypotheses();

        for (int i = 0;  i < m * 2; i++) {
            //probabilistically select m pairs hypotheses, where m = (1 - r )p
            Hypothesis h1 = probabilisticallySelectOne(hypotheses);
            Hypothesis h2 = probabilisticallySelectOne(hypotheses);

            if(h1 != null && h2!= null) {
                pairs.add(new Hypothesis[]{h1, h2});
            }
        }
//        System.out.print("\n" +pairs.size() +" members selected\n");

        return pairs;
    }

    /**
     * This method probabilistically selects an hypothesis from a list of hypotheses
     * @param hypotheses the list if hypotheses from which to pick 1
     * @return the index of the selected hypothesis
     */
    public Hypothesis probabilisticallySelectOne(ArrayList<Hypothesis> hypotheses){
        double x;
        double y = 0;
        double r = Math.random();

        //probabilistically select an hypothesis from the list
        for (Hypothesis hi : hypotheses) {
            x = y;
            y += hi.getPr();

            if (r >= x && r < y) {
                return hi;
            }
        }
        return null;
    }

    /**
     * This method performs a two-point crossover using pairs of hypotheses
     * @param pairs the pairs of hypotheses
     * @param Ps the population to which the generated offsprings should be added
     */
    public void twoPointCrossover(ArrayList<Hypothesis[]> pairs, Population Ps) {
        Random rand = new Random();
        for (Hypothesis[] pair : pairs) {
            Hypothesis parent1 = pair[0];
            Hypothesis parent2 = pair[1];
            Hypothesis offspring1, offspring2;
            String h1 = parent1.getBitString();
            String h2 = parent2.getBitString();
            if (h1.length() == 0 || h2.length() == 0) {
                break; //can only do randInt(n) with valid bitstrings (length greater than 0)
            }
            int d1, d2, h1Pt1, h1Pt2;
            int len1 = h1.length();
            int len2 = h2.length();

            h1Pt1 = rand.nextInt(len1);
            h1Pt2 = rand.nextInt(len1);
            ArrayList<Integer[]> parent2Pts = new ArrayList<Integer[]>();
            //make sure random numbers are different
            while (h1Pt2 <= h1Pt1) {
                h1Pt1 = rand.nextInt(len1);
                h1Pt2 = rand.nextInt(len1);
            }

            int rule1Len = parent1.getRuleset().get(0).getRuleBitString().length();

            d1 = h1Pt1 % rule1Len;
            d2 = h1Pt2 % rule1Len;
//            System.out.print("(d1,d2)=(" + d1 + "," + d2 + ")\n");

            //use d1 and d2 to determine possible crossover points for h2
            for (int i = 0; i < len2; i += rule1Len) {
                int crossPt1 = i + d1;
                for (int j = 0; j < len2; j += rule1Len) {
                    int crossPt2 = j + d2;
                    if (crossPt1 < crossPt2) {
//                        System.out.print("<" + crossPt1 + "," + crossPt2 + ">\t");
                        parent2Pts.add(new Integer[]{crossPt1, crossPt2});
                    }
                }
            }

            //make sure crossover points generated
            if (parent2Pts.size() > 0) {
                //pick a random set of point from the possible crossover points for h2
                Integer[] randPoint2 = parent2Pts.get(rand.nextInt(parent2Pts.size()));
                int h2Pt1 = randPoint2[0];
                int h2Pt2 = randPoint2[1];

//            System.out.print("picked " + h2Pt1 + ", " + h2Pt2 + "\n");

                // apply crossover using points selected above
                String h3 = h1.substring(0, h1Pt1) + h2.substring(h2Pt1, h2Pt2) + h1.substring(h1Pt2, h1.length());
                String h4 = h2.substring(0, h2Pt1) + h1.substring(h1Pt1, h1Pt2) + h2.substring(h2Pt2, h2.length());
                offspring1 = new Hypothesis(genRuleSetFromHypothesis(h3), attributes);
                offspring2 = new Hypothesis(genRuleSetFromHypothesis(h4), attributes);
                Ps.addHypothesis(offspring1);
                Ps.addHypothesis(offspring2);

            }
        }

    }

    /**
     * This method selects m members of a population and performs
     * mutation by flipping a random bit
     * @param m the number of members to select
     * @param Ps the population from which the members should be taken
     */
    public void mutate(double m, Population Ps){
        ArrayList<Hypothesis> hypotheses = Ps.getHypotheses();
        int nMembers = (int)(Math.round(m * hypotheses.size())); //select m percent of the population Ps
//        System.out.println("mutating " + nMembers +" of " +hypotheses.size() +"\n");
        int n = hypotheses.size();
        ArrayList<Integer> memberIndexes = new ArrayList<Integer>();
        Random rand = new Random();
        for(int i = 0; i < nMembers; i++){
            int index = rand.nextInt(n);
            while(memberIndexes.contains(index))
                index = rand.nextInt(n);
            memberIndexes.add(index);
        }

        for(Integer i: memberIndexes){
            Hypothesis h = hypotheses.get(i);
            StringBuilder sb = new StringBuilder(h.getBitString());
            h.setRuleset(genRuleSetFromHypothesis(flipBit(sb)));
//            h = new Hypothesis(getRuleSetFromHypothesis(flipBit(sb)), attributes);
        }

    }


    /**
     * This method generates p hypotheses from a set of rules
     * @param p the number of hypotheses to generate
     * @return ArrayList of hypotheses
     */
    public ArrayList<Hypothesis> generateHypotheses(int p){
        ArrayList<Hypothesis> randHypotheses = new ArrayList<Hypothesis>();
        Random rand = new Random();
        int m;
        for(int i = 0; i < p; i++) {
            m = rand.nextInt((max - min) + 1) + min;
            ArrayList<Rule> randRules = getRandomRuleSample(m);
//            System.out.print("rules:" +randRules.size()+"\n");
            Hypothesis hypothesis = new Hypothesis(randRules, attributes);
            randHypotheses.add(hypothesis);
        }
        return randHypotheses;
    }

    /**
     * This method selects m random rules from a set of rules in order to create
     * an hypothesis of varied length
     * @param m number of rules to select
     * @return Arraylist of randomly selected rules
     */
    public ArrayList<Rule> getRandomRuleSample(int m){
        ArrayList<Rule> result = new ArrayList<Rule>();
        Random rand = new Random();
        int n = generatedRules.size();

        for(int i = n - m; i < n; i++){
            int pos = rand.nextInt(i+1);
            Rule randRule = generatedRules.get(pos);
            if(result.contains(randRule))
                result.add(generatedRules.get(i));
            else
                result.add(randRule);
        }
        return result;
    }

    /**
     * This method generates rules from a bit string. First bitstrings must be generated and
     * stored
     */
    public void generateRules() {
        generatedRules = new ArrayList<Rule>();

        for (int i = 2; i < ruleBitStrings.size(); i++) { //skip *0 and *1 (invalid rule)
            String ruleBitStr = ruleBitStrings.get(i);
            Rule rule = new Rule();
            rule = rule.getRuleFromString(ruleBitStr, attributes);
            if(rule != null) {
                rule.setRuleBitString(ruleBitStr);
                generatedRules.add(rule);
//                rule.printRule();
            }
        }
    }


    /**
     * This method generates all possible bits strings of length N
     * @param level the level or position in the bit string
     * @param bits a bit string
     */
    public void generateBitStrings(int level, StringBuilder bits){
        if (level == N)
            ruleBitStrings.add(bits.toString());
        else {
            bits.setCharAt(level, '0');
            generateBitStrings(level + 1, bits);
            bits.setCharAt(level, '1');
            generateBitStrings(level + 1, bits);
        }
    }

    /**
     * This method generate a ruleset from a certain hypothesis bit string
     * @param hStr hypothesis bit string
     * @return an ArrayList of rules generated from the hypothesis bit string
     */
    public ArrayList<Rule> genRuleSetFromHypothesis(String hStr) {
        ArrayList<Rule> ruleset = new ArrayList<Rule>();
        int len = hStr.length();
        for (int i = 0; i < len; i += N) {
            String ruleBitStr = hStr.substring(i, Math.min(len, i + N));
            Rule generatedRule = new Rule();
            generatedRule = generatedRule.getRuleFromString(ruleBitStr, attributes);
            if (generatedRule != null && generatedRule.getRuleBitString().length() > 0) //maybe rule or hypothesis has no preconditions (000 1) or invalid postcondition (all ones)
                ruleset.add(generatedRule);
        }
        return ruleset;
    }

    /**
     * This method divides a bit string into parts based on attributes and attribute values
     * and prints it.
     */
    public void printSplitBitStrings(){
        for(int i = 2; i < ruleBitStrings.size(); i++){
            String ruleBitStr = ruleBitStrings.get(i);
            int index;
            int end = 0;
            System.out.print("\n" +ruleBitStr +" --> ");
            for(int j = 0; j < attributes.getAttributes().size(); j++){
                Attribute attribute = attributes.get(j);
                int bitStrSize;
                if(j == targetAttribute.getIndex())
                    bitStrSize = targetAttribute.getValues().length - 1;
                else
                    bitStrSize = attribute.getValues().length;

                index = end;
                end = index + bitStrSize;
                String attrStr = ruleBitStr.substring(index, end);

                System.out.print(attrStr +"\t");

            }
//            System.out.print("\n");
        }
    }



    public int getN(){
        return N;
    }

    /**
     * This method flips a bit in a particular bit string
     * @param sb stringBuilder string
     * @return mutated bit string
     */
    public String flipBit(StringBuilder sb){
        Random rand = new Random();
        if(sb.length()> 0) { //random range must be > 0
            int bitPos = rand.nextInt(sb.length());
            if (sb.charAt(bitPos) == '0')
                sb.setCharAt(bitPos, '1');
            else
                sb.setCharAt(bitPos, '0');
        }

        return sb.toString();
    }

    public int getRuleCount(){
        return generatedRules.size();
    }

    public void printGeneratedRules(){
        for(Rule rule : generatedRules)
            rule.printRule();
    }

    /**
     * This method sorts rules by accuracy (not used at the moment)
     * @param h the hypothesis containing rules to sort
     * @return an hypothesis containing sorted rules
     */
    public Hypothesis sortRules(Hypothesis h){
        ArrayList<Double> ruleAccuracies = new ArrayList<Double>();
        ArrayList<Rule> sortedRules = new ArrayList<Rule>();
        ArrayList<Rule> rules = h.getRuleset();

        //get the accuracy of each rule
        for(Rule rule : rules){
            Double ruleAccuracy = rule.getRuleAccuracy(instances, attributes);
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

//        for(Rule rule : sortedRules)
//            System.out.println(rule.getRuleAccuracy(instances, attributes) +"\n");
        h.setRuleset(sortedRules);

        return h;
    }

    public void printRules(Hypothesis h){
        ArrayList<Rule> rules = h.getRuleset();
        for(Rule rule : rules)
            rule.printRule();
    }

}
