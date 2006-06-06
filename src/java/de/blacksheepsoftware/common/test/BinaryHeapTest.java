package de.blacksheepsoftware.common.test;

import de.blacksheepsoftware.common.*;
import junit.framework.TestCase;

public class BinaryHeapTest extends TestCase {

	/*
	 * Test method for 'de.blacksheepsoftware.common.BinaryHeap.add(Comparable)'
	 */
	public void testAddRemove() {
		PriorityQueue pq = new BinaryHeap();
		pq.add(new Integer(23));
		pq.add(new Integer(9));
		pq.add(new Integer(4711));
		pq.add(new Integer(9));
		assertEquals("4711", new Integer(4711), pq.remove());
		pq.add(new Integer(18));
		assertEquals("23", new Integer(23), pq.remove());
		pq.add(new Integer(101));
		pq.add(new Integer(42));
		assertEquals("101", new Integer(101), pq.remove());
		pq.add(new Integer(3));
		assertEquals("42", new Integer(42), pq.remove());
		assertEquals("18", new Integer(18), pq.remove());
		assertEquals("9", new Integer(9), pq.remove());
		assertEquals("9", new Integer(9), pq.remove());
		assertEquals("3", new Integer(3), pq.remove());
		assertTrue("isEmpty", pq.isEmpty());
	}

}
