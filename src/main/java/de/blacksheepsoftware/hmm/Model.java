package de.blacksheepsoftware.hmm;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

import de.blacksheepsoftware.util.TransformingIterator;


public class Model extends Trainable implements SequenceIterable, Serializable {
    public enum Variant {
        COMPLETE_BACKLINKS,
        PARTIAL_BACKLINKS
    }

    private static final long serialVersionUID = 1L;

    protected final Alphabet alphabet;

    protected final int numCharacters;

    protected int[][] transitions;

    protected double[][] frequencies;

    protected double[] frequencySums;

    protected int numNodes = 2;

    protected final StateDistribution startingDistribution;

    protected static final int DEFAULT_NODES = 16;

    public static final int BACK = 0;

    public static final int BOTTOM = 0;
    public static final int EPSILON = 1;

    public Model(de.blacksheepsoftware.hmm.Alphabet alphabet, Variant variant) {
        this(alphabet, DEFAULT_NODES, variant);
    }

    public Model(de.blacksheepsoftware.hmm.Alphabet alphabet, int maxNodes, Variant variant) {
        this.alphabet = alphabet;
        this.numCharacters = alphabet.numberOfCharacters();
        this.frequencies = new double[maxNodes][];
        this.frequencySums = new double[maxNodes];
        this.transitions = new int[maxNodes][];

        transitions[BOTTOM] = new int[numCharacters+1];
        Arrays.fill(transitions[BOTTOM], EPSILON);
        transitions[BOTTOM][BACK] = BOTTOM;

        transitions[EPSILON] = new int[numCharacters+1];
        frequencies[EPSILON] = new double[numCharacters+1];

        startingDistribution = StateDistribution.create(this, variant);
    }

    protected Model(Model m) {
        this(m.alphabet, m.transitions.length, m.startingDistribution.getVariant());
    }

    protected void deepCopyFrequenciesFrom(Model m) {
        numNodes = m.numNodes;
        for (int i = 1; i < numNodes; i++) {
            transitions[i] = m.transitions[i].clone();
            frequencies[i] = m.frequencies[i].clone();
            frequencySums[i] = m.frequencySums[i];
        }
    }

    protected void copyTransitionsFrom(Model m) {
        //        final Model newModel = new Model(numCharacters, transitions.length, startingDistribution.getVariant());
        numNodes = m.numNodes;
        for (int i = 1; i < numNodes; i++) {
            transitions[i] = m.transitions[i].clone();
            frequencies[i] = new double[numCharacters+1];
        }
    }

    protected void copyFrequenciesFrom(final Model m) {
        this.frequencies = m.frequencies;
        this.frequencySums = m.frequencySums;
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
        return startingDistribution;
    }

    public double perplexity(ISequence word) {
        return startingDistribution.perplexity(word);
    }

    public double perplexity(ISequence word, ISequence prefix) {
        return startingDistribution.successor(prefix).perplexity(word);
    }

    /**
     * @param sequences
     * @param totalLength
     * @param model
     * @return
     */
    public double averagePerplexity(Iterable<? extends ISequence> sequences) {
        final int totalLength = Sequence.totalLength(sequences);
        double testPerplexity = 0;
        for (ISequence s : sequences) {
            testPerplexity += perplexity(s);
        }
        testPerplexity /= totalLength;
        return testPerplexity;
    }

    public Iterator<Double> sequenceIterator(Iterator<Integer> seq) {
        return new TransformingIterator<Integer, Double>(seq) {
            protected StateDistribution dist = startingDistribution;

            @Override
            public Double transform(Integer in) {
                dist = dist.successor(in);
                return dist.normalize();
            }
        };
    }

    protected int addNode(int parent, int label) {
        if (transitions[parent][label] != BOTTOM) {
            throw new IllegalArgumentException("State " + parent + " already has a child with label " + label);
        }
        if (numNodes >= transitions.length) {
            if (transitions.length == Integer.MAX_VALUE) {
                throw new OutOfMemoryError();
            }
            final int maxNodes;
            if (transitions.length >= Integer.MAX_VALUE / 2) {
                maxNodes = Integer.MAX_VALUE;
            } else {
                maxNodes = transitions.length << 1;
            }
            extend(maxNodes);
        }
        final int newNode = numNodes++;
        frequencies[newNode] = new double[numCharacters + 1];
        transitions[newNode] = new int[numCharacters + 1];
        //       Arrays.fill(transitions[newNode], -1);
        transitions[parent][label] = newNode;
        final int back = transitions[parent][BACK];
        int t = transitions[back][label];
        if (t == BOTTOM) {
            t = addNode(back, label);
        }
        transitions[newNode][BACK] = t;
        return newNode;
    }

