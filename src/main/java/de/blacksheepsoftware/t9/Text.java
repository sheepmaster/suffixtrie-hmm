package de.blacksheepsoftware.t9;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class Text {
    protected Model model;
    
    protected StringBuffer text = new StringBuffer();
    
    protected int cursorStart = 0;
    protected int cursorEnd = 0;
    protected Stack<NumberKey> activeNumbers = new Stack<NumberKey>();
    protected int activeWord = -1;
    protected List<String> words = new LinkedList<String>();
    
    public Text(Model m) {
        model = m;
    }
    
    public void moveRight() {
        if (cursorEnd == cursorStart) {
            cursorEnd = findWordEnd(cursorEnd);
            if ((cursorEnd == cursorStart) && (cursorStart < text.length())) {
                cursorStart++;
                cursorEnd++;
            }
        } else {
            cursorStart = cursorEnd;
            finishWord();
        }
    }

    public void moveLeft() {
        if (cursorEnd == cursorStart) {
            cursorStart = findWordStart(cursorStart);
            if ((cursorEnd == cursorStart) && (cursorEnd > 0)) {
                cursorStart--;
                cursorEnd--;
            }
        } else {
            cursorEnd = cursorStart;
            finishWord();
        }
    }

    protected void finishWord() {
        activeNumbers.clear();
        words.clear();
        activeWord = -1;
    }
    
    protected void replaceActiveWord() {
        text.replace(cursorStart, cursorEnd, words().get(activeWord));
    }

    public void moveDown() {
//      if (cursorBegin == cursorEnd) {
//      
//      } else {
        findActiveWord();
        activeWord = (activeWord + 1) % words().size();
        replaceActiveWord();
//      }
    }
    
    protected void findActiveWord() {
        if (activeWord == -1) {
            activeWord = words().indexOf(text.substring(cursorStart, cursorEnd));
        }
    }

    public void moveUp() {
//      if (cursorBegin == cursorEnd) {
//      
//      } else {
        activeWord = (activeWord - 1) % words().size();
        replaceActiveWord();
//      }
    }
    
    public void insertChar(char c) {
        if (activeWord > 0) {
            cursorStart = cursorEnd;
            finishWord();
        }
        text.insert(cursorStart++, c);
        cursorEnd++;
    }
    
    public void insertNumberKey(NumberKey n) {
        if (activeWord > 0) {
            cursorStart = cursorEnd;
            finishWord();
        }
        activeNumbers.push(n);
        words.clear();
        activeWord = 0;
        replaceActiveWord();
        cursorEnd++;
    }
    
    protected int findWordStart(int index) {
        while ((index > 0) && Character.isLetter(text.charAt(index-1))) {
            index--;
        }
        return index;
    }
    
    protected int findWordEnd(int index) {
        while ((index < text.length()) && Character.isLetter(text.charAt(index))) {
            index++;
        }
        return index;
    }
    
    protected List<NumberKey> numberKeysInRange(int start, int end) {
        Vector<NumberKey> numberKeys = new Vector<NumberKey>();
        for (int i = start; i < end; i++) {
            numberKeys.add(new NumberKey(text.charAt(i)));
        }
        return numberKeys;
    }
    
    protected List<String> words() {
        if (words.isEmpty()) {
            if (activeNumbers.isEmpty()) {
                activeNumbers.addAll(numberKeysInRange(cursorStart, cursorEnd));
            }
            final Vector<Word> wordList = new Vector<Word>();
            final int[] prefix = NumberKey.intArrayForString(text.substring(findWordStart(cursorStart), cursorStart));
            final Model.StateDistribution dist = model.startingDistribution().read(prefix);
            addWords(wordList, new Word(dist), 0);
            Collections.sort(wordList);
            for (Word w : wordList) {
                words.add(w.string);
            }
        }
        return words;
    }

    protected void addWords(List<Word> wordsWithScores, Word w, int i) {
        if (i >= activeNumbers.size()) {
            wordsWithScores.add(w.clearState());
        } else {
            for (char c : activeNumbers.get(i).characters()) {
                addWords(wordsWithScores, w.push(c), i+1);
            }
        }
    }
    
    public void deleteChar() {
        if (cursorEnd == cursorStart) {
            if (cursorStart > 0) {
                cursorStart--;
                text.deleteCharAt(cursorEnd--);
            }
        } else {
            text.deleteCharAt(cursorEnd--);
            activeNumbers.pop();
            words.clear();
        }
    }
  /*  
    protected static class Word implements Comparable<Word> {
        protected double score;
        protected String string;
        
        public Word(double score, String string) {
            this.score = score;
            this.string = string;
        }

        public int compareTo(Word arg0) {
            return Double.compare(score, arg0.score);
        }
    }
/*    
    protected static final Comparator<List<Word>> wordListComparator = new Comparator<List<Word>>() {
        public int compare(List<Word> arg0, List<Word> arg1) {
            return Double.compare(arg0.get(0).score, arg1.get(0).score);
        }
    };
    
    protected static LinkedList<Word> merge(LinkedList<Word>[] list) {
        LinkedList<Word> mergedList = new LinkedList<Word>();
        PriorityQueue<LinkedList<Word>> pq = new PriorityQueue<LinkedList<Word>>(list.length, wordListComparator);
        for (int i=0; i<list.length; i++) {
            pq.add(list[i]);
        }
        while (!pq.isEmpty()) {
            LinkedList<Word> l = pq.remove();
            mergedList.add(l.removeFirst());
            if (!l.isEmpty()) {
                pq.add(l);
            }
        }
        return mergedList;
    }

	protected static class Word implements Comparable<Word> {
		protected Double score;
		protected String string;
		
		public Word(double score, String string) {
			this.score = new Double(score);
			this.string = string;
		}

		public int compareTo(Word other) {
		    return other.score.compareTo(score);
		}
	}
	
	protected static class CompList<T extends Comparable> extends LinkedList<T> implements Comparable<CompList<T>> {
		private static final long serialVersionUID = 1L;

		public int compareTo(CompList<T> other) {
		    return getFirst().compareTo(other.getFirst());
		}
	}
	
	public Text(Model model) {
		this.model = model;
	}
	
	protected Model model;
	protected List chunks = new Vector();
	protected int activeChunk = 0;
	protected LinkedList<NumberKey> activeNumbers = new LinkedList<NumberKey>();
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
	
	protected static CompList<Word> words(Model model, List<NumberKey> numbers, String word, double score) {
		NumberKey n = numbers.remove(0);
		if (numbers.isEmpty()) {
			CompList<Word> l = new CompList<Word>();
			l.add(new Word(score, word));
			return l;
		} else {
			char[] chars = n.characters();
			CompList<Word>[] newWords = new CompList<Word>[chars.length];
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
			LinkedList<NumberKey> l = new LinkedList<NumberKey>();
			Model model = (Model)active;
			Iterator<Character> it = ((List)model.text()).iterator();
			while (it.hasNext()) {
				l.addFirst(new NumberKey(it.next()));
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
*/
}
