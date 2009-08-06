package de.blacksheepsoftware.hmm;

import java.util.Iterator;

import de.blacksheepsoftware.util.TransformingIterator;


/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class UniformModel implements SequenceIterable {

    final double score;

    public UniformModel(int numChars) {
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
