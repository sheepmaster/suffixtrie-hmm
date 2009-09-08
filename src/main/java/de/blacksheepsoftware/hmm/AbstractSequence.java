package de.blacksheepsoftware.hmm;

import java.util.AbstractList;
import java.util.RandomAccess;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public abstract class AbstractSequence extends AbstractList<Integer> implements ISequence, RandomAccess {

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return length();
    }

    public ISequence subList(int start, int end) {
        return new SubSequence(this, start, end);
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


}
