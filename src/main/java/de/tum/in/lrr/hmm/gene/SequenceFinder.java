package de.tum.in.lrr.hmm.gene;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import de.tum.in.lrr.hmm.ISequence;
import de.tum.in.lrr.hmm.Model;
import de.tum.in.lrr.hmm.SubSequence;
import de.tum.in.lrr.hmm.UniformModel;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class SequenceFinder {

    /**
     * 
     */
    private static final double LOG_2 = Math.log(2);

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java "+SequenceFinder.class.getName()+" <HMM file> <sequence file>");
            System.exit(1);
        }
        final String hmmFileName = args[0];

        try {
            final Reader r = new FileReader(args[1]);

            System.err.print("Reading model...");

            final Model model = (Model)new ObjectInputStream(new GZIPInputStream(new FileInputStream(hmmFileName))).readObject();

            System.err.println("done.");

            final UniformModel baseModel = new UniformModel(model.numCharacters());

            final EmblReader reader = new EmblReader(new BufferedReader(r));

            while (reader.ready()) {

                final AnnotatedSequence fullSequence = reader.readSequence();

                if (fullSequence.getAlphabet().numberOfCharacters() != model.numCharacters()) {
                    throw new FileFormatException("Sequence doesn't fit to model");
                }

                System.out.println("sequence\trange\tscore\tprobability");
                MultiLocalSearch searches = new MultiLocalSearch(model, baseModel, fullSequence);
                SoftMax n = new SoftMax(searches);
                for (ScoredSequence sequence : n) {
                    final double score = sequence.score() / LOG_2;
                    if (score > 0) {
                        final ISequence searchRange = sequence.getContainingSequence();
                        System.out.println(searchRange+"\t"+(searchRange.getStartIndex()+sequence.getStartIndex())+
                                ".."+(searchRange.getStartIndex()+sequence.getEndIndex())+"\t"+score+"\t"+n.probability(sequence));
                    } else {
                        System.err.println("muuh");
                    }
                }

                System.out.println("hit\tscore\tprobability");
                final List<SubSequence> subSequences = fullSequence.getSubSequences();
                n = new SoftMax(subSequences, model, baseModel);
                for (ScoredSequence sequence : n) {
                    System.out.println(sequence+"\t"+sequence.score()/LOG_2+"\t"+n.probability(sequence));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
