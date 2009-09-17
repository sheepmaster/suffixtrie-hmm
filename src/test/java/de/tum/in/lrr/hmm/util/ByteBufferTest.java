package de.tum.in.lrr.hmm.util;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class ByteBufferTest extends TestCase {

    /**
     * Test method for {@link de.tum.in.lrr.hmm.util.ByteBuffer#forList(java.lang.Iterable)}.
     */
    public void testIntArray() {
        ByteBuffer buf = new ByteBuffer();
        buf.append((byte) 1);
        buf.append((byte) 2);
        buf.append((byte) 3);
        buf.append((byte) 4);
        buf.append((byte) 5);
        final byte[] expected = new byte[]{1, 2, 3, 4, 5};
        assertTrue(Arrays.equals(expected, buf.toByteArray()));
    }

    public void testEmptyArray() {
        ByteBuffer buf = new ByteBuffer();
        final byte[] expected = new byte[]{};
        assertTrue(Arrays.equals(expected, buf.toByteArray()));
    }

    public void testLongArray() {
        ByteBuffer buf = new ByteBuffer();
        buf.append((byte) 1);
        buf.append((byte) 2);
        buf.append((byte) 3);
        buf.append((byte) 4);
        buf.append((byte) 5);
        buf.append((byte) 6);
        buf.append((byte) 7);
        buf.append((byte) 8);
        buf.append((byte) 9);
        buf.append((byte) 10);
        buf.append((byte) 11);
        buf.append((byte) 12);
        buf.append((byte) 13);
        buf.append((byte) 14);
        buf.append((byte) 15);
        buf.append((byte) 16);
        buf.append((byte) 17);
        final byte[] expected = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
        assertTrue(Arrays.equals(expected, buf.toByteArray()));
    }
}
