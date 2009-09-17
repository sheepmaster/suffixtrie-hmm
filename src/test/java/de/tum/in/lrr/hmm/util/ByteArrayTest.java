package de.tum.in.lrr.hmm.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class ByteArrayTest extends TestCase {

    /**
     * Test method for {@link de.tum.in.lrr.hmm.util.ByteBuffer#forList(java.lang.Iterable)}.
     */
    public void testIntArray() {
        final List<Byte> list = Arrays.asList((byte)1, (byte)2, (byte)3, (byte)4, (byte)5);
        final byte[] expected = new byte[]{1, 2, 3, 4, 5};
        assertTrue(Arrays.equals(expected, ByteBuffer.forList(list.iterator())));
    }

    public void testEmptyArray() {
        final List<Byte> list = Collections.emptyList();
        final byte[] expected = new byte[]{};
        assertTrue(Arrays.equals(expected, ByteBuffer.forList(list.iterator())));
    }

    public void testLongArray() {
        final List<Byte> list = Arrays.asList((byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6, (byte)7, (byte)8, (byte)9, (byte)10, (byte)11, (byte)12, (byte)13, (byte)14, (byte)15, (byte)16, (byte)17);
        final byte[] expected = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
        assertTrue(Arrays.equals(expected, ByteBuffer.forList(list.iterator())));
    }
}
