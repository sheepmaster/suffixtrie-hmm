package de.blacksheepsoftware.evaluation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import de.blacksheepsoftware.gene.AnnotatedSequence;
import de.blacksheepsoftware.gene.EmblReader;
import de.blacksheepsoftware.gene.FileFormatException;
import de.blacksheepsoftware.gene.MultiLocalSearch;
import de.blacksheepsoftware.gene.ScoredSequence;
import de.blacksheepsoftware.gene.SoftMax;
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

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java "+MultiLocalSearchTest.class.getName()+" <HMM file> <sequence file>");
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

            System.out.println("sequence\trange\tscore\tprobability");

            while (reader.ready()) {

                final AnnotatedSequence fullSequence = reader.readSequence();

                if (fullSequence.getAlphabet().numberOfCharacters() != model.numCharacters()) {
                    throw new FileFormatException("Sequence doesn't fit to model");
                }

                final MultiLocalSearch searches = new MultiLocalSearch(model, baseModel, fullSequence);
                final SoftMax n1 = new SoftMax(searches.iterator());

                final ScoredSequence s1 = n1.iterator().next();

                final double score = s1.score() / LOG_2;
                final ISequence searchRange = s1.getContainingSequence();
                System.out.print(fullSequence+"\t"+(searchRange.getStartIndex()+s1.getStartIndex()+1)+
                        ".."+(searchRange.getStartIndex()+s1.getEndIndex())+"\t"+score+"\t"+n1.probability(s1));

                final List<SubSequence> subSequences = fullSequence.getSubSequences();
                final SoftMax n2 = new SoftMax(subSequences, model, baseModel);

                final Iterator<ScoredSequence> iterator = n2.iterator();
                if (iterator.hasNext()) {
                    final ScoredSequence s2 = iterator.next();

                    System.out.print("\t"+(s2.getStartIndex()+1)+".."+s2.getEndIndex()+"\t"+s2.score()/LOG_2+"\t"+n2.probability(s2));
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
