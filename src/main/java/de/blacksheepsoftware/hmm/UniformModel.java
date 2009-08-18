package de.blacksheepsoftware.hmm;

import java.util.Iterator;

import de.blacksheepsoftware.util.TransformingIterator;


/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class UniformModel implements SequenceIterable {

    final int numChars;

    public UniformModel(int numChars) {
        this.numChars = numChars;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Double> sequenceIterator(final Iterator<Integer> sequence) {
        return new TransformingIterator<Integer, Double>(sequence) {

            final double score = Math.log(numChars);

            @Override
            public Double transform(Integer in) {
                return score;
            }
        };
    }

    public int numCharacters() {
        return numChars;
    }


}