    /**
     * Uses a divide-and-conquer approach to reduce the memory footprint from
     * <i>O(n)</i> to <i>O(log n)</i>, for an increase in runtime from
     * <i>O(n)</i> to <i>O(n log n)</i>.
     * 
     * @see #learnStep(int[], StateDistribution, StateDistribution, int, int, int, int)
     */
    protected StateDistribution learn(int[] word, StateDistribution alpha_start, StateDistribution beta_end, int start, int end, int maxDepth, int linearThreshold, boolean useSmoothing) {
        final int diff = end - start;
        if (diff > linearThreshold) {
            final int mid = start + diff / 2;
            StateDistribution alpha_mid = alpha_start;
            for (int i = start; i < mid; i++) {
                assert i < word.length;
                alpha_mid = alpha_mid.alpha(this, word[i], maxDepth, useSmoothing);
                alpha_mid.normalize();
            }
            final StateDistribution beta_mid = learnStep(word, alpha_mid, beta_end, mid, end, maxDepth, linearThreshold, useSmoothing);

            return (start >= mid) ? beta_mid : learnStep(word, alpha_start, beta_mid, start, mid, maxDepth, linearThreshold, useSmoothing);
        } else {
            return learnStep(word, alpha_start, beta_end, start, end, maxDepth, linearThreshold, useSmoothing);
        }
    }

    /**
     * <p>
     * Updates this model with the inferred state transitions when
     * reading {@code word} from index {@code i} to {@code j}.
     * </p>
     * 
     * <p>
     * It is assumed that {@code word} ends with a sentinel character ({@code
     * StateDistribution.INVALID}), which means that the last transition has to
     * end in the epsilon state.
     * </p>
     * 
     * @param word The word to learn.
     * @param alpha_i alpha[i], i.e. the forward message at index {@code i}.
     * @param beta_j beta[j], i.e. the backwards message at index {@code j}.
     * @param i The start index.
     * @param j The end index.
     * @param maxDepth The maximum depth of the trie.
     * @return beta[i], i.e. the backwards message at index {@code i};
     * 
     * @see #learn(int[], StateDistribution, StateDistribution, int, int, int, int)
     */
    private StateDistribution learnStep(int[] word, StateDistribution alpha_i, StateDistribution beta_j, int i, int j, int maxDepth, int linearThreshold, boolean useSmoothing) {
        final int c = (i < word.length) ? word[i] : StateDistribution.INVALID;
        final StateDistribution alpha_i1 = alpha_i.alpha(this, c, maxDepth, useSmoothing);
        final double scalingFactor = 1 / alpha_i1.totalProbability();
        alpha_i1.scale(scalingFactor);
        final StateDistribution beta_i1 = (i + 1 >= j) ? beta_j : learn(word, alpha_i1, beta_j, i + 1, j, maxDepth, linearThreshold, useSmoothing);
        beta_i1.scale(scalingFactor);
        alpha_i.update(this, beta_i1, c);
        final StateDistribution beta_i = alpha_i.beta(beta_i1, c, useSmoothing);
        return beta_i;
    }

    @Override
    public void learn(int[] word, int maxDepth, int linearThreshold) {
        final Model m = new Model(this);
        m.deepCopyFrequenciesFrom(this);
        m.learn(word, startingDistribution, startingDistribution, 0, word.length+1, maxDepth, linearThreshold, true);
        copyFrequenciesFrom(m);
    }

    /**
     * @return
     */
    public int numStates() {
        return numNodes;
    }

    /**
     * @return
     */
    public int numCharacters() {
        return numCharacters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Alphabet getAlphabet() {
        return alphabet;
    }

    /**
     * @param oldModel
     * @param otherModel
     * @return
     */
    public double parameterDifference(Model otherModel) {
        if (otherModel.numCharacters != numCharacters || otherModel.numNodes != numNodes) {
            throw new IllegalArgumentException();
        }
        double diff = 0;
        for (int i=1; i<numNodes; i++) {
            //            if (transitions[i] != null) {
            for (int j=0; j<=numCharacters; j++) {
                final double p = frequencies[i][j]/frequencySums[i];
                final double p2 = otherModel.frequencies[i][j]/otherModel.frequencySums[i];
                if (!(Double.isNaN(p) || Double.isNaN(p2))) {
                    diff += Math.abs(p - p2);
                }
            }
            //            }
        }
        return diff / (numNodes * (numCharacters+1));
    }

    public void cleanup() {
        for (int i=1; i<numNodes; i++) {
            //            if (transitions[i] != null) {
            for (int j=1; j <= numCharacters; j++) {
                final int state = transitions[i][j];
                if (state != BOTTOM && ((frequencies[i][j]/frequencySums[i] == 0) || frequencySums[state] == 0)) {
                    transitions[i][j] = BOTTOM;
                    //                        transitions[state] = null;
                    //                        frequencies[state] = null;
                    //                    System.err.println("Removed state "+state);
                }
                //                }
            }
        }
    }

}
