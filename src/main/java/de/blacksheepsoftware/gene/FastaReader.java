package de.blacksheepsoftware.gene;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.blacksheepsoftware.common.LazyLinkedList;
import de.blacksheepsoftware.common.LineIterator;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class FastaReader {

    protected static final Pattern headerPattern = Pattern.compile(">\\s*(\\S+)(?:\\s+(.*))?");

    public static List<Sequence> sequencesInFile(BufferedReader r) throws IOException {
        final Vector<Sequence> sequences = new Vector<Sequence>();

        LineIterator it = new LineIterator(r);
        LazyLinkedList<String> l = LazyLinkedList.create(it);

        while (l != null) {
            String header = l.head();

            Matcher m = headerPattern.matcher(header);
            if (!m.matches()) {
                throw new RuntimeException(); // XXX
            }
            final String identifier = m.group(1);
            final String description = m.group(2);

            final String alphabet = "ACGT"; // XXX

            StringBuffer content = new StringBuffer();

            while ((l = l.tail()) != null) {
                String line = l.head();
                if (line.startsWith(">")) {
                    break;
                }
                content.append(line);
            }
            sequences.add(new Sequence(identifier, content.toString(), alphabet));
        }

        if (it.getIOException() != null) {
            throw it.getIOException();
        }

        return sequences;
    }

}
