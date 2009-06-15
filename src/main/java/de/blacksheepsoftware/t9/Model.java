package de.blacksheepsoftware.t9;

import java.io.Serializable;
import java.util.Arrays;

public class Model implements Serializable {
    public enum Variant {
        COMPLETE_BACKLINKS,
        PARTIAL_BACKLINKS
    }

    private static final long serialVersionUID = 1L;

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

    public Model(int numCharacters, Variant variant) {
        this(numCharacters, DEFAULT_NODES, variant);
    }

    public Model(int numCharacters, int maxNodes, Variant variant) {
        this.numCharacters = numCharacters;
        this.frequencies = new double[maxNodes][];
        this.frequencySums = new double[maxNodes];
        this.transitions = new int[maxNodes][];

        transitions[BOTTOM] = new int[numCharacters+1];
        Arrays.fill(transitions[BOTTOM], EPSILON);
        transitions[BOTTOM][BACK] = BOTTOM;

        transitions[EPSILON] = new int[numCharacters+1];
        frequencies[EPSILON] = new double[numCharacters+1];
        Arrays.fill(frequencies[EPSILON], 1);
        frequencies[EPSILON][BACK] = 0;
        frequencySums[EPSILON] = numCharacters;

        startingDistribution = StateDistribution.create(this, variant);
    }

    public Model(Model m) {
        this(m.numCharacters, m.transitions.length, m.startingDistribution.getVariant());
        numNodes = m.numNodes;
        for (int i = 1; i < numNodes; i++) {
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
        return startingDistribution;
    }

    public double perplexity(int[] word) {
        return startingDistribution.perplexity(word);
    }

    public double perplexity(int[] word, int[] prefix) {
        return startingDistribution.read(prefix).perplexity(word);
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

    protected static final UpdateStrategy DEFAULT_STRATEGY = new HybridUpdateStrategy();

    public void learn(int[] word) {
        learn(word, DEFAULT_STRATEGY);
    }

    public void learn(int[] word, UpdateStrategy updateStrategy) {
        final Model m = new Model(this);
        updateStrategy.learn(m, word, startingDistribution, startingDistribution, 0, word.length+1);
        this.frequencies = m.frequencies;
        this.frequencySums = m.frequencySums;
    }
}
