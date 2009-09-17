package de.tum.in.lrr.hmm;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;

import de.tum.in.lrr.hmm.util.ByteArray;


/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class Sequence extends AbstractSequence {

    protected final String identifier;
    protected final Alphabet alphabet;
    protected byte[] charSequence = null;

    public Sequence(String identifier, final String contents, final Alphabet alphabet, int length) {
        this(identifier, alphabet, ByteArray.forList(sequenceIterator(contents, alphabet), length));
    }

    public Sequence(String identifier, final String contents, final Alphabet alphabet) {
        this(identifier, alphabet, ByteArray.forList(sequenceIterator(contents, alphabet)));
    }

    public Sequence(String identifier, Alphabet alphabet, byte[] sequence) {
        this.charSequence = sequence;
        this.alphabet = alphabet;
        this.identifier = identifier;
    }

    /**
     * @param sequence
     * @param alphabet
     * @return
     */
    protected static Iterator<Byte> sequenceIterator(final String sequence, final Alphabet alphabet) {
        return new Iterator<Byte>() {  // TODO: better name

            protected final Matcher matcher = alphabet.getSymbolPattern().matcher(sequence);

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
            public Byte next() {
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

        };
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
        return charSequence.length;
    }

    public byte[] charSequence() {
        return charSequence;
    }

    @Override
    public Byte get(int i) {
        return charSequence()[i];
    }

}
