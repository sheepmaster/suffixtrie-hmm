package de.tum.in.lrr.hmm.evaluation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

import de.tum.in.lrr.hmm.ISequence;
import de.tum.in.lrr.hmm.Model;
import de.tum.in.lrr.hmm.UniformModel;
import de.tum.in.lrr.hmm.gene.RandomSequence;
import de.tum.in.lrr.hmm.gene.ScoredSequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class RandomLocalSearchTest2 {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java "+RandomSequenceTest.class.getName()+" <HMM file>");
            System.exit(1);
        }
        String hmmFileName = args[0];
        try {
            final Model model = (Model)new ObjectInputStream(new GZIPInputStream(new FileInputStream(hmmFileName))).readObject();
            final UniformModel baseModel = new UniformModel(model.numCharacters());

            System.out.println("start index\tend index\tscore\tavg. score");

            for (int i = 0; true; i++) {
                final ISequence seq = new RandomSequence(model.getAlphabet()).generateSequence(400);

                final ScoredSequence search = ScoredSequence.search(model, baseModel, seq);

                System.out.println(search.getStartIndex()+"\t"+search.getEndIndex()+"\t"+search.score()+"\t"+(search.score()/(search.getEndIndex()-search.getStartIndex())));
                //                System.err.println(i+" hits\r");
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
