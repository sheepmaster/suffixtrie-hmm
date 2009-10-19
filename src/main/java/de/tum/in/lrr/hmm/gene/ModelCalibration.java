package de.tum.in.lrr.hmm.gene;

import de.tum.in.lrr.hmm.ISequence;
import de.tum.in.lrr.hmm.Model;
import de.tum.in.lrr.hmm.SequenceIterable;
import de.tum.in.lrr.hmm.util.Stats;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class ModelCalibration {

    /**
     * 
     */
    private static final double LOG_2 = Math.log(2);
    public static final int DEFAULT_NUM_SEQUENCES = 1000;
    public static final int DEFAULT_SEQUENCE_LENGTH = 4000;

    private static final double DELTA = 0.000001;

    protected double lambda;
    protected double k;

    protected final Model model;
    protected final SequenceIterable baseModel;

    public ModelCalibration(Model m, SequenceIterable baseModel) {
        this(m, baseModel, DEFAULT_NUM_SEQUENCES, DEFAULT_SEQUENCE_LENGTH, false);
    }

    public ModelCalibration(Model m, SequenceIterable baseModel, int numSequences, int sequenceLength, boolean calibrateLambda) {
        this.model = m;
        this.baseModel = baseModel;

        calibrate(numSequences, sequenceLength, calibrateLambda);
    }

    private void calibrate(int numSequences, int sequenceLength, boolean calibrateLambda) {
        final double[] scores = new double[numSequences];
        for (int i=0; i<scores.length; i++) {
            final ISequence seq = new RandomSequence(model.getAlphabet()).generateSequence(sequenceLength);
            final ScoredSequence s = ScoredSequence.search(model, baseModel, seq);
            scores[i] = s.score();
        }

        calibrateDirect(scores, sequenceLength, calibrateLambda);
    }

    private static double mu(double[] randomScores, double lambda) {
        double esum = 0;
        for (double s : randomScores) {
            esum += Math.exp(-lambda * s);
        }
        return -Math.log(esum/randomScores.length) / lambda;
    }

    private static double lambdaML(double[] randomScores, double lambda) {
        int iteration = 0;
        final int n = randomScores.length;
        while (true) {
            double xsum = 0;
            double esum = 0;
            double xesum = 0;
            double xxesum = 0;
            for (double x : randomScores) {
                xsum += x;
                final double e = Math.exp(-lambda*x);
                esum += e;
                xesum += x*e;
                xxesum += x*x*e;
            }

            final double tmp = xesum/esum;
            final double fx = 1/lambda - xsum/n + tmp;
            if (Math.abs(fx) < DELTA) {
                return lambda;
            }
            final double dfx = tmp*tmp - xxesum/esum - 1/(lambda*lambda);

            lambda -= fx/dfx;
            System.err.println("iteration "+(++iteration));
        }
    }

    private void calibrateDirect(double[] randomScores, int sequenceLength, boolean calibrateLambda) {
        if (calibrateLambda) {
            final Stats stats = new Stats(randomScores);
            lambda = Math.PI/Math.sqrt(6 * stats.getVariance());
            //        lambda = lambdaML(randomScores, lambdaStart);
        } else {
            lambda = 1.0;
        }

        final double mu = mu(randomScores, lambda);
        k = Math.exp(lambda*mu)/sequenceLength;
    }

    public double normalizedScore(ScoredSequence s) {
        return lambda*s.score() - Math.log(k*s.getContainingSequence().length());
    }

    public double bitScore(ScoredSequence s) {
        return normalizedScore(s) / LOG_2;
    }

    public double eValue(ScoredSequence s) {
        // return Math.exp(-normalizedScore(s));
        return k*s.getContainingSequence().length()*Math.exp(-lambda*s.score());
    }

    public double pValue(ScoredSequence s) {
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
