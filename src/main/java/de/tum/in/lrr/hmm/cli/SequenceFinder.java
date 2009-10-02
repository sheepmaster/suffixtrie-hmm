package de.tum.in.lrr.hmm.cli;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import de.tum.in.lrr.hmm.ISequence;
import de.tum.in.lrr.hmm.Model;
import de.tum.in.lrr.hmm.SubSequence;
import de.tum.in.lrr.hmm.UniformModel;
import de.tum.in.lrr.hmm.gene.AnnotatedSequence;
import de.tum.in.lrr.hmm.gene.EmblReader;
import de.tum.in.lrr.hmm.gene.FileFormatException;
import de.tum.in.lrr.hmm.gene.ModelCalibration;
import de.tum.in.lrr.hmm.gene.ScoredSequence;
import de.tum.in.lrr.hmm.gene.SoftMax;
import de.tum.in.lrr.hmm.gene.SubSequenceSearch;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class SequenceFinder {

    @Option(name = "--maxhits", usage = "maximum number of hits to output")
    int maxHits = Integer.MAX_VALUE;

    @Option(name = "--scorethreshold", usage = "score threshold")
    double scoreThreshold = -Double.MAX_VALUE;

    @Option(name = "--pthreshold", usage = "probability threshold")
    double pThreshold = 0;

    enum Mode {
        Genome,
        Gene
    }

    @Option(name = "--mode", usage = "genome or gene")
    Mode mode = Mode.Genome;

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
            System.err.println("Usage: java "+SequenceFinder.class.getName()+" <options> <HMM file> <sequence file>");
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

            final EmblReader reader = new EmblReader(new BufferedReader(r));

            while (true) {

                final AnnotatedSequence fullSequence = reader.readSequence();
                if (fullSequence == null) {
                    break;
                }

                if (fullSequence.getAlphabet().numberOfCharacters() != model.numCharacters()) {
                    throw new FileFormatException("Sequence doesn't fit to model");
                }

                System.out.println("genome search results:");
                SubSequenceSearch searches = new SubSequenceSearch(model, baseModel, fullSequence);
                printHits(fullSequence, new SoftMax(searches, maxHits), calibration);

                System.out.println("coding sequences:");
                final List<SubSequence> subSequences = fullSequence.getSubSequences();
                printHits(fullSequence, new SoftMax(subSequences, model, baseModel, maxHits), calibration);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    void printHits(ISequence fullSequence, SoftMax m, ModelCalibration c) {
        System.out.println("sequence\trange\tscore\tprobability");
        for (ScoredSequence sequence : m) {
            final double p = m.probability(sequence);
            if (p < pThreshold) {
                break;
            }
            final double score = sequence.score() / LOG_2;
            if (score < scoreThreshold) {
                break;
            }
            System.out.println(fullSequence+"\t"+sequence.getRange()+"\t"+score+"\t"+p+"\t"+c.bitScore(sequence)+"\t"+c.eValue(sequence)+"\t"+c.pValue(sequence));
        }
    }

}
