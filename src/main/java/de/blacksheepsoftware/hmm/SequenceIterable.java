package de.blacksheepsoftware.hmm;

import java.util.Iterator;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public interface SequenceIterable {

    Iterator<Double> sequenceIterator(Iterator<Integer> sequence);

}
