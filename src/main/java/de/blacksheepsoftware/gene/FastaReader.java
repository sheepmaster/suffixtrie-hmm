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

        String line;
        StringBuffer sb;
        while ((line = r.readLine()) != null) {
            if (line.startsWith(">")) {
                sb.toString();

                // header line: start new sequence
                Matcher m = headerPattern.matcher(line);
                if (!m.matches()) {
                    throw new Exception(); // XXX
                }
                String identifier = m.group(1);
                String description = m.group(2);
                sb = new StringBuffer();
            }
        }

        return sequences;
    }

}
