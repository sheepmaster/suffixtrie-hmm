package de.blacksheepsoftware.t9;

import junit.framework.TestCase;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class TextTest extends TestCase {

    protected Text text;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        text = new Text(null);
    }

    /*
     * Test method for 'de.blacksheepsoftware.t9.Text.findWordStart(int)'
     */
    public void testFindWordBoundaries() {
        text.text.append("asdf muuh");
        assertEquals(0, text.findWordStart(2));
        assertEquals(4, text.findWordEnd(2));

        assertEquals(5, text.findWordStart(5));
        assertEquals(9, text.findWordEnd(5));
        assertEquals(0, text.findWordStart(4));
        assertEquals(4, text.findWordEnd(4));
    }

}
