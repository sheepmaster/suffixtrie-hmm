package de.blacksheepsoftware.hmm;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public interface SequenceIterator {

    double score(int character);

}
