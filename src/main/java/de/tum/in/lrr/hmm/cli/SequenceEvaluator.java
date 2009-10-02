package de.tum.in.lrr.hmm.cli;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import de.tum.in.lrr.hmm.Model;
import de.tum.in.lrr.hmm.Sequence;
import de.tum.in.lrr.hmm.SubSequence;
import de.tum.in.lrr.hmm.UniformModel;
import de.tum.in.lrr.hmm.gene.AbstractSequenceReader;
import de.tum.in.lrr.hmm.gene.FileFormatException;
import de.tum.in.lrr.hmm.gene.ModelCalibration;
import de.tum.in.lrr.hmm.gene.SequenceReader;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class SequenceEvaluator {

    @Option(name = "--maxhits", usage = "maximum number of hits to output")
    int maxHits = Integer.MAX_VALUE;

    @Option(name = "--scorethreshold", usage = "score threshold")
    double scoreThreshold = -Double.MAX_VALUE;

    @Option(name = "--pthreshold", usage = "probability threshold")
    double pThreshold = 0;

    @Option(name = "--format", usage="file format (FASTA or EMBL)")
    protected SequenceReader.Format format = null;

    @Argument
    protected List<String> arguments = new ArrayList<String>();

    private static final double LOG_2 = Math.log(2);

    /**
     * @param args
     */
    public static void main(String[] args) {
        new SequenceFinder().doMain(args);
    }

    public void doMain(String[] args) {
        final CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
            if (arguments.size() != 2) {
                throw new CmdLineException(parser, "Expected two arguments");
            }
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("Usage: java "+SequenceEvaluator.class.getName()+" <options> <HMM file> <sequence file>");
            parser.printUsage(System.err);
            System.exit(1);
        }

        final String hmmFileName = arguments.get(0);

        try {
            final Reader r = new FileReader(arguments.get(1));

            System.err.print("Reading model...");

            final Model model = (Model)new ObjectInputStream(new GZIPInputStream(new FileInputStream(hmmFileName))).readObject();

            System.err.println("done.");

            final UniformModel baseModel = new UniformModel(model.numCharacters());

            System.err.print("Calibrating...");

            final ModelCalibration calibration = new ModelCalibration(model, baseModel);

            System.err.println("done");

            final SequenceReader reader = AbstractSequenceReader.create(r, format);

            final List<SubSequence> subSequences = new ArrayList<SubSequence>();
            while (true) {

                final Sequence fullSequence = reader.readSequence();

                if (fullSequence == null) {
                    break;
                }

                if (fullSequence.getAlphabet().numberOfCharacters() != model.numCharacters()) {
                    throw new FileFormatException("Sequence doesn't fit to model");
                }

                double s = 0;
                Iterator<Double> modelIterator = model.scoringIterator(fullSequence.iterator());
                Iterator<Double> baseModelIterator = baseModel.scoringIterator(fullSequence.iterator());
                while (modelIterator.hasNext()) {
                    s += baseModelIterator.next() - modelIterator.next();
                }

                System.out.println(fullSequence+"\t"+(s/Math.log(2)));
                //                subSequences.add(new SubSequence(fullSequence, 0, fullSequence.length()));
            }


            //            final SoftMax softmax = new SoftMax(subSequences, model, baseModel, maxHits);
            //
            //            if (!subSequences.isEmpty()) {
            //                System.out.println("range\tscore\tprobability");
            //                for (ScoredSequence sequence : softmax) {
            //                    final double p = softmax.probability(sequence);
            //                    if (p < pThreshold) {
            //                        break;
            //                    }
            //                    final double score = sequence.score() / LOG_2;
            //                    if (score < scoreThreshold) {
            //                        break;
            //                    }
            //                    System.out.println(sequence.getRange()+"\t"+score+"\t"+p+"\t"+calibration.bitScore(sequence)+"\t"+calibration.eValue(sequence)+"\t"+calibration.pValue(sequence));
            //                }
            //            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
