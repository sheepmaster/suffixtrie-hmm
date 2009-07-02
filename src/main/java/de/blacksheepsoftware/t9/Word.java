package de.blacksheepsoftware.t9;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Represents a possible sequence of characters occurring in a model, along with
 * the associated state distribution and total score.
 * 
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 */
public class Word implements Comparable<Word> {
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

    /**
     * Adds the character {@code c} to the word.
     * 
     * @param c
     *            A character.
     * @return The resulting word.
     */
    public Word push(char c) {
        final StateDistribution newState = state.successor(NumberKey.intForChar(c));
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

    protected void addWords(List<Word> wordsWithScores, List<? extends CharacterTemplate> characters) {
        final int length = string.length();
        if (length >= characters.size()) {
            state = null;
            wordsWithScores.add(this);
        } else {
            for (char c : characters.get(length).characters()) {
                push(c).addWords(wordsWithScores, characters);
            }
        }
    }

    protected void addWords(PriorityQueue<Word> pq, List<? extends CharacterTemplate> characters, int number) {
        if ((pq.size() >= number) && (score > pq.element().score)) {
            return;
        }
        final int length = string.length();
        if (length >= characters.size()) {
            state = null;
            pq.add(this);
            if (pq.size() > number) {
                pq.remove();
            }
        } else {
            char[] cs = characters.get(length).characters();
            Word[] words = new Word[cs.length];
            for (int i = 0; i < words.length; i++) {
                words[i] = push(cs[i]);
            }
            Arrays.sort(words);
            for (Word w : words) {
                w.addWords(pq, characters, number);
            }
            /*
             * for (char c : characters.get(length).characters()) {
             * push(c).addWords(pq, characters, number); }
             */
        }
    }

    /**
     * Calculates an ordered list of possible word completions.
     * 
     * @param d
     *            The state distribution to start from.
     * @param characters
     *            A list of possible completion templates.
     * @return A list of possible completions of the word resulting in the state
     *         distribution {@code d}, ordered by their respective score, with
     *         the smallest score first.
     */
    public static List<Word> completions(StateDistribution d, List<? extends CharacterTemplate> characters) {
        final ArrayList<Word> wordList = new ArrayList<Word>();

        final Word w = new Word(d);
        w.addWords(wordList, characters);

        Collections.sort(wordList);
        return wordList;
    }

    /**
     * Calculates an ordered list of the {@code n} best word completions.
     * 
     * @param d
     *            The state distribution to start from.
     * @param characters
     *            A list of possible completion templates.
     * @param n
     *            The maximum number of word completions to return.
     * @return A list of the {@code n} best possible completions of the word
     *         resulting in the state distribution {@code d}, ordered ascending
     *         by their respective score.
     */
    public static List<Word> completions(StateDistribution d, List<? extends CharacterTemplate> characters, int n) {
        PriorityQueue<Word> pq = new PriorityQueue<Word>(n + 1, Collections.<Word> reverseOrder());

        final Word w = new Word(d);
        w.addWords(pq, characters, n);

        final ArrayList<Word> words = new ArrayList<Word>(n);
        while (!pq.isEmpty()) {
            words.add(pq.remove());
        }
        Collections.reverse(words);
        return words;
    }
}
