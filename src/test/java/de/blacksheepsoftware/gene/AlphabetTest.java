package de.blacksheepsoftware.gene;

import junit.framework.TestCase;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class AlphabetTest extends TestCase {

    /**
     * Test method for {@link de.blacksheepsoftware.gene.Alphabet#indexOfSymbol(char)}.
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
        for (int i=0; i<alphabetString.length(); i++) {
            assertEquals(i+1, a.indexOfSymbol(alphabetString.charAt(i)));
        }
    }

}
