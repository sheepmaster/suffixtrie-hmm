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

        calibrateDirect(scores);
    }

    private static double mu(double[] randomScores, double lambda) {
        double esum = 0;
        for (double s : randomScores) {
            esum += Math.exp(-lambda * s);
        }
        return -Math.log(esum/randomScores.length) / lambda;
    }

    private void calibrateDirect(double[] randomScores) {
        final Stats stats = new Stats(randomScores);
        lambda = Math.PI/Math.sqrt(6 * stats.getVariance());

        //        final double mu = stats.getMean() - 0.57722/lambda;
        final double mu = mu(randomScores, lambda);
        k = Math.exp(lambda*mu)/SEQUENCE_LENGTH;
    }

    public double normalizedScore(LocalSearch s) {
        return lambda*s.sum() - Math.log(k*s.getSequence().length());
    }

    public double eValue(LocalSearch s) {
        // return Math.exp(-normalizedScore(s));
        return k*s.getSequence().length()*Math.exp(-lambda*s.sum());
    }

    public double specificity(LocalSearch s) {
        // The maximum normalized score follows a standard Gumbel distribution
        // return -Math.expm1(-Math.exp(-normalizedScore(s)));

        // The number of hits with a minimum score follows a Poisson distribution
        return -Math.expm1(-eValue(s));
    }

    public double getLambda() {
        return lambda;
    }

    public double getK() {
        return k;
    }

}
