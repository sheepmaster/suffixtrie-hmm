package de.tum.in.lrr.hmm.gene;

import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import de.tum.in.lrr.hmm.SequenceIterable;
import de.tum.in.lrr.hmm.SubSequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class SoftMax implements Iterable<ScoredSequence> {

    protected final SortedSet<ScoredSequence> sequenceList = new TreeSet<ScoredSequence>(Collections.reverseOrder());

    protected final double offset;

    public SoftMax(final Iterable<SubSequence> sequences, final SequenceIterable model, final SequenceIterable baseModel) {
        this(ScoredSequence.scoringIterator(sequences, model, baseModel));
    }

    public SoftMax(Iterator<ScoredSequence> sequences) {
        double max = Double.NEGATIVE_INFINITY;
        double expSum = 0;
        while (sequences.hasNext()) {
            final ScoredSequence s = sequences.next();
            final double score = s.score();
            if (score > max) {
                expSum = expSum * Math.exp(max - score) + 1;
                max = score;
            } else {
                expSum += Math.exp(score - max);
            }
            sequenceList.add(s);
        }
        offset = Math.log(expSum) + max;
    }

    public Iterator<ScoredSequence> iterator() {
        return sequenceList.iterator();
    }

    public double probability(ScoredSequence s) {
        return Math.exp(s.score() - offset);
    }

}
