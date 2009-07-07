package de.blacksheepsoftware.gene;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class FastaReader {

    protected static final Pattern headerPattern = Pattern.compile(">\\s*(\\S+)(?:\\s+(.*))?");

    public static List<Sequence> sequencesInFile(BufferedReader r) throws IOException {
        final Vector<Sequence> sequences = new Vector<Sequence>();

        Alphabet lastAlphabet = null;

        String line = r.readLine();
        while (line != null) {
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
                throw new FileFormatException("Unknown sequence type for \""+identifier+"\"");
            }
            if (lastAlphabet != null) {
                if (alphabet != lastAlphabet) {
                    throw new FileFormatException("All sequences must be of the same type");
                }
            } else {
                lastAlphabet = alphabet;
            }

            StringBuffer content = new StringBuffer();
            while (true) {
                line = r.readLine();
                if (line == null || line.startsWith(">")) {
                    break;
                }
                content.append(line);
            }
            sequences.add(new Sequence(identifier, content.toString(), alphabet));
        }

        return sequences;
    }

}
