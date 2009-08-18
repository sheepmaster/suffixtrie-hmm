package de.blacksheepsoftware.hmm;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public interface ISequence extends Iterable<Integer> {
    int length();

    String getIdentifier();
}
