package de.blacksheepsoftware.evaluation;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import de.blacksheepsoftware.gene.FastaReader;
import de.blacksheepsoftware.gene.FileFormatException;
import de.blacksheepsoftware.hmm.Alphabet;
import de.blacksheepsoftware.hmm.Model;
import de.blacksheepsoftware.hmm.Sequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class OnlineLearningTest2 {

    protected static final int MAX_DEPTH = 8;

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java "+OnlineLearningTest2.class.getName()+" <training sequences> <test sequences>");
            System.exit(1);
        }
        final String trainingFilename = args[0];
        final String testFilename = args[1];

        try {
            final FastaReader trainingReader = new FastaReader(new FileReader(trainingFilename));
            final FastaReader testReader = new FastaReader(new FileReader(testFilename));

            List<Sequence> testSequences = testReader.readAllSequences();

            Sequence trainingSequence = trainingReader.readSequence();

            if (trainingSequence == null) {
                System.err.println("Warning: No sequences found");
                return;
            }
            Alphabet alphabet = trainingSequence.getAlphabet();
            Model model = new Model(alphabet, Model.Variant.PARTIAL_BACKLINKS);

            System.out.println("Avg. perplexity of new word before learning\t...after learning\tAvg. perplexity of test set");

            while (true) {
                double newPerplexity = model.perplexity(trainingSequence)/trainingSequence.length();

                model.learn(trainingSequence, MAX_DEPTH);

                double posteriorPerplexity = model.perplexity(trainingSequence)/trainingSequence.length();

                double testPerplexity = model.averagePerplexity(testSequences);
                System.out.println(newPerplexity+"\t"+posteriorPerplexity+"\t"+testPerplexity);

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
