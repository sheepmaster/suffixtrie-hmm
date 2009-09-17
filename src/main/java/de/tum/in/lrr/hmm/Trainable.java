package de.tum.in.lrr.hmm;


/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public abstract class Trainable {

    protected static final int DEFAULT_THRESHOLD = 16383;

    protected abstract void learn(byte[] word, int maxDepth, int defaultThreshold);

    public abstract Alphabet getAlphabet();

    protected void learn(Sequence seq, int maxDepth, int defaultThreshold) {
        if (seq.getAlphabet() != getAlphabet()) {
            throw new IllegalArgumentException("Invalid alphabet");
        }
        learn(seq.charSequence(), maxDepth, defaultThreshold);
    }

    public void learn(Sequence seq, int maxDepth) {
        learn(seq, maxDepth, DEFAULT_THRESHOLD);
    }

    protected static final int DEFAULT_MAXIMUM_DEPTH = Integer.MAX_VALUE;

    public void learn(Sequence seq) {
        learn(seq, DEFAULT_MAXIMUM_DEPTH);
    }

}
