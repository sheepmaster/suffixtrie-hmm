package de.blacksheepsoftware.gene;

import java.io.IOException;

import de.blacksheepsoftware.hmm.Sequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public interface SequenceReader {

    public boolean canParse() throws IOException;

    public Sequence readSequence() throws IOException;

}
