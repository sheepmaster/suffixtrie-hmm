package de.blacksheepsoftware.t9;

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
    protected void setUp() throws Exception {
        super.setUp();
        model = new Model(26, 5);
        
        model.learn(NumberKey.intArrayForString("foo"));
        model.learn(NumberKey.intArrayForString("bar"));
        model.learn(NumberKey.intArrayForString("baz"));
        model.learn(NumberKey.intArrayForString("blurp"));
        model.learn(NumberKey.intArrayForString("abracadabra"));
        model.learn(NumberKey.intArrayForString("hokuspokus"));

    }

    protected void printWordPerplexity(String word) {
        final double perplexity = model.perplexity(NumberKey.intArrayForString(word));
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
    }

    protected void printCompletionPerplexity(String prefix, String word) {
        final double perplexity = model.perplexity(NumberKey.intArrayForString(word), NumberKey.intArrayForString(prefix));
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

    /*
     * Test method for 'de.blacksheepsoftware.t9.Model.learn(int[])'
     */
    public void testLearn() {
        for (int i=0; i<model.transitions.length; i++) {
            if (i == 0) {
                assertEquals(-1, model.transitions[i][0]);
            } else if (i >= model.numNodes) {
                assertNull(model.transitions[i]);
                assertNull(model.frequencies[i]);
                assertEquals(0.0, model.frequencySums[i]);
            } else {
                final int b = model.transitions[i][0];
                assertFalse(b == -1);
                for (int c=1; c<model.numCharacters; c++) {
                    final int t = model.transitions[i][c];
                    if (t != -1) {
                        assertEquals(model.transitions[t][0], model.transitions[b][c]);
                    }
                }
            }
        }
    }

}
