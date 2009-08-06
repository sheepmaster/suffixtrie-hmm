package de.blacksheepsoftware.t9;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Vector;

import de.blacksheepsoftware.hmm.Alphabet;
import de.blacksheepsoftware.hmm.Model;
import de.blacksheepsoftware.hmm.Sequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public final class WordTrainer {

    private WordTrainer() {
        // empty constructor to prevent instantiation
    }

    protected static List<Sequence> readWords(LineNumberReader r) throws IOException {
        List<Sequence> words = new Vector<Sequence>();

        while (r.ready()) {
            final String s = r.readLine();
            words.add(new Sequence(null, s, Alphabet.ABC, s.length()));
        }

        return words;
    }

    public static double totalPerplexity(Model m, Iterable<Sequence> words) {
        double t = 0;

        for (Sequence w : words) {
            t += m.perplexity(w);
        }

        return t;
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: java "+WordTrainer.class+" <training file> <testing file> <output file>");
            System.exit(1);
        }

        final LineNumberReader r = new LineNumberReader(new FileReader(args[0]));

        List<Sequence> trainingWords = readWords(r);

        final LineNumberReader r2 = new LineNumberReader(new FileReader(args[1]));

        List<Sequence> testingWords = readWords(r2);

        Model model = new Model(Alphabet.ABC, Model.Variant.PARTIAL_BACKLINKS);

        final int totalLength = Sequence.totalLength(testingWords);

        for (Sequence w : trainingWords) {
            model.learn(w);

            double avgPerplexity = totalPerplexity(model, testingWords) / totalLength;

            System.out.println(model.numStates()+"\t"+avgPerplexity);
            //            System.err.print(".");
        }

        final ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(args[2]));

        os.writeObject(model);
        os.close();

    }

}
