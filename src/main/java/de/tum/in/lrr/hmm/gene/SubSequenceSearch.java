package de.tum.in.lrr.hmm.gene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.tum.in.lrr.hmm.ISequence;
import de.tum.in.lrr.hmm.SequenceIterable;
import de.tum.in.lrr.hmm.util.GeneratingIterator;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class SubSequenceSearch extends GeneratingIterator<ScoredSequence> {

    final ISequence sequence;

    final Iterator<Double> modelIterator;
    final Iterator<Double> baseModelIterator;

    final List<ScoredSequence> subSequences = new ArrayList<ScoredSequence>();
    final List<Double> startTotals = new ArrayList<Double>();
    final List<Double> endTotals = new ArrayList<Double>();

    double total = 0;
    int index = 0;


    public SubSequenceSearch(SequenceIterable model, SequenceIterable baseModel, ISequence sequence) {
        this.sequence = sequence;
        final Iterator<Byte> iterator1 = sequence.iterator();
        final Iterator<Byte> iterator2 = sequence.iterator();
        modelIterator = model.scoringIterator(iterator1);
        baseModelIterator = baseModel.scoringIterator(iterator2);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canGenerateValues() {
        return modelIterator.hasNext();
    }

    /**
     * 
     */
    private int findIndex(double startTotal) {
        for (int i = startTotals.size()-1; i >= 0; i--) {
            if (startTotals.get(i) < startTotal) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param start
     * @param end
     * @param startTotal
     * @param endTotal
     */
    private void addSequence(int start, int end, double startTotal, double endTotal) {
        final ScoredSequence seq = new ScoredSequence(sequence, start, end, false, endTotal-startTotal);
        subSequences.add(seq);
        startTotals.add(startTotal);
        endTotals.add(endTotal);
    }

    void addOrMergeSequence(int start, int end, double startTotal, double endTotal) {
        final int j = findIndex(startTotal);

        if (j == -1) {
            clearSequences();
            addSequence(start, end, startTotal, endTotal);
        } else if (endTotals.get(j) >= endTotal) {
            addSequence(start, end, startTotal, endTotal);
        } else {
            final Double startTotal_j = startTotals.get(j);
            final ScoredSequence seq_j = subSequences.get(j);
            subSequences.subList(j, subSequences.size()).clear();
            startTotals.subList(j, startTotals.size()).clear();
            endTotals.subList(j, endTotals.size()).clear();
            addOrMergeSequence(seq_j.getStartIndex(), end, startTotal_j, endTotal);
        }
    }

    /**
     * 
     */
    private void clearSequences() {
        for (ScoredSequence s : subSequences) {
            addValue(s);
        }
        subSequences.clear();
        startTotals.clear();
        endTotals.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void generateValues() {
        final double score = baseModelIterator.next() - modelIterator.next();

        final double newTotal = score+total;
        if (score > 0) {
            addOrMergeSequence(index, index+1, total, newTotal);
        }
        total = newTotal;
        index++;

        if (!modelIterator.hasNext()) {
            clearSequences();
        }
    }


}
