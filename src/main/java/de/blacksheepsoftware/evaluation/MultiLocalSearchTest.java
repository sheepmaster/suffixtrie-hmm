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
import de.blacksheepsoftware.gene.ScoredSequence;
import de.blacksheepsoftware.gene.MultiLocalSearch;
import de.blacksheepsoftware.hmm.ISequence;
import de.blacksheepsoftware.hmm.Model;
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


            final AnnotatedSequence seq = reader.readAnnotatedSequence();

            if (seq.getAlphabet().numberOfCharacters() != model.numCharacters()) {
                throw new FileFormatException("Sequence doesn't fit to model");
            }

            System.out.println("sequence\trange\tscore\texpsum");
            MultiLocalSearch searches = new MultiLocalSearch(model, baseModel, seq);
            double expSum = 0;
            double max = Double.NEGATIVE_INFINITY;
            for (ScoredSequence search : searches) {
                final double score = search.score() / LOG_2;
                if (score > 0) {
                    if (max == Double.NEGATIVE_INFINITY) {
                        max = search.score();
                    }
                    expSum += Math.exp(search.score() - max);
                    final ISequence s = search.getContainingSequence();
                    System.out.println(s+"\t"+(s.getStartIndex()+search.getStartIndex())+
                            ".."+(s.getStartIndex()+search.getEndIndex())+"\t"+score+"\t"+expSum);
                } else {
                    System.err.println("muuh");
                }
            }

            System.out.println("hit\tscore\tprobability");
            final List<ISequence> subSequences = seq.getSubSequences();
            double[] scores = new double[subSequences.size()];
            for (int i=0; i<scores.length; i++) {
                ISequence s = subSequences.get(i);
                final double perplexity = model.perplexity(s);
                scores[i] = LOG_2*(2*s.length()-perplexity);
            }
            double[] probabilities = softMax(scores);
            for (int i=0; i<scores.length; i++) {
                System.out.println(subSequences.get(i)+"\t"+scores[i]/LOG_2+"\t"+probabilities[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
