package de.blacksheepsoftware.t9;

import java.util.*;

import de.blacksheepsoftware.common.*;

public class Text {
	
	protected class Word implements Comparable {
		protected Double score;
		protected String string;
		
		public Word(double score, String string) {
			this.score = new Double(score);
			this.string = string;
		}

		public int compareTo(Object o) {
			if (o instanceof Word) {
				return score.compareTo(((Word)o).score);
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
	
	protected static CompList newCompList() {
		return new CompList() {
			public int compareTo(Object o) {
				if (o instanceof CompList) {
					return ((Comparable)getFirst()).compareTo(((CompList)o).getFirst());
				} else {
					throw new ClassCastException();
				}
			}
		};
	}
	
	protected static CompList merge(CompList[] list) {
		CompList mergedList = newCompList();
		PriorityQueue pq = new BinaryHeap();
		for (int i=0; i<list.length; i++) {
			pq.add(list[i]);
		}
		while (!pq.isEmpty()) {
			CompList l = (CompList)pq.remove();
			mergedList.add(l.removeFirst());
			pq.add(l);
		}
		return mergedList;
	}
	
	protected CompList words(Model model, List numbers, String word, double score) {
		NumberKey n = (NumberKey)numbers.remove(0);
		if (numbers.isEmpty()) {
			CompList l = newCompList();
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
	
	protected void finishWord() {
		if ((words == null) || (words.length == 0)) {
			return;
		}
		((Model)chunks.get(activeChunk-1)).push(words[activeWord]);
		activeNumbers.clear();
	}
	
	protected void activateWord() {
		LinkedList l = new LinkedList();
		Model model = (Model)chunks.get(activeChunk-1);
		Iterator it = ((List)model.text()).iterator();
		while (it.hasNext()) {
			l.addFirst(new NumberKey((Character)it.next()));
		}
		model.pop(l.size());
		activeNumbers.addAll(l);
	}
	
	public void moveRight() {
		finishWord();
		if (activeChunk < chunks.size()) {
			activeChunk++;
		}
		activateWord();
	}

	public void moveLeft() {
		finishWord();
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
		chunks.add(activeChunk, new Character(c));
		activeChunk++;
	}
	
	public void insertNumberKey(NumberKey n) {
		if ((activeChunk == 0) || !(chunks.get(activeChunk-1) instanceof Model)) {
			chunks.add(activeChunk, new Model(freqs));
			activeChunk++;
		}
		activeNumbers.add(n);
		calcWords();
		activeWord = 0;
	}
	
	public void delete() {
		if (activeChunk == 0) {
			return;
		}
		activeChunk--;
		Object o = chunks.get(activeChunk);
		if (o instanceof Character) {
			chunks.remove(activeChunk);
			// TODO: merge two consecutive models
		} else if (o instanceof Model) {
			if (activeNumbers.size() > 0) {
				activeNumbers.removeLast();
				String oldWord = words[activeWord];
				calcWords();
				activeWord = findWord(words, oldWord);
				if (activeNumbers.size() == 0) {
					chunks.remove(activeChunk);
				}
			} else {
				((Model)o).pop();
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
