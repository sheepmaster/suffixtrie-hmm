package de.blacksheepsoftware.hmm;

import de.blacksheepsoftware.t9.NumberKey;
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
     * Test method for {@link de.blacksheepsoftware.hmm.BatchTrainer#learn(int[], int, int)}.
     */
    public void testLearn() {
        final Model m = new Model(Alphabet.ABC, 5, Model.Variant.PARTIAL_BACKLINKS);

        final Sequence[] words = {
                NumberKey.sequenceForWord("foo"),
                NumberKey.sequenceForWord("bar"),
                NumberKey.sequenceForWord("baz"),
                NumberKey.sequenceForWord("blurp"),
                NumberKey.sequenceForWord("abracadabra"),
                NumberKey.sequenceForWord("hokuspokus")
        };

        for (Sequence w : words) {
            m.learn(w);
        }

        final BatchTrainer trainer = new BatchTrainer(m);

        for (Sequence w : words) {
            trainer.learn(w);
        }

        trainer.finishBatch();

    }

}
