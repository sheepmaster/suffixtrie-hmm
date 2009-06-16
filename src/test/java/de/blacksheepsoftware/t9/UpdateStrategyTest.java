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

    public void testUpdateStrategy() {
        final String[] words = {"foo", "bar", "baz", "blurp", "abracadabra", "hokuspokus"};

        final Model model1 = new Model(26, 5, Model.Variant.PARTIAL_BACKLINKS);
        final Model model2 = new Model(26, 5, Model.Variant.PARTIAL_BACKLINKS);

        for (String w : words) {
            model1.learn(NumberKey.intArrayForString(w), Integer.MAX_VALUE, Integer.MAX_VALUE);
            model2.learn(NumberKey.intArrayForString(w), Integer.MAX_VALUE, 0);
        }

        assertTrue(Arrays.deepEquals(model1.transitions, model2.transitions));
        assertTrue(Arrays.deepEquals(model1.frequencies, model2.frequencies));
        assertTrue(Arrays.equals(model1.frequencySums, model2.frequencySums));
    }

}
