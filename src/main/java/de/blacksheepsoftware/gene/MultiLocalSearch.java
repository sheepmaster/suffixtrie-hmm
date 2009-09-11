package de.blacksheepsoftware.gene;

import java.util.Collections;
import java.util.Iterator;
import java.util.PriorityQueue;

import de.blacksheepsoftware.hmm.ISequence;
import de.blacksheepsoftware.hmm.SequenceIterable;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class MultiLocalSearch implements Iterable<ScoredSequence> {

    protected final ISequence sequence;

    protected final SequenceIterable model;
    protected final SequenceIterable baseModel;

    public MultiLocalSearch(SequenceIterable model, SequenceIterable baseModel, ISequence sequence) {
        this.sequence = sequence;
        this.model = model;
        this.baseModel = baseModel;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<ScoredSequence> iterator() {
        return new LocalSearchIterator();
    }

    protected class LocalSearchIterator implements Iterator<ScoredSequence> {

        protected PriorityQueue<ScoredSequence> searchHits = new PriorityQueue<ScoredSequence>(11, Collections.reverseOrder());

        protected LocalSearchIterator() {
            addSearchHit(sequence);
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasNext() {
            return !searchHits.isEmpty();
        }

        /**
         * {@inheritDoc}
         */
        public ScoredSequence next() {
            final ScoredSequence s = searchHits.remove();
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
                searchHits.add(hit);
            }

        }

        /**
         * {@inheritDoc}
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
