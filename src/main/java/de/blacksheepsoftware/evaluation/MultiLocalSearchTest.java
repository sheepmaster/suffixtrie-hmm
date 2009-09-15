package de.blacksheepsoftware.evaluation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import de.blacksheepsoftware.gene.AnnotatedSequence;
import de.blacksheepsoftware.gene.EmblReader;
import de.blacksheepsoftware.gene.FileFormatException;
import de.blacksheepsoftware.gene.MultiLocalSearch;
import de.blacksheepsoftware.gene.Normalizer;
import de.blacksheepsoftware.gene.ScoredSequence;
import de.blacksheepsoftware.hmm.ISequence;
import de.blacksheepsoftware.hmm.Model;
import de.blacksheepsoftware.hmm.SubSequence;
import de.blacksheepsoftware.hmm.UniformModel;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class MultiLocalSearchTest {

    /**
     * 
     */
    private static final double LOG_2 = Math.log(2);

    public static double[] softMax(double[] array) {
        double[] softMax = new double[array.length];
        double max = Double.NEGATIVE_INFINITY;
        for (double d : array) {
            max = Math.max(max, d);
        }
        double expSum = 0;
        for (int i=0; i<array.length; i++) {
            final double exp = Math.exp(array[i]-max);
            softMax[i] = exp;
            expSum += exp;
        }
        for (int i=0; i<array.length; i++) {
            softMax[i] /= expSum;
        }

        return softMax;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java "+MultiLocalSearchTest.class.getName()+" <HMM file> <EMBL file>");
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


            final AnnotatedSequence fullSequence = reader.readSequence();

            if (fullSequence.getAlphabet().numberOfCharacters() != model.numCharacters()) {
                throw new FileFormatException("Sequence doesn't fit to model");
            }

            System.out.println("sequence\trange\tscore\tprobability");
            MultiLocalSearch searches = new MultiLocalSearch(model, baseModel, fullSequence);
            Normalizer n = new Normalizer(searches.iterator());
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
            n = new Normalizer(subSequences, model, baseModel);
            for (ScoredSequence sequence : n) {
                System.out.println(sequence+"\t"+sequence.score()/LOG_2+"\t"+n.probability(sequence));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
