package de.blacksheepsoftware.t9;

import java.util.Arrays;

public class Model {
    protected final int numCharacters;
    
    protected int[][] transitions;
    protected double[][] frequencies;
    protected double[] frequencySums;
    protected int numNodes;
    
    protected static final int DEFAULT_NODES = 16;
    
    public Model(int numCharacters) {
        this(numCharacters, DEFAULT_NODES);
    }
    
    public Model(int numCharacters, int maxNodes) {
        this.numCharacters = numCharacters;
        
        frequencies = new double[maxNodes][numCharacters+1];
        frequencySums = new double[maxNodes];
        transitions = new int[maxNodes][numCharacters+1];
        for (int[] a : transitions) {
            Arrays.fill(a, -1);
        }
    }
    
    protected void extend(int maxNodes) {
        double[][] newFrequencies = new double[maxNodes][numCharacters+1];
        double[] newFrequencySums = new double[maxNodes];
        int[][] newTransitions = new int[maxNodes][numCharacters+1];
        
        for (int i=0; i<transitions.length; i++) {
            System.arraycopy(frequencies[i], 0, newFrequencies[i], 0, numCharacters+1);
            System.arraycopy(transitions[i], 0, newTransitions[i], 0, numCharacters+1);
            newFrequencySums[i] = frequencySums[i];
        }
        for (int i=transitions.length; i<maxNodes; i++) {
            Arrays.fill(transitions[i], -1);
        }
        transitions = newTransitions;
        frequencies = newFrequencies;
        frequencySums = newFrequencySums;
    }
    
    public StateDistribution startingDistribution() {
        return new StateDistribution();
    }
    
    public double perplexity(int[] word) {
        return startingDistribution().perplexity(word);
    }
    
    public double perplexity(int[] word, int[] prefix) {
        return startingDistribution().read(prefix).perplexity(word);
    }
    
    protected void addNode(int parent, int label) {
        if (numNodes >= transitions.length) {
            extend(transitions.length * 2);
        }
        if (transitions[parent][label] != -1) {
            throw new IllegalArgumentException("State "+parent+" already has a child with label "+label);
        }
        transitions[parent][label] = numNodes;
        transitions[numNodes][0] = parent;
        numNodes++;
    }
    
    public class StateDistribution {
        protected final int longestSuffix;
        protected double[] stateProbabilities;

        public StateDistribution() {
            this(0, new double[]{});
        }
        
        protected StateDistribution(int suffix, double[] probabilities) {
            longestSuffix = suffix;
            stateProbabilities = probabilities;
        }
        
        public double getTotalProbability() {
            double total = 0;
            for (double p : stateProbabilities) {
                total += p;
            }
            return total;
        }
        
        public double normalize() {
            double normalizingFactor = 1/getTotalProbability();
            for (int i = 0; i < stateProbabilities.length; i++) {
                stateProbabilities[i] *= normalizingFactor;
            }
            return Math.log(normalizingFactor);
        }
        
        public StateDistribution read(int character) {
            double[] newProbs = null;
            int newLongestSuffix = -1;
            int state = longestSuffix;
            int depth = stateProbabilities.length;
            double p = (depth > 0) ? 0 : 1;
            while (state != -1) {
                if (depth > 0) {
                    p += stateProbabilities[depth-1];
                }
                int t = transitions[state][character];
                if (t != -1) {
                    if (newProbs == null) {
                        newProbs = new double[depth+1];
                        newLongestSuffix = t;
                    }
                    newProbs[depth] = p * frequencies[state][character] / frequencySums[state];
                }
                depth--;
                p *= frequencies[state][0] / frequencySums[state];
                state = transitions[state][0];
            }
            return new StateDistribution(newLongestSuffix, newProbs);
        }
        
        public StateDistribution read(int[] characters) {
            StateDistribution newDistribution = this;
            for (int c : characters) {
                newDistribution = newDistribution.read(c);
                newDistribution.normalize();
            }
            return newDistribution;
        }
        
        public double perplexity(int[] word) {
            double p = 0;
            StateDistribution newDistribution = this;
            for (int c : word) {
                newDistribution = newDistribution.read(c);
                p += newDistribution.normalize();
            }
            return p;
        }
    }
}
