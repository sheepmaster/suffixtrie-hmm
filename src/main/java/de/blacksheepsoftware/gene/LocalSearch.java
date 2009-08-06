package de.blacksheepsoftware.gene;

import java.util.Iterator;

import de.blacksheepsoftware.hmm.SequenceIterable;


/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class LocalSearch {

    protected int maxStartIndex = 0;
    protected int maxEndIndex = 0;
    protected double maxSum = 0.0;

    public LocalSearch(SequenceIterable model, SequenceIterable baseModel, Iterable<Integer> sequence) {
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
    }

    public int startIndex() {
        return maxStartIndex;
    }

    public int endIndex() {
        return maxEndIndex;
    }

    public double sum() {
        return maxSum;
    }

}
