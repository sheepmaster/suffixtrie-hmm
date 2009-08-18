package de.blacksheepsoftware.gene;

import de.blacksheepsoftware.evaluation.FiniteRandomSequence;
import de.blacksheepsoftware.hmm.SequenceIterable;
import de.blacksheepsoftware.util.Stats;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class ModelCalibration {

    private static final int NUM_SEQUENCES = 10000;
    private static final int SEQUENCE_LENGTH = 4000;

    protected double lambda;
    protected double k;

    protected final SequenceIterable model;
    protected final SequenceIterable baseModel;

    public ModelCalibration(SequenceIterable m, SequenceIterable baseModel) {
        this.model = m;
        this.baseModel = baseModel;

        calibrate();
    }

    private void calibrate() {
        final double[] scores = new double[NUM_SEQUENCES];
        final int numCharacters = model.numCharacters();
        for (int i=0; i<scores.length; i++) {
            final FiniteRandomSequence seq = new FiniteRandomSequence(numCharacters, SEQUENCE_LENGTH);
            final LocalSearch s = new LocalSearch(model, baseModel, seq);
            scores[i] = s.sum();
        }

        calibrate(scores);
    }

    private void calibrate(double[] randomScores) {
        final Stats stats = new Stats(randomScores);
        lambda = Math.PI/Math.sqrt(6 * stats.getVariance());

        final double mu = stats.getMean() - 0.57722/lambda;
        k = Math.exp(lambda*mu)/SEQUENCE_LENGTH;
    }

    public double specificity(LocalSearch s) {
        return -Math.expm1(-Math.exp(Math.log(k*s.getSequence().length()) - lambda*s.sum()));
    }

    public double getLambda() {
        return lambda;
    }

    public double getK() {
        return k;
    }

}
