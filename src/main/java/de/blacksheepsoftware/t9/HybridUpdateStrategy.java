package de.blacksheepsoftware.t9;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class HybridUpdateStrategy extends UpdateStrategy {

    protected static final int DEFAULT_THRESHOLD = 127;
    protected final int linearThreshold;

    public HybridUpdateStrategy() {
        this(DEFAULT_THRESHOLD);
    }

    public HybridUpdateStrategy(int threshold) {
        linearThreshold = threshold;
    }

    @Override
    protected StateDistribution learn(Model m, int[] word, StateDistribution alpha_start, StateDistribution beta_end, int start, int end) {
        final int diff = end - start;
        if (diff > linearThreshold) {
            final int mid = start + diff / 2;
            StateDistribution alpha_mid = alpha_start;
            for (int i = start; i < mid; i++) {
                assert i < word.length;
                alpha_mid = alpha_mid.alpha(m, word[i]);
                alpha_mid.normalize();
            }
            final StateDistribution beta_mid = super.learn(m, word, alpha_mid, beta_end, mid, end);

            return (start >= mid) ? beta_mid : super.learn(m, word, alpha_start, beta_mid, start, mid);
        } else {
            return super.learn(m, word, alpha_start, beta_end, start, end);
        }
    }



}
