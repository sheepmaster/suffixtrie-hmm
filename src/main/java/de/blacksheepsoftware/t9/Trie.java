package de.blacksheepsoftware.t9;

import java.io.*;

public class Trie implements Serializable {
	
	private static final long serialVersionUID = -5865474431091083665L;

	public static final int NUM_CHARS = 26;
	
	protected long freq = 0;
	volatile protected long freqSum = 0;
	
	protected Trie[] children = new Trie[NUM_CHARS];
	

	public long frequency() {
		return freq;
	}
	
	public long frequencySum() {
		if (freqSum == 0) {
			freqSum = freq;
			for (int i=0; i<NUM_CHARS; i++) {
				Trie child = children[i];
				if (child != null) freqSum += child.frequencySum();
			}
		}
		return freqSum;
	}
	
	public Trie child(char c) {
		return children[Character.getNumericValue(c)-10];
	}
	
	public Trie child(Character c) {
		return children[Character.getNumericValue(c.charValue())-10];
	}
	
	public void learn(String s, int i) {
		freqSum++;
		if (i < 0) {
			freq++;
			return;
		}
		int childIndex = Character.getNumericValue(s.charAt(i))-10;
		if (children[childIndex] == null) {
			children[childIndex] = new Trie();
		}
		children[childIndex].learn(s, i-1);
	}
	
	public void learn(String s) {
		for (int i=-1; i<s.length(); i++) {
			learn(s, i);
		}
	}
}
