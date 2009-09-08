package de.blacksheepsoftware.gene;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

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

        protected Queue<ISequence> sequences = new LinkedList<ISequence>();

        protected LocalSearchIterator() {
            sequences.add(sequence);
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasNext() {
            return !sequences.isEmpty();
        }

        /**
         * {@inheritDoc}
         */
        public LocalSearch next() {
            final ISequence seq = sequences.remove();
            final LocalSearch s = LocalSearch.search(model, baseModel, seq);
            if (s.startIndex() > 0) {
                sequences.add(s.precedingSubsequence());
            }
            if (s.endIndex() < sequence.length()) {
                sequences.add(s.followingSequence());
            }
            return s;
        }

        /**
         * {@inheritDoc}
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
