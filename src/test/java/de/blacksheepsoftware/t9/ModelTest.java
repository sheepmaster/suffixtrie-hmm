package de.blacksheepsoftware.t9;

import static de.blacksheepsoftware.t9.Model.BACK;
import static de.blacksheepsoftware.t9.Model.BOTTOM;
import static de.blacksheepsoftware.t9.Model.EPSILON;
import junit.framework.TestCase;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class ModelTest extends TestCase {

    protected Model model;

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        model = new Model(26, 5, Model.Variant.PARTIAL_BACKLINKS);

        model.learn(NumberKey.intArrayForString("foo"));
        model.learn(NumberKey.intArrayForString("bar"));
        model.learn(NumberKey.intArrayForString("baz"));
        model.learn(NumberKey.intArrayForString("blurp"));
        model.learn(NumberKey.intArrayForString("abracadabra"));
        model.learn(NumberKey.intArrayForString("hokuspokus"));

    }

    protected void printWordPerplexity(String word) {
        final double perplexity = model.perplexity(NumberKey.characterSequenceForString(word));
        assertFalse("perplexity is not a number", Double.isNaN(perplexity));
        //        assertFalse(Double.isInfinite(perplexity));
        System.err.println("perplexity for \""+word+"\": "+perplexity);
    }

    /*
     * Test method for 'de.blacksheepsoftware.t9.Model.perplexity(int[])'
     */
    public void testWordPerplexity() {
        printWordPerplexity("foo");
        printWordPerplexity("bar");
        printWordPerplexity("foobar");
        printWordPerplexity("zyx");
    }

    protected void printCompletionPerplexity(String prefix, String word) {
        final double perplexity = model.perplexity(NumberKey.characterSequenceForString(word), NumberKey.characterSequenceForString(prefix));
        assertFalse("perplexity is not a number", Double.isNaN(perplexity));
        System.err.println("perplexity for \""+word+"\" after \""+prefix+"\": "+perplexity);
    }

    /*
     * Test method for 'de.blacksheepsoftware.t9.Model.perplexity(int[], int[])'
     */
    public void testCompletionPerplexity() {
        printCompletionPerplexity("foo", "bar");
        printCompletionPerplexity("hokus", "pokus");
    }

    protected void checkOutputDistribution(String prefix) {
        final StateDistribution dist = model.startingDistribution().successor(NumberKey.characterSequenceForString(prefix));
        dist.normalize();
        double totalProbability = 0;
        for (int c=1; c<=model.numCharacters; c++) {
            final StateDistribution successor = dist.successor(c);
            final double totalOutputProbability = successor.totalProbability();
            double outputProbability = 0.0;
            int[] states = dist.states();
            for (int d=dist.depth(); d >= 0; d--) {
                outputProbability += dist.stateProbability(d) * transitionProbability(states[d], c);
            }
            assertEquals(outputProbability, totalOutputProbability, 0.001);
            totalProbability += totalOutputProbability;
        }
        //        System.err.println("output probability sum for \""+prefix+"\": "+totalProbability);
        assertEquals("valid output distribution for \""+prefix+"\"", 1.0, totalProbability, 0.001);
    }

    public void testOutputDistribution() {
        checkOutputDistribution("");
        checkOutputDistribution("foo");
        checkOutputDistribution("bar");
        checkOutputDistribution("foobar");
        //        checkOutputDistribution("zyx");
    }

    protected double transitionProbability(int state, int c) {
        if (state == BOTTOM) {
            return 0.0;
        }
        final double p = model.frequencies[state][c];

        return (p + model.frequencies[state][BACK] * transitionProbability(model.transitions[state][BACK], c))
        / model.frequencySums[state];
    }

    protected double transitionProbabilitySum(int state) {
        if (state == BOTTOM) {
            return 42;
        }
        double p = 0.0;
        for (int c=1; c<=model.numCharacters; c++) {
            p += model.frequencies[state][c];
        }
        p += model.frequencies[state][BACK] * transitionProbabilitySum(model.transitions[state][BACK]);
        return p/model.frequencySums[state];
    }

    /*
     * Test method for 'de.blacksheepsoftware.t9.Model.learn(int[])'
     */
    public void testLearn() {
        assertEquals(BOTTOM, model.transitions[BOTTOM][BACK]);
        for (int c=1; c<=model.numCharacters; c++) {
            assertEquals(EPSILON, model.transitions[BOTTOM][c]);
        }
        //        assertEquals(0, model.frequencies[BOTTOM][BACK]);
        assertEquals(0.0, model.frequencySums[BOTTOM], 0.0001);
        for (int i=EPSILON; i<model.transitions.length; i++) {
            if (i < model.numNodes) {
                final int b = model.transitions[i][BACK];
                assertEquals((i == EPSILON), (b == BOTTOM));

                double freq = model.frequencies[i][BACK];

                double p = 0.0;

                for (int c=1; c<=model.numCharacters; c++) {
                    p += transitionProbability(i, c);
                    freq += model.frequencies[i][c];
                    final int t = model.transitions[i][c];
                    if (t != BOTTOM) {
                        assertEquals(model.transitions[t][BACK], model.transitions[b][c]);
                    }
                }

                assertEquals(freq, model.frequencySums[i], 0.0001);
                assertEquals(1.0, p, 0.01);
                assertEquals(1.0, transitionProbabilitySum(i), 0.01);
            } else {
                assertNull(model.transitions[i]);
                assertNull(model.frequencies[i]);
                assertEquals(0.0, model.frequencySums[i], 0.0001);
            }
        }
    }

}
