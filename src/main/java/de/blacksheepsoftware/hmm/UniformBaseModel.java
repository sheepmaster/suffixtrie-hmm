package de.blacksheepsoftware.hmm;

import java.util.Iterator;


/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class UniformBaseModel implements SequenceIterable {

    final double score;

    public UniformBaseModel(int numChars) {
        score = Math.log(numChars);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Double> sequenceIterator(final Iterator<Integer> sequence) {
        return new TransformingIterator<Integer, Double>(sequence) {
            @Override
            public Double transform(Integer in) {
                return score;
            }
        };
    }


}
