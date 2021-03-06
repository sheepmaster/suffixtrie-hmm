package de.tum.in.lrr.hmm;

import java.util.Iterator;

import de.tum.in.lrr.hmm.util.TransformingIterator;


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
    public Iterator<Double> scoringIterator(final Iterator<Byte> sequence) {
        return new TransformingIterator<Byte, Double>(sequence) {

            final double score = Math.log(numChars);

            @Override
            public Double transform(Byte in) {
                return score;
            }
        };
    }

    public int numCharacters() {
        return numChars;
    }


}
