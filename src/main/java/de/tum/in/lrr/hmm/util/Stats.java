package de.tum.in.lrr.hmm.util;

/**
 * Calculates sample mean and variance for an array of double values using a compensated two-pass algorithm.
 * 
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class Stats {

    protected final double mean;

    protected final double variance;

    public Stats(double[] values) {
        double sum1 = 0;
        for (double v : values) {
            sum1 += v;
        }
        mean = sum1/values.length;

        double sum2 = 0;
        double sumc = 0;
        for (double v : values) {
            final double diff = (v - mean);
            sum2 += diff*diff;
            sumc += diff;
        }
        variance = (sum2 - sumc*sumc/values.length)/(values.length-1);
    }

    /**
     * @return the mean
     */
    public double getMean() {
        return mean;
    }

    /**
     * @return the variance
     */
    public double getVariance() {
        return variance;
    }

}
