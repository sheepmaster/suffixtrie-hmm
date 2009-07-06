package de.blacksheepsoftware.t9;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class UniformBaseModel implements SequenceIterable {

    private static final double LOG_2 = Math.log(2);

    protected final SequenceIterator sequenceIterator;

    public UniformBaseModel(int numChars) {
        final double score = Math.log(numChars)/LOG_2;
        sequenceIterator = new SequenceIterator() {
            public double score(int character) {
                return score;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public SequenceIterator sequenceIterator() {
        return sequenceIterator;
    }


}
