package de.tum.in.lrr.hmm.gene;

import java.util.Iterator;
import java.util.Random;

import de.tum.in.lrr.hmm.Alphabet;
import de.tum.in.lrr.hmm.ISequence;
import de.tum.in.lrr.hmm.Sequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class RandomSequence implements Iterable<Byte> {

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
    protected class RandomIterator implements Iterator<Byte> {
        private final Random r = (seed == 0) ? new Random() : new Random(seed);

        public boolean hasNext() {
            return true;
        }

        public Byte next() {
            return (byte)(r.nextInt(numCharacters)+1);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Byte> iterator() {
        return new RandomIterator();
    }

    /**
     * @param i
     * @return
     */
    public ISequence generateSequence(int length) {
        Iterator<Byte> it = iterator();
        byte[] array = new byte[length];
        for (byte i=0; i<length; i++) {
            array[i] = it.next();
        }
        return new Sequence("Random"+numCharacters+":"+seed, alphabet, array);
    }

}
