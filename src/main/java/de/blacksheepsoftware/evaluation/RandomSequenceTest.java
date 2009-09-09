package de.blacksheepsoftware.evaluation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import de.blacksheepsoftware.hmm.Model;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class RandomSequenceTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java "+RandomSequenceTest.class.getName()+" <HMM file>");
            System.exit(1);
        }
        String hmmFileName = args[0];
        try {
            final Model model = (Model)new ObjectInputStream(new GZIPInputStream(new FileInputStream(hmmFileName))).readObject();

            final RandomSequence seq = new RandomSequence(model.getAlphabet());
            final Iterator<Double> scores = model.sequenceIterator(seq.iterator());
            final double log2 = Math.log(2);

            while(true) {
                System.out.println(scores.next() / log2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
