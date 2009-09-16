package de.tum.in.lrr.hmm.util;

import java.util.Iterator;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public abstract class TransformingIterator<I, O> implements Iterator<O> {

    protected final Iterator<I> input;

    public TransformingIterator(Iterator<I> input) {
        this.input = input;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        return input.hasNext();
    }

    public abstract O transform(I in);

    /**
     * {@inheritDoc}
     */
    public O next() {
        return transform(input.next());
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
        input.remove();
    }

}
