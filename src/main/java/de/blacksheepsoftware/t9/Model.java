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

    public Model(int numCharacters) {
        this(numCharacters, DEFAULT_NODES);
    }

    public Model(int numCharacters, int maxNodes) {
        this.numCharacters = numCharacters;
        this.frequencies = new double[maxNodes][];
        this.frequencySums = new double[maxNodes];
        this.transitions = new int[maxNodes][];
        transitions[0] = new int[numCharacters+1];
        frequencies[0] = new double[numCharacters+1];
        Arrays.fill(transitions[0], -1);
        Arrays.fill(frequencies[0], 1);
        frequencies[0][0] = 0;
        frequencySums[0] = numCharacters;
    }

    public Model(Model m) {
        this(m.numCharacters, m.transitions.length);
        numNodes = m.numNodes;
        for (int i = 0; i < numNodes; i++) {
            transitions[i] = m.transitions[i].clone();
            frequencies[i] = m.frequencies[i].clone();
            frequencySums[i] = m.frequencySums[i];
        }
    }

    protected void extend(int maxNodes) {
        double[][] newFrequencies = new double[maxNodes][];
        double[] newFrequencySums = new double[maxNodes];
        int[][] newTransitions = new int[maxNodes][];

        System.arraycopy(frequencies, 0, newFrequencies, 0, frequencies.length);
        System.arraycopy(frequencySums, 0, newFrequencySums, 0, frequencySums.length);
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
            throw new IllegalArgumentException("State " + parent + " already has a child with label " + label);
        }
        if (numNodes >= transitions.length) {
            extend(transitions.length * 2);
        }
        final int newNode = numNodes++;
        frequencies[newNode] = new double[numCharacters + 1];
        transitions[newNode] = new int[numCharacters + 1];
        Arrays.fill(transitions[newNode], -1);
        transitions[parent][label] = newNode;
        final int back = transitions[parent][0];
        if (back != -1) {
            int t = transitions[back][label];
            if (t == -1) {
                t = addNode(back, label);
            }
            transitions[newNode][0] = t;
        } else { // we are adding a new child to the root node, so the back link just goes back to root.
            transitions[newNode][0] = parent;
        }
        return newNode;
    }

    public void learn(int[] word) {
        Model m = new Model(this);
        startingDistribution().learn(m, word, 0);
        this.frequencies = m.frequencies;
        this.frequencySums = m.frequencySums;
    }

    public class StateDistribution {
        protected final int longestSuffix;

        protected double[] stateProbabilities;

        public StateDistribution() {
            this(0, new double[] {});
        }

        protected StateDistribution(int suffix, double[] probabilities) {
            longestSuffix = suffix;
            stateProbabilities = probabilities;
        }

        public double totalProbability() {
//            if (stateProbabilities.length == 0) {
//                throw new IllegalArgumentException();
//                return 1;
//            }
            double total = 0;
            for (double p : stateProbabilities) {
                total += p;
            }
            return total;
        }

        public double normalize() {
            final double normalizingFactor = 1 / totalProbability();
//            if (Double.isInfinite(normalizingFactor)) {
//                throw new IllegalArgumentException("total probability is zero");
//            } else if (Double.isNaN(normalizingFactor)) {
//                throw new IllegalArgumentException("total probability is not a number");
//            }
            scale(normalizingFactor);
            return Math.log(normalizingFactor);
        }

        public void scale(double scalingFactor) {
            for (int i = 0; i < depth(); i++) {
                stateProbabilities[i] *= scalingFactor;
            }
        }

        public StateDistribution read(int character) {
            return alpha(null, character);
        }

        protected StateDistribution beta(StateDistribution oldBeta, int character) {
            checkExpectedSuffix(oldBeta, character);
            final int depth = depth();
            double[] newProbs = new double[depth];
            int[] states = states();
            double p = 0;
            for (int i = 0; i <= depth; i++) {
                final double smoothing = stateProbability(i);
                final int state = states[i];
                p *= (frequencies[state][0] + smoothing / 2);
                if (character != -1) {
                    p += oldBeta.stateProbabilities[i] * (frequencies[state][character] + smoothing / 2);
                }
                p /= (frequencySums[state] + smoothing);
//                p = (p * (frequencies[state][0] + smoothing / 2) + oldBeta.stateProbabilities[i]
//                        * (frequencies[state][character] + smoothing / 2))
//                        / (frequencySums[state] + smoothing);
                if (i > 0) {
                    newProbs[i - 1] = p;
                } else if (character == -1) {
                    p = 1;
                }
            }

            return new StateDistribution(longestSuffix, newProbs);
        }

        protected StateDistribution alpha(Model m, int character) {
            if (character == -1) {
                return startingDistribution();
            }
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
                        newProbs = new double[depth + 1];
                        newLongestSuffix = t;
                    }
                    newProbs[depth] = p * (frequencies[state][character] + smoothingValue / 2)
                            / (frequencySums[state] + smoothingValue);
                }
                depth--;
                p *= (frequencies[state][0] + smoothingValue / 2) / (frequencySums[state] + smoothingValue);
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
                return stateProbabilities[depth - 1];
            } else if (longestSuffix != 0) {
                // the probability for being in the root state is zero, except
                // for the starting distribution, where no character has been
                // read yet.
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
            int[] states = new int[depth + 1];
            int state = longestSuffix;
            int d = depth;
            while (d >= 0) {
                states[d--] = state;
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
            checkExpectedSuffix(beta, character);
            int depth = depth();
            int[] states = new int[depth + 1];
            double[] incoming = new double[depth + 1];
            {
                double p = 0; // probability for entering the state; saved in incoming
                int state = longestSuffix;
                while (depth >= 0) {
                    final double currentProbability = stateProbability(depth);
                    p += currentProbability;
                    
                    incoming[depth] = p;
                    states[depth] = state;
                    
                    depth--;
                    p *= (frequencies[state][0]+currentProbability/2) / (frequencySums[state]+currentProbability);
                    state = transitions[state][0];
                }
            }
            double p = beta.stateProbability(0); // probability for leaving the state
            for (int i = 0; i < states.length; i++) {
                final int state = states[i];
                final double currentProbability = stateProbability(i);
                if (i > 0) {
                    // p has still the value from the last iteration, i.e. the
                    // probability for leaving the *next* state
                    p *= (frequencies[state][0]+currentProbability/2) / (frequencySums[state]+currentProbability);
                    final double backCount = incoming[i] * p;
                    m.frequencies[state][0] += backCount;
                    m.frequencySums[state] += backCount;
                }

                if (character != -1) {
                    // probability for reading the character in this state
                    final double q = (frequencies[state][character]+currentProbability/2) / (frequencySums[state]+currentProbability) * beta.stateProbabilities[i];
                    p += q;

                    final double readCount = incoming[i] * q;
                    m.frequencies[state][character] += readCount;
                    m.frequencySums[state] += readCount;
                }
            }
        }

        protected void checkExpectedSuffix(StateDistribution beta, int character) throws IllegalArgumentException {
            final int expectedLongestSuffix = (character == -1) ? 0 : transitions[longestSuffix][character];
            if (beta.longestSuffix != expectedLongestSuffix) {
                throw new IllegalArgumentException("Invalid successor state");
            }
        }

        protected StateDistribution learn(Model m, int[] word, int i) {
            if (i > word.length) {
                return this;
            }
            int c = (i < word.length) ? word[i] : -1;
            StateDistribution newAlpha = alpha(m, c);
            final double scalingFactor = 1 / newAlpha.totalProbability();
            newAlpha.scale(scalingFactor);
            StateDistribution beta = newAlpha.learn(m, word, i + 1);
            beta.scale(scalingFactor);
            update(m, beta, c);
            return beta(beta, c);
        }

    }
}
