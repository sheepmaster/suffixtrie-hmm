package de.blacksheepsoftware.evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.blacksheepsoftware.gene.FastaReader;
import de.blacksheepsoftware.gene.FileFormatException;
import de.blacksheepsoftware.hmm.Alphabet;
import de.blacksheepsoftware.hmm.BatchTrainer;
import de.blacksheepsoftware.hmm.Model;
import de.blacksheepsoftware.hmm.Sequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class BatchLearningTest {
    protected static final int MAX_DEPTH = 8;

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java "+BatchLearningTest.class.getName()+" <training sequences> <test sequences>");
            System.exit(1);
        }
        final String trainingFilename = args[0];
        final String testFilename = args[1];

        try {
            final FastaReader trainingReader = new FastaReader(new BufferedReader(new FileReader(trainingFilename)));
            final FastaReader testReader = new FastaReader(new BufferedReader(new FileReader(testFilename)));

            List<Sequence> testSequences = testReader.readAllSequences();
            int totalLength = 0;
            for (Sequence s : testSequences) {
                totalLength += s.length();
            }

            List<Sequence> trainingSequences = new ArrayList<Sequence>();

            Sequence trainingSequence = trainingReader.readSequence();

            if (trainingSequence == null) {
                System.err.println("Warning: No sequences found");
                return;
            }
            Alphabet alphabet = trainingSequence.getAlphabet();
            Model model = new Model(alphabet, Model.Variant.PARTIAL_BACKLINKS);

            while (true) {
                model.learn(trainingSequence, MAX_DEPTH);

                trainingSequences.add(trainingSequence);

                trainingSequence = trainingReader.readSequence();
                if (trainingSequence == null) {
                    break;
                }
                if (trainingSequence.getAlphabet() != alphabet) {
                    throw new FileFormatException("All sequences must be of the same type");
                }
            }

            System.out.println("Avg. test perplexity\tparameter difference");

            final BatchTrainer trainer = new BatchTrainer(model);
            Model oldModel = model;

            while (true) {
                double testPerplexity = 0;
                for (Sequence s : testSequences) {
                    testPerplexity += oldModel.perplexity(s);
                }
                testPerplexity /= totalLength;

                for (Sequence s : trainingSequences) {
                    trainer.learn(s, MAX_DEPTH);
                }
                final Model newModel = trainer.finishBatch();

                final double parameterDifference = oldModel.parameterDifference(newModel);

                System.out.println(testPerplexity+"\t"+parameterDifference);

                if (parameterDifference < 0.01 * model.numStates()) {
                    break;
                }

                oldModel = newModel;
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

}
