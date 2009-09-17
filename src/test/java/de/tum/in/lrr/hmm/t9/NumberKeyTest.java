package de.tum.in.lrr.hmm.t9;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class NumberKeyTest extends TestCase {

    /*
     * Test method for 'de.blacksheepsoftware.t9.NumberKey.intArrayForString(String)'
     */
    public void testIntArrayForString() {
        assertTrue(Arrays.equals(new byte[]{1, 2, 3, 4}, NumberKey.sequenceForWord("aBcD").charSequence()));
    }

    public void testIntForChar() {
        assertEquals(1, NumberKey.byteForChar('a'));
        assertEquals(26, NumberKey.byteForChar('z'));
    }

}
