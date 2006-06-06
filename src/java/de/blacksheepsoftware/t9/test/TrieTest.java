package de.blacksheepsoftware.t9.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.blacksheepsoftware.t9.Trie;
import junit.framework.TestCase;

public class TrieTest extends TestCase {

	protected Trie trie = new Trie();
	
	public void setUp() throws Exception {
		String words[] = new String[]{"foo", "bar", "baz", "blurp", "foobie", "bletch", "klaus", "haus", "maus", "abracadabra", "hokus", "pokus"};
		for (int i=0; i<words.length; i++) {
			trie.learn(words[i]);
		}
	}
	
	protected void assertDeepEquals(String message, Trie trie1, Trie trie2) throws Exception {
		assertEquals(message+".freq", trie1.frequency(), trie2.frequency());
		assertEquals(message+".freqSum", trie1.frequencySum(), trie2.frequencySum());
		char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
		for (int i=0; i<Trie.NUM_CHARS; i++) {
			Trie child1 = trie1.child(chars[i]);
			Trie child2 = trie2.child(chars[i]);
			if (child1 == null) {
				assertNull(child2);
				continue;
			}
			assertDeepEquals(message+chars[i], child1, child2);
		}
	}
	
	/*
	 * Test method for 'de.blacksheepsoftware.t9.Trie.learn(String)'
	 */
	public void testLearnString() {
		assertEquals("freq", 12, trie.frequency());
		assertEquals("freqSum", 72, trie.frequencySum());
		assertNotNull("child", trie.child('e'));
		assertNull("null child", trie.child('y'));
		
		Trie child = trie.child('p').child('r').child('u').child('l').child('b');
		assertEquals("child freq", 1, child.frequency());
		assertEquals("child freqSum", 1, child.frequencySum());
	}
	
	public void testSerialization() throws Exception {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		new ObjectOutputStream(buffer).writeObject(trie);
		Trie trie2 = (Trie)new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray())).readObject();
		assertDeepEquals("trie1/trie2/", trie, trie2);
	}

}
