package de.blacksheepsoftware.hmm;


/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class UniformBaseModel implements SequenceIterable {

    protected final SequenceIterator sequenceIterator;

    public UniformBaseModel(int numChars) {
        final double score = Math.log(numChars);
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
