package de.tum.in.lrr.hmm.util;

import java.util.Iterator;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public abstract class UnmodifiableIterator<B> implements Iterator<B> {

    public void remove() {
        throw new UnsupportedOperationException();
    }

}