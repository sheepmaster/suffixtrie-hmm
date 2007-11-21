package de.blacksheepsoftware.t9;

import java.util.*;

public class Model {
	private class IntRef {
		private int intValue;
		
		public IntRef(int v) {
			intValue = v;
		}
		
		public int intValue() { 
			return intValue;
		}
		
		public void setIntValue(int v) {
			intValue = v;
		}
	}

	protected Trie freqs;
	
	protected List text = new LinkedList();
	protected List probs = new LinkedList();
	
	public Model(Trie freqs) {
		this.freqs = freqs;
	}
	
	protected double count(Trie trie, IntRef ir) {
		int depth = 0;
		int minDepth = ir.intValue();
		double count = 0.0;
		Iterator probsEnum = probs.iterator();
		Iterator textEnum = text.iterator();
		Trie child = null;
		while ((trie != null) && textEnum.hasNext() && ((child = trie.child((Character)textEnum.next())) != null) || (depth < minDepth)) {
			if (trie != null) count += trie.frequency();
			depth++;
			trie = child;
			count *= ((Double)probsEnum.next()).doubleValue();
		}
		if (trie != null) count += trie.frequencySum();
		ir.setIntValue(depth);
		return count;
	}
	
	public double push(char c) {
		IntRef ir = new IntRef(0);
		double count2 = count(freqs, ir);
		double count1 = count(freqs.child(c), ir);
		
		double p = count1/count2;
		
		text.add(0, new Character(c));
		probs.add(0, new Double(p));
		
		return -Math.log(p);
	}
	
	public double push(String s) {
		double score = 0.0;
		for (int i=0; i<s.length(); i++) {
			score += push(s.charAt(i));
		}
		return score;
	}
	
	public void pop() {
		text.remove(0);
		probs.remove(0);
	}
	
	public void pop(int num) {
		text.subList(0, num).clear();
		probs.subList(0, num).clear();
	}
	
	public String toString() {
		String s = "";
		Iterator i = text.iterator();
		while (i.hasNext()) {
			s = (Character)i.next() + s;
		}
		return s;
	}

	public List text() {
		return text;
	}

	public int length() {
		return text.size();
	}
}
