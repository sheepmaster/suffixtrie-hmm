package de.blacksheepsoftware.t9;

import java.util.*;

import de.blacksheepsoftware.common.*;

public class Text {
	
	protected static class Word implements Comparable {
		protected Double score;
		protected String string;
		
		public Word(double score, String string) {
			this.score = new Double(score);
			this.string = string;
		}

		public int compareTo(Object other) {
			if (other instanceof Word) {
				return ((Word)other).score.compareTo(score);
//				return score.compareTo(((Word)other).score);
			} else {
				throw new ClassCastException();
			}
		}
	}
	
	protected static class CompList extends LinkedList implements Comparable {
		private static final long serialVersionUID = 1L;

		public int compareTo(Object other) {
			if (other instanceof CompList) {
				return ((Comparable)getFirst()).compareTo(((CompList)other).getFirst());
			} else {
				throw new ClassCastException();
			}
		}
	}
	
	public Text(Trie freqs) {
		this.freqs = freqs;
	}
	
	protected Trie freqs;
	protected List chunks = new Vector();
	protected int activeChunk = 0;
	protected LinkedList activeNumbers = new LinkedList();
	protected String[] words = null;
	protected int activeWord = 0;
	
	protected static CompList merge(CompList[] list) {
		CompList mergedList = new CompList();
		PriorityQueue pq = new BinaryHeap();
		for (int i=0; i<list.length; i++) {
			pq.add(list[i]);
		}
		while (!pq.isEmpty()) {
			CompList l = (CompList)pq.remove();
			mergedList.add(l.removeFirst());
			if (!l.isEmpty()) {
				pq.add(l);
			}
		}
		return mergedList;
	}
	
	protected static CompList words(Model model, List numbers, String word, double score) {
		NumberKey n = (NumberKey)numbers.remove(0);
		if (numbers.isEmpty()) {
			CompList l = new CompList();
			l.add(new Word(score, word));
			return l;
		} else {
			char[] chars = n.characters();
			CompList[] newWords = new CompList[chars.length];
			for (int i=0; i<chars.length; i++) {
				score += model.push(chars[i]);
				newWords[i] = words(model, numbers, word+chars[i], score);
				model.pop();
			}
			numbers.add(n);
			return merge(newWords);
		}
	}

	protected static int findWord(String[] words, String oldWord) {
		for (int i=0; i<words.length; i++) {
			if (oldWord.startsWith(words[i])) {
				return i;
			}
		}
		throw new NoSuchElementException();
	}

	protected void calcWords() {
		CompList sortedWords = words((Model)chunks.get(activeChunk-1), activeNumbers, "", 0);
		words = new String[sortedWords.size()];
		Iterator it = sortedWords.iterator();
		for (int i=0; i<sortedWords.size(); i++) {
			words[i] = ((Word)it.next()).string;
		}
	}
	
	protected void mergeWords() {
		if (activeChunk == 0) {
			return;
		}
		Object next = chunks.get(activeChunk);
		Object active = chunks.get(activeChunk-1);
		if ((active instanceof Model) && (next instanceof Model)) {
			((Model)active).push(((Model)next).toString());
			chunks.remove(activeChunk);
		}
	}
	
	protected void finishWord() {
		if ((words == null) || (words.length == 0) || (activeChunk == 0)) {
			return;
		}
		Object active = chunks.get(activeChunk-1);
		if (active instanceof Model) {
			((Model)active).push(words[activeWord]);
			activeNumbers.clear();
			mergeWords();
		}
	}
	
	protected void activateWord() {
		if (activeChunk == 0) {
			return;
		}
		Object active = chunks.get(activeChunk-1);
		if (active instanceof Model) {
			LinkedList l = new LinkedList();
			Model model = (Model)active;
			Iterator it = ((List)model.text()).iterator();
			while (it.hasNext()) {
				l.addFirst(new NumberKey((Character)it.next()));
			}
			model.pop(l.size());
			activeNumbers.addAll(l);
		}
	}
	
	public void moveRight() {
		finishWord();
		if (activeChunk < chunks.size()) {
			activeChunk++;
		}
		activateWord();
	}

	public void moveLeft() {
//		finishWord();
		if (activeChunk > 0) {
			activeChunk--;
		}
		activateWord();
	}
	
	public void nextWord() {
		activeWord = (activeWord + 1) % words.length;
	}
	
	public void previousWord() {
		activeWord = (activeWord - 1) % words.length;
	}
	
	public void insertChar(char c) {
		finishWord();
		chunks.add(activeChunk++, new Character(c));
	}
	
	public void insertNumberKey(NumberKey n) {
		if ((activeChunk == 0) || !(chunks.get(activeChunk-1) instanceof Model)) {
			chunks.add(activeChunk++, new Model(freqs));
		}
		activeNumbers.add(n);
		calcWords();
		activeWord = 0;
	}
	
	public void delete() {
		if (activeChunk == 0) {
			return;
		}
		Object active = chunks.get(--activeChunk);
		if (active instanceof Character) {
			chunks.remove(activeChunk);
			mergeWords();
		} else if (active instanceof Model) {
			if (activeNumbers.size() > 0) {
				// active word
				activeNumbers.removeLast();
				String oldWord = words[activeWord];
				calcWords();
				activeWord = findWord(words, oldWord);
			} else {
				// inactive word
				((Model)active).pop();
			}
			// delete word if empty
			if (((Model)active).length() == 0) {
				chunks.remove(activeChunk);
			}
		}
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		Iterator i = chunks.iterator();
		while (i.hasNext()) {
			s.append(i.next());
		}
		return s.toString();
	}
}
