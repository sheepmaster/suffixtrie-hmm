package de.tum.in.lrr.hmm.evaluation;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import de.tum.in.lrr.hmm.Alphabet;
import de.tum.in.lrr.hmm.Model;
import de.tum.in.lrr.hmm.Sequence;
import de.tum.in.lrr.hmm.gene.AbstractSequenceReader;
import de.tum.in.lrr.hmm.gene.FastaReader;
import de.tum.in.lrr.hmm.gene.FileFormatException;
import de.tum.in.lrr.hmm.gene.SequenceReader;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class OnlineLearningTest {

    protected static final int MAX_DEPTH = 8;

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
            final SequenceReader trainingReader = new FastaReader(new FileReader(trainingFilename));
            final AbstractSequenceReader testReader = new FastaReader(new FileReader(testFilename));

            List<Sequence> testSequences = testReader.readAllSequences();

            Sequence trainingSequence = trainingReader.readSequence();

            if (trainingSequence == null) {
                System.err.println("Warning: No sequences found");
                return;
            }
            Alphabet alphabet = trainingSequence.getAlphabet();
            Model model = new Model(alphabet, Model.Variant.PARTIAL_BACKLINKS);


            while (true) {
                model.learn(trainingSequence, MAX_DEPTH);

                for (Sequence s : testSequences) {
                    System.out.print((model.perplexity(s)/s.length())+"\t");
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
