package de.blacksheepsoftware.t9;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class UpdateStrategyTest extends TestCase {

    /**
     * @param name
     */
    public UpdateStrategyTest(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for {@link de.blacksheepsoftware.t9.UpdateStrategy#learn(de.blacksheepsoftware.t9.Model, int[], de.blacksheepsoftware.t9.StateDistribution)}.
     */
    public void testUpdateStrategy() {
        final String[] words = {"foo", "bar", "baz", "blurp", "abracadabra", "hokuspokus"};

        final Model model1 = new Model(26, 5, Model.Variant.PARTIAL_BACKLINKS);
        final Model model2 = new Model(26, 5, Model.Variant.PARTIAL_BACKLINKS);

        final HybridUpdateStrategy linearStrategy = new HybridUpdateStrategy(Integer.MAX_VALUE);
        final HybridUpdateStrategy divideAndConquerStrategy = new HybridUpdateStrategy(0);

        for (String w : words) {
            model1.learn(NumberKey.intArrayForString(w), linearStrategy);
            model2.learn(NumberKey.intArrayForString(w), divideAndConquerStrategy);
        }


        assertTrue(Arrays.deepEquals(model1.transitions, model2.transitions));
        assertTrue(Arrays.deepEquals(model1.frequencies, model2.frequencies));
        assertTrue(Arrays.equals(model1.frequencySums, model2.frequencySums));
    }

}
