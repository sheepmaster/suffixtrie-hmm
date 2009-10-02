package de.tum.in.lrr.hmm.gene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import de.tum.in.lrr.hmm.Sequence;
import de.tum.in.lrr.hmm.util.CloneableReader;
import de.tum.in.lrr.hmm.util.LinkedDataBlockReader;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public abstract class AbstractSequenceReader implements SequenceReader {

    public static SequenceReader create(Reader r1) throws IOException {
        final CloneableReader r = new LinkedDataBlockReader(r1);
        final Format format = guessFormat(r);
        switch(format) {
        case Fasta:
            return new FastaReader(r);
        case Embl:
            return new EmblReader(r);
        default:
            throw new FileFormatException("Unknown file format");
        }

    }

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

    /**
     * @param r
     * @throws IOException
     */
    static Format guessFormat(final CloneableReader r) throws IOException {
        String firstLine = new BufferedReader(r.clone()).readLine();
        if (firstLine == null) {
            return null;
        } else if (firstLine.startsWith(">")) {
            return Format.Fasta;
        } else if (firstLine.startsWith("ID ")) {
            return Format.Embl;
        } else {
            return null;
        }
    }



}

