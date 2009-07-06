package de.blacksheepsoftware.t9;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class UniformBaseModel implements SequenceIterable {

    private static final double LOG_2 = Math.log(2);

    protected final double score;

    public UniformBaseModel(int numChars) {
        score = Math.log(numChars)/LOG_2;
    }

    /**
     * {@inheritDoc}
     */
    public SequenceIterator sequenceIterator() {
        return new SequenceIterator() {
            public double score(int character) {
                return score;
            }
        };
    }


}
