package de.blacksheepsoftware.t9;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class WordTest extends TestCase {

    protected Model model;

    /**
     * @param name
     */
    public WordTest(String name) {
        super(name);

        model = new Model(26, 5, Model.Variant.PARTIAL_BACKLINKS);

        final UpdateStrategy strategy = new HybridUpdateStrategy();
        model.learn(NumberKey.intArrayForString("foo"), strategy);
        model.learn(NumberKey.intArrayForString("bar"), strategy);
        model.learn(NumberKey.intArrayForString("baz"), strategy);
        model.learn(NumberKey.intArrayForString("blurp"), strategy);
        model.learn(NumberKey.intArrayForString("abracadabra"), strategy);
        model.learn(NumberKey.intArrayForString("hokuspokus"), strategy);
    }

    /**
     * Test method for {@link de.blacksheepsoftware.t9.Word#completions(de.blacksheepsoftware.t9.StateDistribution, java.util.List)}.
     */
    public void testCompletions() {
        List<Word> completions = Word.completions(model.startingDistribution(), NumberKey.numberKeysForString("bar"));

        System.err.println("Completions for \"bar\": "+Arrays.toString(completions.toArray()));
    }

    /**
     * Test method for {@link de.blacksheepsoftware.t9.Word#completions(de.blacksheepsoftware.t9.StateDistribution, java.util.List, int)}.
     */
    public void testLimitedCompletions() {
        List<Word> words = Word.completions(model.startingDistribution(), NumberKey.numberKeysForString("bar"), 1);

        System.err.println("Best completion for \"bar\": "+words.get(0));
    }

}
