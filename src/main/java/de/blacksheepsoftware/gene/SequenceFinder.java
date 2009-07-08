package de.blacksheepsoftware.gene;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.SequenceInputStream;
import java.util.List;
import java.util.Vector;

import de.blacksheepsoftware.hmm.Model;
import de.blacksheepsoftware.hmm.UniformBaseModel;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class SequenceFinder {

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java "+SequenceFinder.class.getName()+" <HMM file> <FASTA files...>");
            System.exit(1);
        }
        String hmmFileName = args[0];
        Vector<InputStream> files = new Vector<InputStream>();
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
        BufferedReader r = new BufferedReader(new InputStreamReader(input));

        try {
            Model model = (Model)new ObjectInputStream(new FileInputStream(hmmFileName)).readObject();

            final List<Sequence> sequences = FastaReader.sequencesInFile(r);

            if (sequences.isEmpty()) {
                System.err.println("Warning: No sequences found");
                return;
            }

            final UniformBaseModel baseModel = new UniformBaseModel(model.numCharacters());
            for (Sequence s : sequences) {
                final LocalSearch search = new LocalSearch(model, baseModel, s);
                final double score = search.sum();
                if (score > 0) {
                    System.out.println("Found match for sequence "+s.getIdentifier()+": from "+search.startIndex()+" to "+search.endIndex()+" (score "+score+")");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
