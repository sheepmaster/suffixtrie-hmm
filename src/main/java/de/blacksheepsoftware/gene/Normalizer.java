package de.blacksheepsoftware.gene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.blacksheepsoftware.hmm.SequenceIterable;
import de.blacksheepsoftware.hmm.SubSequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class Normalizer implements Iterable<ScoredSequence> {

    protected final List<ScoredSequence> sequenceList = new ArrayList<ScoredSequence>();

    protected final double offset;

    public Normalizer(final Iterable<SubSequence> sequences, final SequenceIterable model, final SequenceIterable baseModel) {
        this(ScoredSequence.scoringIterator(sequences, model, baseModel));
    }

    public Normalizer(Iterator<ScoredSequence> sequences) {
        double max = Double.NEGATIVE_INFINITY;
        while (sequences.hasNext()) {
            final ScoredSequence s = sequences.next();
            max = Math.max(max, s.score());
            sequenceList.add(s);
        }
        double expSum = 0;
        for (ScoredSequence s : sequenceList) {
            expSum += Math.exp(s.score() - max);
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
