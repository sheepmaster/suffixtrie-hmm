package de.blacksheepsoftware.evaluation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

import de.blacksheepsoftware.gene.LocalSearch;
import de.blacksheepsoftware.hmm.Model;
import de.blacksheepsoftware.hmm.UniformModel;

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

            System.out.println("start index\tend index\tscore");

            for (int i = 0; true; i++) {
                final RandomSequence seq = new FiniteRandomSequence(model.numCharacters(), 400);

                final LocalSearch search = new LocalSearch(model, baseModel, seq);

                System.out.println(search.startIndex()+"\t"+search.endIndex()+"\t"+search.sum());
                //                System.err.println(i+" hits\r");
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
