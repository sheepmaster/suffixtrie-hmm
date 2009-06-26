package de.blacksheepsoftware.t9;

import junit.framework.TestCase;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class BatchTrainerTest extends TestCase {

    /**
     * @param name
     */
    public BatchTrainerTest(String name) {
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
     * Test method for {@link de.blacksheepsoftware.t9.BatchTrainer#learn(int[], int, int)}.
     */
    public void testLearn() {
        final Model m = new Model(26, 5, Model.Variant.PARTIAL_BACKLINKS);

        final String[] words = {"foo", "bar", "baz", "blurp", "abracadabra", "hokuspokus"};

        for (String w : words) {
            m.learn(NumberKey.intArrayForString(w));
        }

        final BatchTrainer trainer = new BatchTrainer(m);

        for (String w : words) {
            trainer.learn(NumberKey.intArrayForString(w));
        }

        trainer.finishBatch();

    }

}
