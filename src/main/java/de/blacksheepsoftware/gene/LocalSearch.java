package de.blacksheepsoftware.gene;

import java.util.Iterator;

import de.blacksheepsoftware.hmm.ISequence;
import de.blacksheepsoftware.hmm.SequenceIterable;


/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 * 
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 */
public class LocalSearch implements Comparable<LocalSearch> {

    protected final int startIndex;
    protected final int endIndex;
    protected final double sum;

    protected final ISequence sequence;

    protected LocalSearch(ISequence sequence, int startIndex, int endIndex, double sum) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.sum = sum;
        this.sequence = sequence;
    }

    public static LocalSearch search(SequenceIterable model, SequenceIterable baseModel, ISequence sequence) {
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
        return new LocalSearch(sequence, maxStartIndex, maxEndIndex, maxSum);
    }

    public int startIndex() {
        return startIndex;
    }

    public int endIndex() {
        return endIndex;
    }

    public double sum() {
        return sum;
    }

    public ISequence getSequence() {
        return sequence;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(LocalSearch o) {
        return Double.compare(sum, o.sum);
    }

}
