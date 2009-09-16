package de.tum.in.lrr.hmm.gene;

import java.util.Collections;
import java.util.Iterator;
import java.util.PriorityQueue;

import de.tum.in.lrr.hmm.ISequence;
import de.tum.in.lrr.hmm.SequenceIterable;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class SubSequenceSearch implements Iterator<ScoredSequence> {

    protected final ISequence sequence;

    protected final SequenceIterable model;
    protected final SequenceIterable baseModel;

    public SubSequenceSearch(SequenceIterable model, SequenceIterable baseModel, ISequence sequence) {
        this.sequence = sequence;
        this.model = model;
        this.baseModel = baseModel;
        addSearchHit(sequence);
    }


    protected PriorityQueue<ScoredSequence> searchHitQueue = new PriorityQueue<ScoredSequence>(11, Collections.reverseOrder());

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        return !searchHitQueue.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public ScoredSequence next() {
        final ScoredSequence s = searchHitQueue.remove();
        addSearchHit(s.precedingSubSequence());
        addSearchHit(s.followingSubSequence());
        return s;
    }

    /**
     * @param seq
     */
    private void addSearchHit(final ISequence seq) {
        if (seq.length() == 0) {
            return;
        }
        final ScoredSequence hit = ScoredSequence.search(model, baseModel, seq);
        if (hit.length() > 0) {
            searchHitQueue.add(hit);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }


}
