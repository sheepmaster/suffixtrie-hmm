package de.tum.in.lrr.hmm;

import junit.framework.TestCase;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class AlphabetTest extends TestCase {

    /**
     * Test method for {@link de.tum.in.lrr.hmm.Alphabet#indexOfSymbol(char)}.
     */
    public void testIndexOfSymbol() {
        checkSymbolIndices("abcdef");
        try {
            checkSymbolIndices("");
        } catch (IllegalArgumentException e) {
            // do nothing
        }
        try {
            checkSymbolIndices("abcabc");
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    /**
     * @param alphabetString
     */
    protected void checkSymbolIndices(final String alphabetString) {
        final Alphabet a = new Alphabet(alphabetString);
        for (int i=1; i<alphabetString.length(); i++) {
            assertEquals(i, a.indexOfSymbol(alphabetString.charAt(i)));
        }
    }

}
