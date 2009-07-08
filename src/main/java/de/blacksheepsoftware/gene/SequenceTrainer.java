package de.blacksheepsoftware.gene;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.SequenceInputStream;
import java.util.List;
import java.util.Vector;

import de.blacksheepsoftware.hmm.Model;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class SequenceTrainer {

    protected static final int MAX_DEPTH = 8;

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java "+SequenceTrainer.class.getName()+" <HMM file> <FASTA files...>");
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
            final List<Sequence> sequences = FastaReader.sequencesInFile(r);

            if (sequences.isEmpty()) {
                System.err.println("Warning: No sequences found");
                return;
            }
            Alphabet alphabet = sequences.get(0).getAlphabet();
            Model model = new Model(alphabet.numberOfCharacters(), Model.Variant.PARTIAL_BACKLINKS);

            for (Sequence s : sequences) {
                model.learn(IntArray.forList(s), MAX_DEPTH);
            }

            new ObjectOutputStream(new FileOutputStream(hmmFileName)).writeObject(model);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
