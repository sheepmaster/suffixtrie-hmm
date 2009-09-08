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
public class MultiLocalSearch implements Iterable<LocalSearch> {

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
    public Iterator<LocalSearch> iterator() {
        return new LocalSearchIterator();
    }

    protected class LocalSearchIterator implements Iterator<LocalSearch> {

        protected PriorityQueue<LocalSearch> searchHits = new PriorityQueue<LocalSearch>(11, Collections.reverseOrder());

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
        public LocalSearch next() {
            final LocalSearch s = searchHits.remove();
            final ISequence seq = s.getContainingSequence();
            addSearchHit(seq.subSequencePreceding(s));
            addSearchHit(seq.subSequenceFollowing(s));
            return s;
        }

        /**
         * @param seq
         */
        private void addSearchHit(final ISequence seq) {
            if (seq.length() == 0) {
                return;
            }
            final LocalSearch hit = LocalSearch.search(model, baseModel, seq);
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
