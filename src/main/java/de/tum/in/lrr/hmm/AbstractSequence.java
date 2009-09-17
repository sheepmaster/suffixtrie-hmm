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

    public ISequence subSequencePreceding(SubSequence s) {
        if (s.containingSequence != this) {
            throw new IllegalArgumentException();
        }
        return new SubSequence(this, 0, s.getStartIndex());
    }

    public ISequence subSequenceFollowing(SubSequence s) {
        if (s.containingSequence != this) {
            throw new IllegalArgumentException();
        }
        return new SubSequence(this, s.getEndIndex(), length());
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

    public int getStartIndex() {
        return 0;
    }

    public int getEndIndex() {
        return length();
    }

}
