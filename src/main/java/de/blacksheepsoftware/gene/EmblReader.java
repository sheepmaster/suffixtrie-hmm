package de.blacksheepsoftware.gene;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.blacksheepsoftware.hmm.Alphabet;
import de.blacksheepsoftware.hmm.ISequence;
import de.blacksheepsoftware.hmm.SubSequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class EmblReader {

    protected final List<String> subsequenceStrings = new ArrayList<String>();

    protected final BufferedReader r;

    public EmblReader(BufferedReader r) {
        this.r = r;
    }

    public AnnotatedSequence readAnnotatedSequence() throws IOException, FileFormatException {
        final String idLine = r.readLine();
        if (idLine == null) {
            throw new FileFormatException("Missing header line");
        }
        final Matcher headerMatcher = Pattern.compile("ID\\s+([a-zA-Z0-9_]+);").matcher(idLine);
        if (!headerMatcher.lookingAt()) {
            throw new FileFormatException("Invalid header line: \""+idLine+"\"");
        }
        final String identifier = headerMatcher.group(1);

        final AnnotatedSequence seq = readSequence(identifier);

        final List<ISequence> subsequences = seq.getSubSequences();
        for (String s : subsequenceStrings) {
            final ISequence subseq;
            final Matcher m1 = Pattern.compile("FT\\s+CDS\\s+(\\d+)\\.\\.(\\d+)").matcher(s);
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
                    throw new FileFormatException("Invalid feature header: \""+s+"\"");
                }
            }
            subsequences.add(subseq);
        }

        return seq;


    }

    /**
     * @param identifier
     * @throws IOException
     * @throws FileFormatException
     */
    private AnnotatedSequence readSequence(final String identifier) throws IOException, FileFormatException {
        while (r.ready()) {
            final String line = r.readLine();
            if (line.matches("FT\\s+CDS\\s+.*")) {
                subsequenceStrings.add(line);
            } else {
                Matcher m = Pattern.compile("SQ\\s+Sequence (\\d+) BP;").matcher(line);
                if (m.lookingAt()) {
                    final int length = Integer.parseInt(m.group(1));

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
