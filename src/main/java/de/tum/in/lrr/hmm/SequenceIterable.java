package de.tum.in.lrr.hmm;

import java.util.Iterator;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public interface SequenceIterable {

    Iterator<Double> scoringIterator(Iterator<Integer> sequence);

    int numCharacters();

}
