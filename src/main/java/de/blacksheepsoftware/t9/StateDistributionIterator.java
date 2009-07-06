package de.blacksheepsoftware.t9;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class StateDistributionIterator implements SequenceIterator {

    protected StateDistribution dist;

    public StateDistributionIterator(StateDistribution dist) {
        this.dist = dist;
    }

    /**
     * {@inheritDoc}
     */
    public double score(int character) {
        dist = dist.successor(character);
        return dist.normalize();
    }

}
