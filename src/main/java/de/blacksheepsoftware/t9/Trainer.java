package de.blacksheepsoftware.t9;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Vector;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public final class Trainer {

    private Trainer() {
        // empty constructor to prevent instantiation
    }

    protected static List<int[]> readWords(LineNumberReader r) throws IOException {
        List<int[]> words = new Vector<int[]>();

        while (r.ready()) {
            words.add(NumberKey.intArrayForString(r.readLine()));
        }

        return words;
    }

    public static int totalLength(Iterable<int[]> words) {
        int l = 0;

        for (int[] w : words) {
            l += w.length;
        }

        return l;
    }

    public static double totalPerplexity(Model m, Iterable<int[]> words) {
        double t = 0;

        for (int[] w : words) {
            t += m.perplexity(NumberKey.intArrayList(w));
        }

        return t;
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: java de.blacksheepsoftware.t9.Trainer <training file> <testing file> <output file>");
            System.exit(1);
        }

        final LineNumberReader r = new LineNumberReader(new FileReader(args[0]));

        List<int[]> trainingWords = readWords(r);

        final LineNumberReader r2 = new LineNumberReader(new FileReader(args[1]));

        List<int[]> testingWords = readWords(r2);

        Model model = new Model(26, Model.Variant.PARTIAL_BACKLINKS);

        final int totalLength = totalLength(testingWords);

        for (int[] w : trainingWords) {
            model.learn(w);

            double avgPerplexity = totalPerplexity(model, testingWords) / totalLength;

            System.out.println(model.numNodes+"\t"+avgPerplexity);
            //            System.err.print(".");
        }

        final ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(args[2]));

        os.writeObject(model);
        os.close();

    }

}
