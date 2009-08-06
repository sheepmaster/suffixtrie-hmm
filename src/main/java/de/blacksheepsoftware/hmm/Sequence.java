package de.blacksheepsoftware.hmm;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;

import de.blacksheepsoftware.util.IntArray;


/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class Sequence implements Iterable<Integer> {

    protected final String identifier;
    protected final String contents;
    protected final Alphabet alphabet;
    protected final int length;
    protected int[] charSequence = null;

    public Sequence(String identifier, String contents, Alphabet alphabet, int length) {
        this.identifier = identifier;
        this.contents = contents;
        this.alphabet = alphabet;
        this.length = length;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Integer> iterator() {
        return new SequenceIterator();
    }

    public Alphabet getAlphabet() {
        return alphabet;
    }

    /**
     * @return
     */
    public String getIdentifier() {
        return identifier;
    }

    public int length() {
        return length;
    }

    public int[] charSequence() {
        if (charSequence == null) {
            charSequence = IntArray.forList(this);
        }
        return charSequence;
    }

    /**
     * @param sequences
     */
    public static int totalLength(Iterable<Sequence> sequences) {
        int totalLength = 0;
        for (Sequence s : sequences) {
            totalLength += s.length();
        }
        return totalLength;
    }

    private class SequenceIterator implements Iterator<Integer> {  // TODO: better name

        protected SequenceIterator() {
            // nothing to do
        }

        protected final Matcher matcher = alphabet.getSymbolPattern().matcher(contents);

        protected boolean foundMatch = false;

        /**
         * {@inheritDoc}
         */
        public boolean hasNext() {
            return foundMatch || (foundMatch = matcher.find());
        }

        /**
         * {@inheritDoc}
         */
        public Integer next() {
            if (hasNext()) {
                foundMatch = false;
                return alphabet.indexOfSymbol(matcher.group().charAt(0));
            } else {
                throw new NoSuchElementException();
            }
        }

        /**
         * {@inheritDoc}
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
