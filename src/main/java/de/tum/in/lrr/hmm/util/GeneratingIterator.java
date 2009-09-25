package de.tum.in.lrr.hmm.util;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public abstract class GeneratingIterator<E> extends UnmodifiableIterator<E> {

    Queue<E> q = new ArrayDeque<E>();

    protected abstract boolean canGenerateValues();

    protected abstract void generateValues();

    protected void addValue(E value) {
        q.add(value);
    }

    public boolean hasNext() {
        while (q.isEmpty() && canGenerateValues()) {
            generateValues();
        }
        return !q.isEmpty();
    }

    public E next() {
        return q.remove();
    }
}
