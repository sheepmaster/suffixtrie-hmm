package de.blacksheepsoftware.hmm;

import java.util.AbstractList;
import java.util.RandomAccess;


/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class SubSequence extends AbstractList<Integer> implements ISequence, RandomAccess {

    protected final ISequence containingSequence;
    protected final int start;
    protected final int end;

    public SubSequence(ISequence containingSequence, int start, int end) {
        this.containingSequence = containingSequence;
        this.start = start;
        this.end = end;
    }

    /**
     * @return the containingSequence
     */
    public ISequence getContainingSequence() {
        return containingSequence;
    }

    /**
     * @return the start
     */
    public int startIndex() {
        return start;
    }

    /**
     * @return the end
     */
    public int endIndex() {
        return end;
    }

    /**
     * {@inheritDoc}
     */
    public String getIdentifier() {
        return containingSequence.getIdentifier()+"["+start+".."+end+"]";
    }

    /**
     * {@inheritDoc}
     */
    public int length() {
        return end-start;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer get(int index) {
        final int size = size();
        if (index<0 || index>=size) {
            throw new IndexOutOfBoundsException("Index: "+index+",Size: "+size);
        }
        return containingSequence.get(index + start);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return length();
    }

}
