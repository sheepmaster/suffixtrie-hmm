package de.tum.in.lrr.hmm.gene;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.PriorityQueue;

import de.tum.in.lrr.hmm.SequenceIterable;
import de.tum.in.lrr.hmm.SubSequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class SoftMax implements Iterable<ScoredSequence> {

    protected final Deque<ScoredSequence> sequences = new ArrayDeque<ScoredSequence>();

    protected final double offset;

    public SoftMax(Iterator<ScoredSequence> scoredSequences) {
        this(scoredSequences, Integer.MAX_VALUE-1);
    }

    public SoftMax(Iterable<SubSequence> sequences, final SequenceIterable model, final SequenceIterable baseModel) {
        this(sequences, model, baseModel, Integer.MAX_VALUE-1);
    }

    public SoftMax(Iterable<SubSequence> sequences, final SequenceIterable model, final SequenceIterable baseModel, int numEntries) {
        this(ScoredSequence.scoringIterator(sequences, model, baseModel), numEntries);
    }

    public SoftMax(Iterator<ScoredSequence> scoredSequences, int numEntries) {
        final PriorityQueue<ScoredSequence> sequenceQueue = new PriorityQueue<ScoredSequence>();
        double max = Double.NEGATIVE_INFINITY;
        double expSum = 0;
        while (scoredSequences.hasNext()) {
            final ScoredSequence s = scoredSequences.next();
            final double score = s.score();
            if (score > max) {
                expSum = expSum * Math.exp(max - score) + 1;
                max = score;
            } else {
                expSum += Math.exp(score - max);
            }
            if (sequenceQueue.size() < numEntries || s.score() >= sequenceQueue.peek().score()) {
                sequenceQueue.add(s);
                while (sequenceQueue.size() > numEntries) {
                    sequenceQueue.remove();
                }
            }
        }
        while (!sequenceQueue.isEmpty()) {
            sequences.push(sequenceQueue.remove());
        }
        offset = Math.log(expSum) + max;
    }

    protected void addSequence(ScoredSequence s) {
        System.out.println(offset);
    }

    public Iterator<ScoredSequence> iterator() {
        return sequences.iterator();
    }

    public double probability(ScoredSequence s) {
        return Math.exp(s.score() - offset);
    }

}
