package de.blacksheepsoftware.t9;

import java.util.Collections;
import java.util.List;
import java.util.Vector;



/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class Word implements Comparable<Word>{
    protected StateDistribution state;
    protected final String string;
    protected final double score;
    
    protected Word(StateDistribution state, String characters, double score) {
        this.state = state;
        this.string = characters;
        this.score = score;
    }

    public Word(StateDistribution s) {
        this(s, "", 0);
    }
    
    public Word push(char c) {
        final StateDistribution newState = state.read(NumberKey.intForChar(c));
        return new Word(newState, string + c, score + newState.normalize());
    }
    
    public String toString() {
        return string;
    }
    
    public double score() {
        return score;
    }
    
    public int compareTo(Word arg0) {
        return Double.compare(score, arg0.score);
    }

    protected void addWords(List<Word> wordsWithScores, List<? extends CharacterTemplate> characters, int i) {
        if (i >= characters.size()) {
            state = null;
            wordsWithScores.add(this);
        } else {
            for (char c : characters.get(i).characters()) {
                push(c).addWords(wordsWithScores, characters, i+1);
            }
        }
    }

    public List<Word> completions(List<? extends CharacterTemplate> characters) {
        final Vector<Word> wordList = new Vector<Word>();
        addWords(wordList, characters, 0);
        Collections.sort(wordList);
        return wordList;
    }
}
