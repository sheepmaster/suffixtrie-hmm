package de.blacksheepsoftware.evaluation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

import de.blacksheepsoftware.gene.AnnotatedSequence;
import de.blacksheepsoftware.gene.EmblReader;
import de.blacksheepsoftware.gene.FileFormatException;
import de.blacksheepsoftware.gene.LocalSearch;
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

            System.out.println("sequence\trange\tscore");
            MultiLocalSearch searches = new MultiLocalSearch(model, baseModel, seq);
            for (LocalSearch search : searches) {
                final double score = search.sum() / Math.log(2);
                if (score > 0) {
                    final ISequence s = search.getContainingSequence();
                    System.out.println(s+"\t"+(s.startIndex()+search.startIndex())+
                            ".."+(s.startIndex()+search.endIndex())+"\t"+score);
                } else {
                    System.err.println("muuh");
                }
            }

            System.out.println("hit\tscore");
            for (ISequence s : seq.getSubSequences()) {
                final double perplexity = model.perplexity(s);
                final double score = 2*s.length()-perplexity;
                System.out.println(s+"\t"+score);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
