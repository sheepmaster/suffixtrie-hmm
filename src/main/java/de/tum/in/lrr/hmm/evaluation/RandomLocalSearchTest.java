package de.tum.in.lrr.hmm.evaluation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import de.tum.in.lrr.hmm.Model;
import de.tum.in.lrr.hmm.UniformModel;
import de.tum.in.lrr.hmm.gene.RandomSequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class RandomLocalSearchTest {

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
            final UniformModel baseModel = new UniformModel(model.numCharacters());

            final RandomSequence seq = new RandomSequence(model.getAlphabet(), 42);

            double maxSum = 0.0;

            System.out.println("start index\tend index\tscore");

            final Iterator<Byte> iterator1 = seq.iterator();
            final Iterator<Byte> iterator2 = seq.iterator();
            Iterator<Double> modelIterator = model.scoringIterator(iterator1);
            Iterator<Double> baseModelIterator = baseModel.scoringIterator(iterator2);
            int startIndex = 0;
            int endIndex = 0;
            double sum = 0.0;
            while(modelIterator.hasNext()) {
                endIndex++;
                sum += baseModelIterator.next() - modelIterator.next();
                if (sum <= 0) {
                    sum = 0;
                    startIndex = endIndex;
                    modelIterator = model.scoringIterator(iterator1);
                    baseModelIterator = baseModel.scoringIterator(iterator2);
                }
                if (sum > maxSum) {
                    maxSum = sum;
                    System.out.println(startIndex + "\t" + endIndex + "\t" + sum);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
