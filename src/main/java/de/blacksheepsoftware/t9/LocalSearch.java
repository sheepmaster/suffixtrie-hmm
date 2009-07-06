package de.blacksheepsoftware.t9;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class LocalSearch {

    protected int maxStartIndex = 0;
    protected int maxEndIndex = 0;
    protected double maxSum = 0.0;

    public LocalSearch(SequenceIterable model, SequenceIterable baseModel, Iterable<Integer> sequence) {
        SequenceIterator modelIterator = model.sequenceIterator();
        SequenceIterator baseModelIterator = baseModel.sequenceIterator();
        int startIndex = 0;
        int endIndex = 0;
        double sum = 0.0;
        for (int c : sequence) {
            endIndex++;
            sum += baseModelIterator.score(c) - modelIterator.score(c);
            if (sum <= 0) {
                sum = 0;
                startIndex = endIndex;
                modelIterator = model.sequenceIterator();
                baseModelIterator = baseModel.sequenceIterator();
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
