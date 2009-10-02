package de.tum.in.lrr.hmm.gene;

import java.io.IOException;

import de.tum.in.lrr.hmm.Sequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public interface SequenceReader {

    public enum Format {
        Fasta,
        Embl
    }

    public boolean ready() throws IOException;

    public Sequence readSequence() throws IOException;

}
