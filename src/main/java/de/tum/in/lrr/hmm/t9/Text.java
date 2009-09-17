package de.tum.in.lrr.hmm.t9;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.tum.in.lrr.hmm.Model;

public class Text {
    protected Model model;

    protected StringBuffer text = new StringBuffer();

    protected int cursorStart = 0;
    protected int cursorEnd = 0;
    protected Stack<NumberKey> activeNumbers = new Stack<NumberKey>();
    protected boolean wordSelected = false;
    protected int activeWord = 0;
    protected List<String> words = new ArrayList<String>();

    public Text(Model m) {
        model = m;
    }

    public void check() {
        assert cursorStart >= 0;
        assert cursorStart <= text.length();
        assert cursorEnd >= 0;
        assert cursorEnd <= text.length();
        final int selectionLength = cursorEnd - cursorStart;
        assert selectionLength >= 0;
        assert activeNumbers.size() == selectionLength;
        assert activeWord < words.size();
        assert activeWord >= 0;
        assert (wordSelected || (activeWord == 0)); // if no word is selected, the "active" word is the first
    }

    public String getContents() {
        return text.toString();
    }

    public int getCursorStart() {
        return cursorStart;
    }

    public int getCursorEnd() {
        return cursorEnd;
    }

    public void moveRight() {
        if (cursorEnd == cursorStart) {
            // nothing is selected
            cursorEnd = findWordEnd(cursorEnd);
            if (cursorEnd == cursorStart) {
                // to the right of the cursor there is no word, so move the cursor to the right
                if (cursorStart < text.length()) {
                    cursorStart++;
                    cursorEnd++;
                }
            } else {
                findActiveWord();
            }
        } else {
            cursorStart = cursorEnd;
            finishWord();
        }
    }

    public void moveLeft() {
        if (cursorEnd == cursorStart) {
            // nothing is selected
            cursorStart = findWordStart(cursorStart);
            if (cursorEnd == cursorStart) {
                // to the left of the cursor there is no word
                if (cursorEnd > 0) {
                    cursorStart--;
                    cursorEnd--;
                }
            } else {
                findActiveWord();
            }
        } else {
            cursorEnd = cursorStart;
            finishWord();
        }
    }

    protected void finishWord() {
        activeNumbers.clear();
        deselectWord();
    }

    protected void replaceActiveWord() {
        text.replace(cursorStart, cursorEnd, words().get(activeWord));
    }

    protected void findActiveWord() {
        if (!wordSelected) {
            activeWord = words().indexOf(text.substring(cursorStart, cursorEnd));
            wordSelected = true;
        }
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

    public void moveUp() {
        //      if (cursorBegin == cursorEnd) {
        //
        //      } else {
        findActiveWord();
        final int size = words().size();
        activeWord = (activeWord + size - 1) % size;
        replaceActiveWord();
        //      }
    }

    public void insertChar(char c) {
        //        if (activeWord > 0) {
        cursorStart = cursorEnd;
        finishWord();
        //        }
        text.insert(cursorStart++, c);
        cursorEnd++;
    }

    public void insertNumberKey(NumberKey n) {
        if (wordSelected) {
            cursorStart = cursorEnd;
            finishWord();
        }
        activeNumbers.push(n);
        deselectWord();
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

    protected List<String> words() {
        if (words.isEmpty()) {
            if (activeNumbers.isEmpty()) {
                activeNumbers.addAll(NumberKey.numberKeysForString(text.substring(cursorStart, cursorEnd)));
            }
            final Iterable<Byte> prefix = NumberKey.sequenceForWord(text.substring(findWordStart(cursorStart), cursorStart));
            final List<Word> wordList = Word.completions(model.startingDistribution().successor(prefix), activeNumbers);
            for (Word w : wordList) {
                words.add(w.string);
            }
        }
        return words;
    }

    public void deleteChar() {
        if (cursorEnd == 0) {
            return;
        }
        text.deleteCharAt(--cursorEnd);
        if (cursorEnd < cursorStart) {
            cursorStart--;
        } else {
            activeNumbers.pop();
            words.clear();
            if (wordSelected) {
                findActiveWord();
            } else {
                replaceActiveWord();
            }
        }
    }

    /**
     * 
     */
    protected void deselectWord() {
        words.clear();
        wordSelected = false;
        activeWord = 0;
    }
}
