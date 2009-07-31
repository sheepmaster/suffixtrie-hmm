package de.blacksheepsoftware.gene;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class FastaReader {

    protected static final Pattern headerPattern = Pattern.compile("^>\\s*(\\S+)(?:\\s+(.*))?");

    protected final BufferedReader r;

    protected String line;

    public FastaReader(BufferedReader r) throws IOException {
        this.r = r;
        line = r.readLine();
    }

    public Sequence readSequence() throws IOException {
        if (line == null) {
            return null;
        }
        Matcher m = headerPattern.matcher(line);
        if (!m.matches()) {
            throw new FileFormatException("Sequence header must start with \">\"");
        }
        final String identifier = m.group(1);
        final String description = m.group(2);

        final Alphabet alphabet;
        if (description.endsWith("dna")) {
            alphabet = Alphabet.DNA;
        } else if (description.endsWith("ami")) {
            alphabet = Alphabet.AMINO_ACIDS;
        } else {
            throw new FileFormatException("Unknown sequence type for \""+identifier+"\" ("+description+")");
        }

        StringBuffer content = new StringBuffer();
        while (true) {
            line = r.readLine();
            if (line == null || line.startsWith(">")) {
                break;
            }
            content.append(line);
        }
        return new Sequence(identifier, content.toString(), alphabet);
    }

}
