package de.blacksheepsoftware.t9;

/**
 * Divide-and-conquer learning strategy.
 * 
 * Runs in time <i>O(n log(n))</i> and intermediate space <i>O(log(n))</i>.
 * 
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class DivideAndConquerUpdateStrategy extends UpdateStrategy {

    @Override
    protected StateDistribution learn(Model m, int[] word, StateDistribution alpha_start, StateDistribution beta_end, int start, int end) {
        final int mid = start + (end - start) / 2;
        StateDistribution alpha_mid = alpha_start;
        for (int i = start; i < mid; i++) {
            assert i < word.length;
            alpha_mid = alpha_mid.alpha(m, word[i]);
            alpha_mid.normalize();
        }
        final StateDistribution beta_mid = super.learn(m, word, alpha_mid, beta_end, mid, end);

        return (start >= mid) ? beta_mid : super.learn(m, word, alpha_start, beta_mid, start, mid);
    }

}
