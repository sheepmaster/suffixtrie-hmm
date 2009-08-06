package de.blacksheepsoftware.evaluation;

import java.util.Iterator;
import java.util.Random;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class RandomSequence implements Iterable<Integer> {

    protected final int numCharacters;

    public RandomSequence(int numCharacters) {
        this.numCharacters = numCharacters;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {

            final Random r = new Random();

            public boolean hasNext() {
                return true;
            }

            public Integer next() {
                return r.nextInt(numCharacters)+1;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }


}
