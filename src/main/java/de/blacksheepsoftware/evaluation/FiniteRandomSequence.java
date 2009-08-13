package de.blacksheepsoftware.evaluation;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class FiniteRandomSequence extends RandomSequence {

    protected final int length;

    /**
     * @param numCharacters
     */
    public FiniteRandomSequence(int numCharacters, int length) {
        this(numCharacters, length, 0);
    }

    /**
     * @param numCharacters
     * @param seed
     */
    public FiniteRandomSequence(int numCharacters, int length, long seed) {
        super(numCharacters, seed);
        this.length = length;
    }

    protected class FiniteRandomIterator extends RandomIterator {

        protected int remaining = length;

        @Override
        public boolean hasNext() {
            return (remaining > 0);
        }

        @Override
        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            remaining--;
            return super.next();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Integer> iterator() {
        return new FiniteRandomIterator();
    }


}
