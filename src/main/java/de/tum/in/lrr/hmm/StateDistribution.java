package de.tum.in.lrr.hmm;

import static de.tum.in.lrr.hmm.Model.BACK;
import static de.tum.in.lrr.hmm.Model.BOTTOM;
import static de.tum.in.lrr.hmm.Model.EPSILON;

import java.io.Serializable;

import de.tum.in.lrr.hmm.Model.Variant;

public abstract class StateDistribution implements Serializable {
    protected static final double ALMOST_ONE = 0.99999999999999;

    private static final long serialVersionUID = -366668114489984302L;

    public static final double LOG_2 = Math.log(2);

    public static final int INVALID = -1;

    protected final Model model;

    protected final int longestSuffix;

    protected double[] stateProbabilities;

    protected StateDistribution(Model m, int suffix, double[] probabilities) {
        model = m;
        longestSuffix = suffix;
        stateProbabilities = probabilities;
    }

    public static StateDistribution create(Model model, Variant v) {
        if (v.equals(Variant.PARTIAL_BACKLINKS)) {
            return new PartialBacklinksVariant(model, EPSILON, new double[0]);
        } else {
            throw new IllegalArgumentException("Variant not implemented");
        }
    }

    protected abstract StateDistribution copy(int suffix, double[] probabilities);

    protected abstract Variant getVariant();

    public int depth() {
        return stateProbabilities.length;
    }

    /**
     * @return an array of possible states, starting with the root state.
     */
    public int[] states() {
        int depth = depth();
        int[] states = new int[depth + 1];
        int state = longestSuffix;
        int d = depth;
        while (d >= 0) {
            states[d--] = state;
            state = model.transitions[state][BACK];
        }
        return states;
    }

    public double totalProbability() {
        //        if (stateProbabilities.length == 0) {
        //            return 1.0;
        //        }
        double total = 0;
        for (double p : stateProbabilities) {
            total += p;
        }
        return total;
    }

    public double normalize() {
        final double normalizingFactor = 1 / totalProbability();
        assert ((stateProbabilities.length == 0) || !Double.isInfinite(normalizingFactor)) : "total probability is zero";
        assert !Double.isNaN(normalizingFactor) : "total probability is not a number";
        assert normalizingFactor >= ALMOST_ONE : "total probability is not between zero and one";
        scale(normalizingFactor);
        return Math.log(normalizingFactor);
    }

    protected void scale(double scalingFactor) {
        for (int i = 0; i < depth(); i++) {
            stateProbabilities[i] *= scalingFactor;
        }
    }

    /**
     * @param depth
     * @return
     */
    public double stateProbability(int depth) {
        if (depth > 0) {
            return stateProbabilities[depth - 1];
        } else if (longestSuffix != EPSILON) {
            // the probability for being in the root state is zero, except
            // for the starting distribution, where no character has been
            // read yet.
            return 0;
        } else {
            return 1;
        }
    }

    public StateDistribution successor(byte character) {
        if (character == BACK) {
            return model.startingDistribution();
        }
        return alpha(null, character, Integer.MAX_VALUE, false);
    }

    public StateDistribution successor(Iterable<Byte> characters) {
        StateDistribution newDistribution = this;
        for (byte c : characters) {
            newDistribution = newDistribution.successor(c);
            newDistribution.normalize();
        }
        return newDistribution;
    }

    /**
     * @param word
     * @return The perplexity (in <strong>bits</strong>) of {@code word}.
     */
    public double perplexity(Iterable<Byte> word) {
        double p = 0;
        StateDistribution newDistribution = this;
        for (final byte c : word) {
            newDistribution = newDistribution.successor(c);
            if (c == BACK) {
                p += Math.log(model.numCharacters());
            } else {
                p += newDistribution.normalize();
            }
        }
        return p / LOG_2;
    }

    protected abstract StateDistribution beta(StateDistribution alpha, byte character, boolean useSmoothing);

    protected abstract StateDistribution alpha(Model m, byte character, int maxDepth, boolean useSmoothing);

    protected abstract void update(Model m, StateDistribution beta, byte character);

    protected int successorState(int state, int character) {
        final int newState = model.transitions[state][character];
        if (newState != BOTTOM) {
            return newState;
        } else if (state == EPSILON) {
            return EPSILON;
        } else {
            return successorState(model.transitions[state][BACK], character);
        }
    }

    protected void checkExpectedSuffix(StateDistribution beta, int character) {
        int expectedLongestSuffix;
        if (character == INVALID) {
            expectedLongestSuffix = EPSILON;
        } else {
            expectedLongestSuffix = successorState(longestSuffix, character);
        }

        while (expectedLongestSuffix != BOTTOM) {
            if (beta.longestSuffix == expectedLongestSuffix) {
                return;
            }
            expectedLongestSuffix = model.transitions[expectedLongestSuffix][BACK];
        }
        throw new IllegalArgumentException("Invalid successor state");
    }

    public static class PartialBacklinksVariant extends StateDistribution {

        private static final long serialVersionUID = 1L;

        protected PartialBacklinksVariant(Model m, int suffix, double[] probabilities) {
            super(m, suffix, probabilities);
        }

        @Override
        protected StateDistribution copy(int suffix, double[] probabilities) {
            return new PartialBacklinksVariant(model, suffix, probabilities);
        }

