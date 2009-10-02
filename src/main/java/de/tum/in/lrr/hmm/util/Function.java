package de.tum.in.lrr.hmm.util;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public interface Function<A, B> {

    B call(A a);

}
