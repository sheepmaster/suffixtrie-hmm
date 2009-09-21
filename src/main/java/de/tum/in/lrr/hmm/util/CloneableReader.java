package de.tum.in.lrr.hmm.util;

import java.io.Reader;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public abstract class CloneableReader extends Reader implements Cloneable {

    @Override
    public abstract CloneableReader clone();

}
