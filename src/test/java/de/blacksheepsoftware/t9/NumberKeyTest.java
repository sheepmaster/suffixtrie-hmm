package de.blacksheepsoftware.t9;

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
        assertTrue(Arrays.equals(new int[]{1, 2, 3, 4}, NumberKey.intArrayForString("aBcD")));
    }
    
    public void testIntForChar() {
        assertEquals(1, NumberKey.intForChar('a'));
        assertEquals(26, NumberKey.intForChar('z'));
    }

}
