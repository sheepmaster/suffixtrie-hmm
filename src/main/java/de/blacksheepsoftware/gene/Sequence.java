package de.blacksheepsoftware.gene;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class Sequence implements Iterable<Integer> {

    protected final String identifier;
    protected final String contents;
    protected final String alphabet;

    protected final Map<Character,Integer> alphabetMap = new HashMap<Character, Integer>();
    protected final Pattern symbolPattern;

    public Sequence(String identifier, String contents, String alphabet) {
        this.identifier = identifier;
        this.contents = contents;
        this.alphabet = alphabet;

        if (!alphabet.matches("[a-zA-Z]+")) {
            throw new IllegalArgumentException();
        }
        symbolPattern = Pattern.compile("["+alphabet+"]", Pattern.CASE_INSENSITIVE);

        for (int i=0; i<alphabet.length(); i++) {
            alphabetMap.put(alphabet.charAt(i), i+1);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Integer> iterator() {
        return new SequenceIterator();
    }

    private class SequenceIterator implements Iterator<Integer> {  // TODO: better name

        public SequenceIterator() {
            // nothing to do
        }

        protected final Matcher matcher = symbolPattern.matcher(contents);

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
                return alphabetMap.get(matcher.group().charAt(0));
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
