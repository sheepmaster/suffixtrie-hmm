package de.blacksheepsoftware.hmm;



/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class SubSequence extends AbstractSequence {

    protected final ISequence containingSequence;
    protected final int start;
    protected final int end;
    protected final boolean complement;

    public SubSequence(ISequence containingSequence, int start, int end) {
        this(containingSequence, start, end, false);
    }

    public SubSequence(ISequence containingSequence, int start, int end, boolean complement) {
        if (complement && containingSequence.getAlphabet() != Alphabet.DNA) {
            throw new IllegalArgumentException("Only DNA sequences support complements");
        }

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
        //        if (containingSequence instanceof SubSequence) {
        //            SubSequence subSequence = (SubSequence)containingSequence;
        //            this.containingSequence = subSequence.containingSequence;
        //            this.start = start + subSequence.start;
        //            this.end = end + subSequence.start;
        //        } else {
        this.containingSequence = containingSequence;
        this.start = start;
        this.end = end;
        this.complement = complement;
        //        }
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
    @Override
    public int startIndex() {
        return start;
    }

    /**
     * @return the end
     */
    @Override
    public int endIndex() {
        return end;
    }

    /**
     * {@inheritDoc}
     */
    public String getIdentifier() {
        return complement ? containingSequence.getIdentifier()+"[complement("+start+".."+end+")]"
                : containingSequence.getIdentifier()+"["+start+".."+end+"]";
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
        return complement ? (5 - containingSequence.get(end - index - 1)) : containingSequence.get(index + start);

    }

    @Override
    public ISequence subSequencePreceding(SubSequence s) {
        if (s.containingSequence != this) {
            throw new IllegalArgumentException();
        }
        return new SubSequence(containingSequence, start, start+s.startIndex());
    }

    @Override
    public ISequence subSequenceFollowing(SubSequence s) {
        if (s.containingSequence != this) {
            throw new IllegalArgumentException();
        }
        return new SubSequence(containingSequence, start+s.endIndex(), end);
    }

    public ISequence precedingSubSequence() {
        return containingSequence.subSequencePreceding(this);
    }

    public ISequence followingSubSequence() {
        return containingSequence.subSequenceFollowing(this);
    }

    /**
     * {@inheritDoc}
     */
    public Alphabet getAlphabet() {
        return containingSequence.getAlphabet();
    }

}
