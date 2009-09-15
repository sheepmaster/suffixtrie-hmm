package de.blacksheepsoftware.evaluation;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import de.blacksheepsoftware.gene.AbstractSequenceReader;
import de.blacksheepsoftware.gene.FastaReader;
import de.blacksheepsoftware.gene.FileFormatException;
import de.blacksheepsoftware.gene.SequenceReader;
import de.blacksheepsoftware.hmm.Alphabet;
import de.blacksheepsoftware.hmm.Model;
import de.blacksheepsoftware.hmm.Sequence;

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
