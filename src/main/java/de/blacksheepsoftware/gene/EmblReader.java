package de.blacksheepsoftware.gene;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.blacksheepsoftware.hmm.Alphabet;
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
            throw new FileFormatException("Invalid header line");
        }
        final Matcher headerMatcher = Pattern.compile("^ID\\s+(\\w+);").matcher(idLine);
        if (!headerMatcher.matches()) {
            throw new FileFormatException("Invalid header line");
        }
        final String identifier = headerMatcher.group(1);

        final AnnotatedSequence seq = readSequence(identifier);

        final List<SubSequence> subsequences = seq.subSequences();
        for (String s : subsequenceStrings) {
            final Matcher m = Pattern.compile("^FT\\s+CDS\\s+(\\d+)\\.\\.(\\d+)$").matcher(s);
            if (!m.matches()) {
                throw new FileFormatException("Invalid feature header");
            }
            final int start = Integer.parseInt(m.group(1));
            final int end = Integer.parseInt(m.group(2));
            subsequences.add(new SubSequence(seq, start, end));
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
            if (line.matches("^FT\\s+CDS\\s+")) {
                subsequenceStrings.add(line);
            } else {
                Matcher m = Pattern.compile("^SQ\\s+Sequence (\\d+) BP;").matcher(line);
                if (m.matches()) {
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
