package de.tum.in.lrr.hmm.gene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tum.in.lrr.hmm.Alphabet;
import de.tum.in.lrr.hmm.SubSequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class EmblReader extends AbstractSequenceReader {

    protected final List<String> subsequenceStrings = new ArrayList<String>();

    protected final BufferedReader r;

    public EmblReader(BufferedReader r) {
        this.r = r;
    }

    public EmblReader(Reader r) {
        this(AbstractSequenceReader.bufferedReader(r));
    }

    public boolean canParse() throws IOException {
        final String idLine = r.readLine();
        if (idLine == null) {
            return false;
        }
        return (idLine.startsWith("ID "));

    }

    public boolean ready() throws IOException {
        return r.ready();
    }

    public AnnotatedSequence readSequence() throws IOException, FileFormatException {
        final String idLine = r.readLine();
        if (idLine == null) {
            return null;
        }
        final Matcher headerMatcher = Pattern.compile("ID\\s+([a-zA-Z0-9_]+)").matcher(idLine);
        if (!headerMatcher.lookingAt()) {
            throw new FileFormatException("Invalid header line: \""+idLine+"\"");
        }
        final String identifier = headerMatcher.group(1);

        final AnnotatedSequence seq = readFullSequence(identifier);

        final List<SubSequence> subsequences = seq.getSubSequences();
        for (String s : subsequenceStrings) {
            final SubSequence subseq;
            final Matcher m1 = Pattern.compile("FT\\s+CDS\\s+<?(\\d+)\\.\\.>?(\\d+)").matcher(s);
            if (m1.matches()) {
                final int start = Integer.parseInt(m1.group(1));
                final int end = Integer.parseInt(m1.group(2));
                subseq = new SubSequence(seq, start-1, end);
            } else {
                final Matcher m2 = Pattern.compile("FT\\s+CDS\\s+complement\\((\\d+)\\.\\.(\\d+)\\)").matcher(s);
                if (m2.matches()) {
                    final int start = Integer.parseInt(m2.group(1));
                    final int end = Integer.parseInt(m2.group(2));
                    subseq = new SubSequence(seq, start-1, end, true);
                } else {
                    System.err.println("Warning: Invalid feature header: \""+s+"\"");
                    continue;
                }
            }
            subsequences.add(subseq);
        }

        subsequenceStrings.clear();

        return seq;


    }

    /**
     * @param identifier
     * @throws IOException
     * @throws FileFormatException
     */
    private AnnotatedSequence readFullSequence(final String identifier) throws IOException, FileFormatException {
        while (r.ready()) {
            final String line = r.readLine();
            if (line.matches("FT\\s+CDS\\s+.*")) {
                subsequenceStrings.add(line);
            } else {
                Matcher m = Pattern.compile("SQ\\s+Sequence (\\d+) BP;( \\d+ A; \\d+ C; \\d+ G; \\d+ T; (\\d+) other;)?").matcher(line);
                if (m.lookingAt()) {
                    int length = Integer.parseInt(m.group(1));
                    if (m.group(3) != null) {
                        length -= Integer.parseInt(m.group(3));
                    }

                    final StringBuffer sb = new StringBuffer();
                    while (true) {
                        final String seqLine = r.readLine();
                        if (seqLine == null) {
                            throw new FileFormatException("Unexpected end of sequence");
                        }
                        if (seqLine.equals("//")) {
                            break;
                        } else {
                            sb.append(seqLine);
                        }
                    }

                    final String contents = sb.toString();
                    final AnnotatedSequence seq = new AnnotatedSequence(identifier, contents, Alphabet.DNA, length);

                    return seq;
                }
            }
        }

        throw new FileFormatException("No sequence data found");
    }

}
