package de.blacksheepsoftware.t9;

/**
 * Linear recursive learning strategy. Runs in time and intermediate space <i>O(n)</i>.
 * 
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class LinearUpdateStrategy implements UpdateStrategy {

    /**
     * {@inheritDoc}
     */
    public StateDistribution learn(Model m, StateDistribution d, int[] word) {
        return learn(m, d, word, 0);
    }

    protected StateDistribution learn(Model m, StateDistribution d, int[] word, int i) {
        if (i > word.length) {
            return d;
        }
        final int c = (i < word.length) ? word[i] : StateDistribution.INVALID;
        final StateDistribution newAlpha = d.alpha(m, c);
        final double scalingFactor = 1 / newAlpha.totalProbability();
        newAlpha.scale(scalingFactor);
        final StateDistribution beta = learn(m, newAlpha, word, i + 1);
        beta.scale(scalingFactor);
        d.update(m, beta, c);
        return d.beta(beta, c);
    }

}
