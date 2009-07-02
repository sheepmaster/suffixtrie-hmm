package de.blacksheepsoftware.t9;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public abstract class Trainable {

    protected static final int DEFAULT_THRESHOLD = 127;

    protected abstract void learn(int[] word, int maxDepth, int defaultThreshold);

    public void learn(int[] word, int maxDepth) {
        learn(word, maxDepth, DEFAULT_THRESHOLD);
    }

    protected static final int DEFAULT_MAXIMUM_DEPTH = Integer.MAX_VALUE;

    public void learn(int[] word) {
        learn(word, DEFAULT_MAXIMUM_DEPTH);
    }

}
