package de.blacksheepsoftware.evaluation;

import java.util.Iterator;
import java.util.Random;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class RandomSequence implements Iterable<Integer> {

    protected final int numCharacters;

    protected final long seed;

    public RandomSequence(int numCharacters) {
        this(numCharacters, 0);
    }

    public RandomSequence(int numCharacters, long seed) {
        this.numCharacters = numCharacters;
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


}
