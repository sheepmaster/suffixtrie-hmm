package de.blacksheepsoftware.evaluation;

import java.util.Iterator;
import java.util.Random;

import de.blacksheepsoftware.hmm.AbstractSequence;
import de.blacksheepsoftware.hmm.Alphabet;
import de.blacksheepsoftware.hmm.ISequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class RandomSequence implements Iterable<Integer> {

    protected final int numCharacters;

    protected final long seed;

    protected final Alphabet alphabet;

    public RandomSequence(Alphabet alphabet) {
        this(alphabet, 0);
    }

    public RandomSequence(Alphabet alphabet, long seed) {
        this.alphabet = alphabet;
        this.numCharacters = alphabet.numberOfCharacters();
        this.seed = seed;
    }

    /**
     * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
     *
     */
    protected class RandomIterator implements Iterator<Integer> {
        private final Random r = (seed == 0) ? new Random() : new Random(seed);

        public boolean hasNext() {
            return true;
        }

        public Integer next() {
            return r.nextInt(numCharacters)+1;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Integer> iterator() {
        return new RandomIterator();
    }

    /**
     * @param i
     * @return
     */
    public ISequence generateSequence(int length) {
        Iterator<Integer> it = iterator();
        int[] array = new int[length];
        for (int i=0; i<length; i++) {
            array[i] = it.next();
        }
        return new ListSequence(alphabet, "Random"+numCharacters+":"+seed, array);
    }

    protected static class ListSequence extends AbstractSequence {

        protected final int[] sequence;

        protected final Alphabet alphabet;

        protected final String identifier;

        public ListSequence(Alphabet alphabet, String identifier, int[] seq) {
            this.alphabet = alphabet;
            this.identifier = identifier;
            sequence = seq;
        }

        /**
         * {@inheritDoc}
         */
        public String getIdentifier() {
            return identifier;
        }

        /**
         * {@inheritDoc}
         */
        public int length() {
            return sequence.length;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Integer get(int index) {
            return sequence[index];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return length();
        }

        /**
         * {@inheritDoc}
         */
        public Alphabet getAlphabet() {
            return alphabet;
        }

    }

}
