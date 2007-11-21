package de.blacksheepsoftware.t9;

import java.util.*;

import de.blacksheepsoftware.common.BinaryHeap;

import junit.framework.TestCase;

public class TextTest extends TestCase {

	protected Text text;
	
	public void setUp() {
		String words[] = ModelTest.words;
		Trie trie = new Trie();
		for (int i=0; i<words.length; i++) {
			trie.learn(words[i]);
		}
		text = new Text(trie);
	}
	
	/*
	 * Test method for 'de.blacksheepsoftware.t9.Text.merge(CompList[])'
	 */
	public void testMerge() {
		Text.CompList[] lists = new Text.CompList[]{new Text.CompList(), new Text.CompList(), new Text.CompList(), new Text.CompList(), new Text.CompList()};
		lists[0].add(new Integer(10));
		lists[0].add(new Integer(8));
		lists[0].add(new Integer(6));
		lists[0].add(new Integer(4));
		lists[0].add(new Integer(2));
		lists[1].add(new Integer(18));
		lists[1].add(new Integer(15));
		lists[1].add(new Integer(12));
		lists[1].add(new Integer(9));
		lists[1].add(new Integer(3));
		lists[2].add(new Integer(35));
		lists[2].add(new Integer(30));
		lists[2].add(new Integer(25));
		lists[2].add(new Integer(20));
		lists[2].add(new Integer(5));
		lists[3].add(new Integer(28));
		lists[3].add(new Integer(21));
		lists[3].add(new Integer(14));
		lists[3].add(new Integer(7));
		lists[4].add(new Integer(55));
		lists[4].add(new Integer(44));
		lists[4].add(new Integer(33));
		lists[4].add(new Integer(22));
		lists[4].add(new Integer(11));
		SortedSet s = new TreeSet(BinaryHeap.reverseComp);
		for (int i=0; i<lists.length; i++) {
			s.addAll(lists[i]);
		}
		
		Iterator it1 = s.iterator();
		Iterator it2 = Text.merge(lists).iterator(); 
		while (it1.hasNext()) {
//			System.err.println("expected: "+it1.next()+"; got: "+it2.next());
			assertEquals(it1.next(), it2.next());
		}
	}

	public void testEdit() {
		text.delete();
		text.insertNumberKey(new NumberKey(2));
		text.insertNumberKey(new NumberKey(3));
		text.insertNumberKey(new NumberKey(4));
		text.nextWord();
		text.nextWord();
		text.insertChar(' ');
		text.insertNumberKey(new NumberKey(5));
		text.insertNumberKey(new NumberKey(6));
		text.insertNumberKey(new NumberKey(7));
		text.moveLeft();
		text.moveLeft();
		text.moveLeft();
		text.delete();
		text.moveLeft();
		text.previousWord();
		text.moveRight();
		text.moveRight();
		
		text.finishWord();
		System.err.println("["+text+"]");
	}


}
