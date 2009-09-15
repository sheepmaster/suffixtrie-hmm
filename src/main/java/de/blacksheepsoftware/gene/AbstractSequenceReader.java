package de.blacksheepsoftware.gene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import de.blacksheepsoftware.hmm.Sequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public abstract class AbstractSequenceReader implements SequenceReader {

    protected static BufferedReader bufferedReader(Reader r) {
        if (r instanceof BufferedReader) {
            return (BufferedReader)r;
        } else {
            return new BufferedReader(r);
        }
    }

    public List<Sequence> readAllSequences() throws IOException {
        List<Sequence> testSequences = new ArrayList<Sequence>();
        while (ready()) {
            testSequences.add(readSequence());
        }
        return testSequences;
    }



}
