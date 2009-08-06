package de.blacksheepsoftware.t9;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import de.blacksheepsoftware.hmm.Alphabet;
import de.blacksheepsoftware.hmm.Model;

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

        model = new Model(Alphabet.ABC, 5, Model.Variant.PARTIAL_BACKLINKS);

        model.learn(NumberKey.sequenceForWord("foo"));
        model.learn(NumberKey.sequenceForWord("bar"));
        model.learn(NumberKey.sequenceForWord("baz"));
        model.learn(NumberKey.sequenceForWord("blurp"));
        model.learn(NumberKey.sequenceForWord("abracadabra"));
        model.learn(NumberKey.sequenceForWord("hokuspokus"));
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