        @Override
        protected StateDistribution beta(StateDistribution oldBeta, byte character, boolean useSmoothing) {
            checkExpectedSuffix(oldBeta, character);
            final int depth = depth();
            double[] newProbs = new double[depth];
            int[] states = states();
            double p = 0;
            for (int i = 0; i <= depth; i++) {
                final int state = states[i];
                final double transitionPseudoCount;
                final double backPseudoCount;
                if (!useSmoothing) {
                    backPseudoCount = 0;
                    transitionPseudoCount = 0;
                } else if (state == EPSILON) {
                    transitionPseudoCount = 1;
                    backPseudoCount = 0;
                } else {
                    transitionPseudoCount = stateProbability(i) / 2;
                    backPseudoCount = transitionPseudoCount;
                }
                p *= (model.frequencies[state][BACK] + backPseudoCount);
                if (i < oldBeta.stateProbabilities.length) {
                    p += oldBeta.stateProbabilities[i] * (model.frequencies[state][character] + transitionPseudoCount);
                }
                p /= (model.frequencySums[state] + backPseudoCount + transitionPseudoCount);
                if (i > 0) {
                    newProbs[i - 1] = p;
                } else if (character == -1) {
                    p = 1;
                }
            }

            return copy(longestSuffix, newProbs);
        }

        @Override
        protected StateDistribution alpha(Model m, byte character, int maxDepth, boolean useSmoothing) {
            if (character == BACK) {
                throw new IllegalArgumentException("Illegal character");
            }
            if (character == INVALID) {
                return model.startingDistribution();
            }
            double[] newProbs = null;
            int newLongestSuffix = BOTTOM;
            int state = longestSuffix;
            int depth = depth();
            double p = 0;
            while (state != BOTTOM) {
                final double currentProbability = stateProbability(depth);

                p += currentProbability;
                final double transitionPseudoCount;
                final double backPseudoCount;
                if (!useSmoothing) {
                    transitionPseudoCount = 0;
                    backPseudoCount = 0;
                } else if (state == EPSILON) {
                    transitionPseudoCount = 1;
                    backPseudoCount = 0;
                } else {
                    transitionPseudoCount = currentProbability / 2;
                    backPseudoCount = transitionPseudoCount;
                }
                int t = model.transitions[state][character];
                if ((t == BOTTOM) && useSmoothing && (depth < maxDepth)) {
                    // if we are in training mode, add the missing state
                    t = model.addNode(state, character);
                    m.addNode(state, character);
                }
                final double frequencySum = model.frequencySums[state] + transitionPseudoCount + backPseudoCount;
                assert frequencySum > 0;
                if (t != BOTTOM) {
                    final double newProb = p * (model.frequencies[state][character] + transitionPseudoCount) / frequencySum;
                    if (newProb > 0) {
                        if (newProbs == null) {
                            newProbs = new double[depth + 1];
                            newLongestSuffix = t;
                        }
                        newProbs[depth] = newProb;
                        assert !Double.isNaN(newProb);
                        //                    } else {
                        //                        System.err.println("muuh");
                    }
                }
                depth--;
                p *= (model.frequencies[state][BACK] + backPseudoCount) / frequencySum;
                assert !Double.isNaN(p);
                state = model.transitions[state][BACK];
            }
            if (newProbs == null) {
                newProbs = new double[0];
            }
            return copy(newLongestSuffix, newProbs);
        }

        @Override
        protected void update(Model m, StateDistribution beta, byte character) {
            checkExpectedSuffix(beta, character);
            int depth = depth();
            int[] states = new int[depth + 1];
            double[] incoming = new double[depth + 1];
            {
                double p = 0; // probability for entering the state; saved in incoming
                int state = longestSuffix;
                while (depth >= 0) {
                    final double currentProbability = stateProbability(depth);
                    p += currentProbability;

                    final double transitionPseudoCount;
                    final double backPseudoCount;
                    if (state == EPSILON) {
                        transitionPseudoCount = 1;
                        backPseudoCount = 0;
                    } else {
                        transitionPseudoCount = currentProbability / 2;
                        backPseudoCount = transitionPseudoCount;
                    }

                    incoming[depth] = p;
                    states[depth] = state;

                    depth--;
                    p *= (model.frequencies[state][BACK] + backPseudoCount)
                    / (model.frequencySums[state] + transitionPseudoCount + backPseudoCount);
                    state = model.transitions[state][BACK];
                }
            }
            double p = beta.stateProbability(0); // probability for leaving the state
            for (int i = 0; i < states.length; i++) {
                final int state = states[i];
                final double currentProbability = stateProbability(i);
                final double transitionPseudoCount;
                final double backPseudoCount;
                if (state == EPSILON) {
                    transitionPseudoCount = 1;
                    backPseudoCount = 0;
                } else {
                    transitionPseudoCount = currentProbability / 2;
                    backPseudoCount = transitionPseudoCount;
                }

                if (i > 0) {
                    // p has still the value from the last iteration, i.e. the
                    // probability for leaving the *next* state
                    p *= (model.frequencies[state][BACK] + backPseudoCount)
                    / (model.frequencySums[state] + backPseudoCount + transitionPseudoCount);
                    final double backCount = incoming[i] * p;
                    m.frequencies[state][BACK] += backCount;
                    m.frequencySums[state] += backCount;
                }

                if (i < beta.stateProbabilities.length) {
                    // probability for reading the character in this state
                    final double q = (model.frequencies[state][character] + transitionPseudoCount)
                    / (model.frequencySums[state] + backPseudoCount + transitionPseudoCount) * beta.stateProbabilities[i];
                    p += q;

                    final double readCount = incoming[i] * q;
                    m.frequencies[state][character] += readCount;
                    m.frequencySums[state] += readCount;
                }
            }
        }

        @Override
        protected Variant getVariant() {
            return Variant.PARTIAL_BACKLINKS;
        }

    }

}
