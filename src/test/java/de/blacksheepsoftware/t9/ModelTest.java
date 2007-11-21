package de.blacksheepsoftware.t9;

import junit.framework.TestCase;

public class ModelTest extends TestCase {

	protected static final String words[] = new String[]{"foo", "bar", "baz", "blurp", "foobie", "bletch", "klaus", "haus", "maus", "abracadabra", "hokus", "pokus"};
	
	protected Model model;
	
	public void setUp() throws Exception {
		Trie trie = new Trie();
		for (int i=0; i<words.length; i++) {
			trie.learn(words[i]);
		}
		model = new Model(trie);
	}
	/*
	 * Test method for 'de.blacksheepsoftware.t9.Model.count(Trie, IntRef)'
	 */
	public void testProbs() {
		for (int i=0; i<words.length; i++) {
			System.err.println(words[i]+": "+model.push(words[i])/words[i].length());
			model.pop(words[i].length());
//			System.err.println("Model: <"+model+">");
			assertTrue("empty", model.text().isEmpty());
		}
	}

}
