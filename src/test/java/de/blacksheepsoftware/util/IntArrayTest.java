package de.blacksheepsoftware.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.blacksheepsoftware.util.IntArray;

import junit.framework.TestCase;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class IntArrayTest extends TestCase {

    /**
     * Test method for {@link de.blacksheepsoftware.util.IntArray#forList(java.lang.Iterable)}.
     */
    public void testIntArray() {
        final List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        final int[] expected = new int[]{1, 2, 3, 4, 5};
        assertTrue(Arrays.equals(expected, IntArray.forList(list)));
    }

    public void testEmptyArray() {
        final List<Integer> list = Collections.emptyList();
        final int[] expected = new int[]{};
        assertTrue(Arrays.equals(expected, IntArray.forList(list)));
    }

    public void testLongArray() {
        final List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17);
        final int[] expected = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
        assertTrue(Arrays.equals(expected, IntArray.forList(list)));
    }
}
