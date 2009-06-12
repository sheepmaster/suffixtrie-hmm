package de.blacksheepsoftware.t9;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 * 
 */
public abstract class UpdateStrategy {

    /**
     * <p>Updates the model {@code m} with the inferred state transitions when
     * reading {@code word} from index {@code i} to {@code j}.</p>
     * 
     * <p>It is assumed that {@code word} ends with a sentinel character ({@code StateDistribution.INVALID}).</p>
     * 
     * @param m The Model.
     * @param word The word to learn.
     * @param alpha_i alpha[i], i.e. the forward message at index {@code i}.
     * @param beta_j beta[j], i.e. the backwards message at index {@code j}.
     * @param i
     * @param j
     * @return beta[i], i.e. the backwards message at index {@code i};
     */
    protected StateDistribution learn(Model m, int[] word, StateDistribution alpha_i, StateDistribution beta_j, int i, int j) {
        final int c = (i < word.length) ? word[i] : StateDistribution.INVALID;
        final StateDistribution alpha_i1 = alpha_i.alpha(m, c);
        final double scalingFactor = 1 / alpha_i1.totalProbability();
        alpha_i1.scale(scalingFactor);
        final StateDistribution beta_i1 = (i + 1 >= j) ? beta_j : learn(m, word, alpha_i1, beta_j, i + 1, j);
        beta_i1.scale(scalingFactor);
        alpha_i.update(m, beta_i1, c);
        final StateDistribution beta_i = alpha_i.beta(beta_i1, c);
        return beta_i;
    }

}
