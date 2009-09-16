package de.tum.in.lrr.hmm.gene;

import java.util.Iterator;

import de.tum.in.lrr.hmm.ISequence;
import de.tum.in.lrr.hmm.SequenceIterable;
import de.tum.in.lrr.hmm.SubSequence;
import de.tum.in.lrr.hmm.util.TransformingIterator;


/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 * 
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 */
public class ScoredSequence extends SubSequence implements Comparable<ScoredSequence> {

    protected final double score;

    ScoredSequence(ISequence sequence, int startIndex, int endIndex, double score) {
        super(sequence, startIndex, endIndex);
        this.score = score;
    }

    public ScoredSequence(SubSequence sequence, SequenceIterable model, SequenceIterable baseModel) {
        this(sequence.getContainingSequence(), sequence.getStartIndex(), sequence.getEndIndex(), sequence.isComplement(), model, baseModel);
    }

    public ScoredSequence(ISequence sequence, int startIndex, int endIndex, boolean complement, SequenceIterable model, SequenceIterable baseModel) {
        super(sequence, startIndex, endIndex);
        double s = 0;
        Iterator<Double> modelIterator = model.sequenceIterator(this.iterator());
        Iterator<Double> baseModelIterator = baseModel.sequenceIterator(this.iterator());
        while (modelIterator.hasNext()) {
            s += baseModelIterator.next() - modelIterator.next();
        }

        this.score = s;
    }

    public static ScoredSequence search(SequenceIterable model, SequenceIterable baseModel, ISequence sequence) {
        int maxStartIndex = 0;
        int maxEndIndex = 0;
        double maxSum = 0.0;

        final Iterator<Integer> iterator1 = sequence.iterator();
        final Iterator<Integer> iterator2 = sequence.iterator();
        Iterator<Double> modelIterator = model.sequenceIterator(iterator1);
        Iterator<Double> baseModelIterator = baseModel.sequenceIterator(iterator2);
        int startIndex = 0;
        int endIndex = 0;
        double sum = 0.0;
        while(modelIterator.hasNext()) {
            endIndex++;
            sum += baseModelIterator.next() - modelIterator.next();
            if (sum <= 0) {
                sum = 0;
                startIndex = endIndex;
                modelIterator = model.sequenceIterator(iterator1);
                baseModelIterator = baseModel.sequenceIterator(iterator2);
            }
            if (sum > maxSum) {
                maxSum = sum;
                maxStartIndex = startIndex;
                maxEndIndex = endIndex;
            }
        }
        return new ScoredSequence(sequence, maxStartIndex, maxEndIndex, maxSum);
    }

    /**
     * {@inheritDoc}
     */
    public double score() {
        return score;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(ScoredSequence o) {
        return Double.compare(score, o.score);
    }

    /**
     * @param sequences
     * @param model
     * @param baseModel
     * @return
     */
    public static TransformingIterator<SubSequence, ScoredSequence> scoringIterator(
            final Iterable<SubSequence> sequences, final SequenceIterable model, final SequenceIterable baseModel) {
        return new TransformingIterator<SubSequence,ScoredSequence>(sequences.iterator()) {
            @Override
            public ScoredSequence transform(SubSequence s) {
                return new ScoredSequence(s, model, baseModel);
            }
        };
    }

}
