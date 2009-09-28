package de.tum.in.lrr.hmm.util;

import java.util.Iterator;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public interface Mapper<A,B> {

    Iterator<B> map(Function<A,B> f, Iterator<A> as);

}
