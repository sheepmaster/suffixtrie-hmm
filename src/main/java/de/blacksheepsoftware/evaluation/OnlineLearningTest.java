package de.blacksheepsoftware.evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.blacksheepsoftware.gene.Alphabet;
import de.blacksheepsoftware.gene.FastaReader;
import de.blacksheepsoftware.gene.FileFormatException;
import de.blacksheepsoftware.gene.Sequence;
import de.blacksheepsoftware.hmm.IntArrayList;
import de.blacksheepsoftware.hmm.Model;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class OnlineLearningTest {

    protected static final int MAX_DEPTH = 8;

    public static List<List<Integer>> readAllSequences(FastaReader r) throws IOException {
        List<List<Integer>> testSequences = new ArrayList<List<Integer>>();
        Sequence s;
        while ((s = r.readSequence()) != null) {
            System.out.print(s.getIdentifier()+"\t");
            testSequences.add(new IntArrayList(s, s.length()));
        }
        System.out.println();
        return testSequences;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java "+OnlineLearningTest.class.getName()+" <training sequences> <test sequences>");
            System.exit(1);
        }
        final String trainingFilename = args[0];
        final String testFilename = args[1];

        try {
            final FastaReader trainingReader = new FastaReader(new BufferedReader(new FileReader(trainingFilename)));
            final FastaReader testReader = new FastaReader(new BufferedReader(new FileReader(testFilename)));

            List<List<Integer>> testSequences = readAllSequences(testReader);
            int totalLength = 0;
            for (List<Integer> s : testSequences) {
                totalLength += s.size();
            }

            Sequence trainingSequence = trainingReader.readSequence();

            if (trainingSequence == null) {
                System.err.println("Warning: No sequences found");
                return;
            }
            Alphabet alphabet = trainingSequence.getAlphabet();
            Model model = new Model(alphabet.numberOfCharacters(), Model.Variant.PARTIAL_BACKLINKS);


            while (true) {
                model.learn(IntArrayList.forList(trainingSequence, trainingSequence.length()), MAX_DEPTH);

                for (List<Integer> s : testSequences) {
                    System.out.print((model.perplexity(s)/s.size())+"\t");
                }
                System.out.println();

                trainingSequence = trainingReader.readSequence();
                if (trainingSequence == null) {
                    break;
                }
                if (trainingSequence.getAlphabet() != alphabet) {
                    throw new FileFormatException("All sequences must be of the same type");
                }
            }



        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

}
