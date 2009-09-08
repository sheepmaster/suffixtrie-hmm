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

    public ISequence subSequencePreceding(SubSequence s) {
        if (s.containingSequence != this) {
            throw new IllegalArgumentException();
        }
        return new SubSequence(this, 0, s.startIndex());
    }

    public ISequence subSequenceFollowing(SubSequence s) {
        if (s.containingSequence != this) {
            throw new IllegalArgumentException();
        }
        return new SubSequence(this, s.endIndex(), length());
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
