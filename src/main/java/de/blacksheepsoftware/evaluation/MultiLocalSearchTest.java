package de.blacksheepsoftware.evaluation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import de.blacksheepsoftware.gene.FastaReader;
import de.blacksheepsoftware.gene.FileFormatException;
import de.blacksheepsoftware.gene.LocalSearch;
import de.blacksheepsoftware.gene.MultiLocalSearch;
import de.blacksheepsoftware.hmm.Model;
import de.blacksheepsoftware.hmm.Sequence;
import de.blacksheepsoftware.hmm.UniformModel;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class MultiLocalSearchTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java "+MultiLocalSearchTest.class.getName()+" <HMM file> <FASTA files...>");
            System.exit(1);
        }
        final String hmmFileName = args[0];
        final Vector<InputStream> files = new Vector<InputStream>();
        final InputStream input;
        if (args.length > 1) {
            for (int i=1; i<args.length; i++) {
                try {
                    files.add(new FileInputStream(args[i]));
                } catch (FileNotFoundException e) {
                    System.err.println("Warning: Couldn't find '" + args[i] + "'!");
                }
            }
            input = new SequenceInputStream(files.elements());
        } else {
            input = System.in;
        }
        final Reader r = new InputStreamReader(input);

        try {
            System.err.print("Reading model...");

            final Model model = (Model)new ObjectInputStream(new GZIPInputStream(new FileInputStream(hmmFileName))).readObject();

            System.err.println("done.");

            final UniformModel baseModel = new UniformModel(model.numCharacters());

            final FastaReader reader = new FastaReader(r);

            System.out.println("sequence\tfrom\tto\tscore");

            final Sequence s = reader.readSequence();
            if (s != null) {
                if (s.getAlphabet().numberOfCharacters() != model.numCharacters()) {
                    throw new FileFormatException("Sequence doesn't fit to model");
                }
                MultiLocalSearch searches = new MultiLocalSearch(model, baseModel, s);
                for (LocalSearch search : searches) {
                    final double score = search.sum() / Math.log(2);
                    if (score > 0) {
                        System.out.println(search+"\t"+search.startIndex()+
                                "\t"+search.endIndex()+"\t"+score);
                    } else {
                        System.err.println("muuh");
                    }
                }
            } else {
                throw new IllegalArgumentException();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
