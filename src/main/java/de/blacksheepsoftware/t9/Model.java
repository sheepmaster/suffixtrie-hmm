package de.blacksheepsoftware.t9;

import java.io.Serializable;
import java.util.Arrays;

public class Model implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final int numCharacters;
    
    protected int[][] transitions;
    protected double[][] frequencies;
    protected double[] frequencySums;
    protected int numNodes = 1;
    
    protected static final int DEFAULT_NODES = 16;
    
    protected Model(int characters, int[][] transitions) {
        int maxNodes = transitions.length;
        this.numCharacters = characters;
        this.frequencies = new double[maxNodes][numCharacters+1];
        this.frequencySums = new double[maxNodes];
        this.transitions = transitions;
    }

    public Model(int numCharacters) {
        this(numCharacters, DEFAULT_NODES);
    }
        
    public Model(int numCharacters, int maxNodes) {
        this(numCharacters, new int[maxNodes][numCharacters+1]);
        for (int[] a : transitions) {
            Arrays.fill(a, -1);
        }
    }
    
    public Model(Model m) {
        this(m.numCharacters, m.transitions);
        for (int i=0; i<m.transitions.length; i++) {
            System.arraycopy(m.frequencies[i], 0, frequencies[i], 0, numCharacters+1);
            frequencySums[i] = m.frequencySums[i];
        }
    }
    
    protected void extend(int maxNodes) {
        double[][] newFrequencies = new double[maxNodes][];
        double[] newFrequencySums = new double[maxNodes];
        int[][] newTransitions = new int[maxNodes][];
        
        System.arraycopy(frequencies, 0, newFrequencies, 0, frequencies.length);
        System.arraycopy(frequencySums, 0, newTransitions, 0, frequencySums.length);
        System.arraycopy(transitions, 0, newTransitions, 0, transitions.length);

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
    
    protected int addNode(int parent, int label) {
        if (transitions[parent][label] != -1) {
            throw new IllegalArgumentException("State "+parent+" already has a child with label "+label);
        }
        if (numNodes >= transitions.length) {
            extend(transitions.length * 2);
        }
        frequencies[numNodes] = new double[numCharacters+1];
        transitions[numNodes] = new int[numCharacters+1];
        Arrays.fill(transitions[numNodes], -1);
        transitions[parent][label] = numNodes;
        int back = transitions[parent][0];
        transitions[numNodes][0] = (back == -1) ? parent : transitions[back][label];
        return numNodes++;
    }
    
    public Model learn(int[] word) {
        Model m = new Model(this);
        startingDistribution().learn(m, word, 0);
        return m;
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
        
        public double totalProbability() {
            double total = 0;
            for (double p : stateProbabilities) {
                total += p;
            }
            return total;
        }
        
        public double normalize() {
            double normalizingFactor = 1/totalProbability();
            scale(normalizingFactor);
            return Math.log(normalizingFactor);
        }
        
        public void scale(double scalingFactor) {
            for (int i = 0; i < depth(); i++) {
                stateProbabilities[i] *= scalingFactor;
            }
        }
        
        public StateDistribution read(int character) {
            return alpha(character, null);
        }
        
        protected StateDistribution beta(int character, StateDistribution oldBeta) {
            int depth = depth();
            double[] newProbs = new double[depth];
            int[] states = states();
            double p = 0;
            for (int i=0; i<=depth; i++) {
                double smoothing = stateProbability(i);
                p = (p * (frequencies[states[i]][0]+smoothing/2) + oldBeta.stateProbabilities[i]*(frequencies[states[i]][character]+smoothing/2))/(frequencySums[states[i]]+smoothing);
                if (i > 0) {
                    newProbs[i-1] = p;
                }
            }
            
            return new StateDistribution(longestSuffix, newProbs);
        }

        protected StateDistribution alpha(int character, Model m) {
            double[] newProbs = null;
            int newLongestSuffix = -1;
            int state = longestSuffix;
            int depth = depth();
            double p = 0;
            while (state != -1) {
                double currentProbability = stateProbability(depth);
                
                p += currentProbability;
                double smoothingValue = (m == null) ? 0 : currentProbability;
                int t = transitions[state][character];
                if ((t == -1) && (m != null)) {
                    // if we are in training mode, add the missing state
                    t = addNode(state, character);
                    m.addNode(state, character);
                }
                if (t != -1) {
                    if (newProbs == null) {
                        newProbs = new double[depth+1];
                        newLongestSuffix = t;
                    }
                    newProbs[depth] = p * ((frequencies[state][character]+smoothingValue/2) / (frequencySums[state])+smoothingValue);
                }
                depth--;
                p *= (frequencies[state][0]+smoothingValue/2) / (frequencySums[state]+smoothingValue);
                state = transitions[state][0];
            }
            return new StateDistribution(newLongestSuffix, newProbs);
        }

        /**
         * @param depth
         * @return
         */
        public double stateProbability(int depth) {
            if (depth > 0) {
                return stateProbabilities[depth-1];
            } else if (longestSuffix != 0) {
                // the probability for being in the root state is zero, except for the starting distribution, where no character has been read yet.
                return 0;
            } else {
                return 1;
            }
        }

        public int depth() {
            return stateProbabilities.length;
        }
        
        /**
         * @return an array of possible states, starting with the root state.
         */
        public int[] states() {
            int depth = depth();
            int[] states = new int[depth+1];
            int state = longestSuffix;
            int d = depth;
            while (d >= 0) {
                states[d] = state;
                state = transitions[state][0];
            }
            return states;
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
        
        protected void update(Model m, StateDistribution beta, int character) {
            if (((character == -1) && beta.longestSuffix != 0) || (transitions[longestSuffix][character] != beta.longestSuffix)) {
                throw new IllegalArgumentException("Invalid successor state");
            }
            double p = 0; // probability for entering the state; saved in incoming
            int depth = depth();
            int state = longestSuffix;
            int[] states = new int[depth+1];
            double[] incoming = new double[depth+1];
            while (depth >= 0) {
                if (depth > 0) {
                    p += stateProbabilities[depth-1];
                }
                
                incoming[depth] = p;
                states[depth] = state;

                depth--;
                p *= frequencies[state][0]/frequencySums[state];
                state = transitions[state][0];
            }
            p = 0; // probability for leaving the state
            for (int i=0; i<states.length; i++) {
                int s = states[i];
                if (i > 0) {
                    // p has still the value from the last iteration, i.e. the probability for leaving the *previous* state 
                    double backCount = incoming[i] * frequencies[state][0]/frequencySums[state] * p;
                    m.frequencies[s][0] += backCount;
                    m.frequencySums[s] += backCount;
                    p *= frequencies[s][0]/frequencySums[s];
                }
                
                if (character != -1) {
                    double q = frequencies[s][character]/frequencySums[s] * beta.stateProbabilities[i]; // probability for reading the character in this state
                    p += q;
                    
                    double readCount = incoming[i] * q;
                    m.frequencies[s][character] += readCount;
                    m.frequencySums[s] += readCount;
                }
            }
        }
        
        protected StateDistribution learn(Model m, int[] word, int i) {
            if (i >= word.length) {
                StateDistribution d = startingDistribution();
                update(m, d, -1); // final transition is back to root without reading a character
                return d;
            }
            StateDistribution newAlpha = alpha(word[i], m);
            double scalingFactor = 1/newAlpha.totalProbability();
            newAlpha.scale(scalingFactor);
            StateDistribution beta = newAlpha.learn(m, word, i+1);
            beta.scale(scalingFactor);
            update(m, beta, word[i]);
            return beta(word[i], beta);
        }

    }
}
