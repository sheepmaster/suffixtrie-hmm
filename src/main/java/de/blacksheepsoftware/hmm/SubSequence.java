package de.blacksheepsoftware.hmm;



/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class SubSequence extends AbstractSequence {

    protected final ISequence containingSequence;
    protected final int start;
    protected final int end;

    public SubSequence(ISequence containingSequence, int start, int end) {
        final int length = containingSequence.length();
        if (start < 0) {
            throw new IndexOutOfBoundsException("start: "+start);
        }
        if (end > length) {
            throw new IndexOutOfBoundsException("end: "+end+" length: "+length);
        }
        if (start > end) {
            throw new IllegalArgumentException("start: "+start+" end: "+end);
        }
        if (containingSequence instanceof SubSequence) {
            SubSequence subSequence = (SubSequence)containingSequence;
            this.containingSequence = subSequence.containingSequence;
            this.start = start + subSequence.start;
            this.end = end + subSequence.start;
        } else {
            this.containingSequence = containingSequence;
            this.start = start;
            this.end = end;
        }
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

    public ISequence precedingSubsequence() {
        return containingSequence.subList(0, start);
    }

    public ISequence followingSequence() {
        return containingSequence.subList(end, containingSequence.length());
    }

}
