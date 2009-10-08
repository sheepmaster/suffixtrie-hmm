package de.tum.in.lrr.hmm;

import java.util.AbstractList;
import java.util.RandomAccess;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public abstract class AbstractSequence extends AbstractList<Byte> implements ISequence, RandomAccess {

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return length();
    }

    /**
     * @param sequences
     */
    public static int totalLength(Iterable<? extends ISequence> sequences) {
        int totalLength = 0;
        for (ISequence s : sequences) {
            totalLength += s.length();
        }
        return totalLength;
    }

    @Override
    public String toString() {
        return getIdentifier();
    }

}
