package de.tum.in.lrr.hmm.evaluation;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tum.in.lrr.hmm.Alphabet;
import de.tum.in.lrr.hmm.BatchTrainer;
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
public class BatchLearningTest {
    protected static final int MAX_DEPTH = 10;

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
            final SequenceReader trainingReader = new FastaReader(new FileReader(trainingFilename));
            final AbstractSequenceReader testReader = new FastaReader(new FileReader(testFilename));

            final List<Sequence> testSequences = testReader.readAllSequences();

            final List<Sequence> trainingSequences = new ArrayList<Sequence>();

            Sequence trainingSequence = trainingReader.readSequence();

            if (trainingSequence == null) {
                System.err.println("Warning: No sequences found");
                return;
            }
            Alphabet alphabet = trainingSequence.getAlphabet();
            Model model = new Model(alphabet, Model.Variant.PARTIAL_BACKLINKS);

            int trainingLength = 0;
            while (true) {
                model.learn(trainingSequence, MAX_DEPTH);

                trainingSequences.add(trainingSequence);
                trainingLength += trainingSequence.length();

                trainingSequence = trainingReader.readSequence();
                if (trainingSequence == null) {
                    break;
                }
                if (trainingSequence.getAlphabet() != alphabet) {
                    throw new FileFormatException("All sequences must be of the same type");
                }
            }


            System.out.println("Avg. test perplexity\ttraining perplexity\tparameter difference");

            final BatchTrainer trainer = new BatchTrainer(model);
            Model oldModel = model;

            while (true) {
                final double testPerplexity = oldModel.averagePerplexity(testSequences);
                final double trainingPerplexity = oldModel.averagePerplexity(trainingSequences);

                for (Sequence s : trainingSequences) {
                    trainer.learn(s, MAX_DEPTH);
                }
                final Model newModel = trainer.finishBatch();

                final double parameterDifference = oldModel.parameterDifference(newModel);

                System.out.println(testPerplexity+"\t"+trainingPerplexity+"\t"+parameterDifference);

                if (parameterDifference < 0.002) {
                    break;
                }

                oldModel = newModel;
            }
            System.err.println(oldModel.numStates()+" states.");


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

}
