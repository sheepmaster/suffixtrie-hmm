package de.tum.in.lrr.hmm.cli;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import de.tum.in.lrr.hmm.Alphabet;
import de.tum.in.lrr.hmm.BatchTrainer;
import de.tum.in.lrr.hmm.Model;
import de.tum.in.lrr.hmm.Sequence;
import de.tum.in.lrr.hmm.gene.EmblReader;
import de.tum.in.lrr.hmm.gene.FastaReader;
import de.tum.in.lrr.hmm.gene.FileFormatException;
import de.tum.in.lrr.hmm.gene.SequenceReader;
import de.tum.in.lrr.hmm.util.CloneableReader;
import de.tum.in.lrr.hmm.util.LinkedDataBlockReader;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class SequenceTrainer {

    public enum Format {
        Fasta,
        Embl
    }

    @Option(name = "--format", usage="file format")
    protected Format format;

    @Option(name = "--maxDepth", usage="maximum trie depth")
    protected int maxDepth = 8;

    @Option(name = "--maxIterations", usage="maximum number of iterations for batch learning")
    protected int maxIterations = Integer.MAX_VALUE;

    @Option(name = "--epsilon", usage="threshold for parameter difference")
    protected double parameterEpsilon = 0.002;

    @Argument
    protected List<String> arguments = new ArrayList<String>();

    /**
     * @param args
     */
    public static void main(String[] args) {
        SequenceTrainer trainer = new SequenceTrainer();
        trainer.doMain(args);

    }

    /**
     * @param args
     */
    private void doMain(String[] args) {
        final CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
            if (arguments.size() < 1) {
                throw new CmdLineException(parser, "No arguments given");
            }
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("Usage: java "+SequenceTrainer.class.getName()+" <options> <HMM file> <sequence files...>");
            parser.printUsage(System.err);
            System.exit(1);
        }

        String hmmFileName = arguments.remove(0);
        Vector<InputStream> files = new Vector<InputStream>();
        final InputStream input;
        if (arguments.isEmpty()) {
            input = System.in;
        } else {
            for (String s : arguments) {
                try {
                    files.add(new FileInputStream(s));
                } catch (FileNotFoundException e) {
                    System.err.println("Warning: Couldn't find '" + s + "'!");
                }
            }
            input = new SequenceInputStream(files.elements());
        }
        Reader r = new InputStreamReader(input);

        try {
            SequenceReader reader = reader(r);
            Sequence trainingSequence = reader.readSequence();

            if (trainingSequence == null) {
                System.err.println("Warning: No sequences found");
                return;
            }
            Alphabet alphabet = trainingSequence.getAlphabet();
            Model model = new Model(alphabet, Model.Variant.PARTIAL_BACKLINKS);

            final List<Sequence> trainingSequences = new ArrayList<Sequence>();

            int seqNo = 0;
            while (true) {
                model.learn(trainingSequence, maxDepth);
                System.out.print("\r"+(++seqNo)+" sequences read");
                trainingSequences.add(trainingSequence);
                trainingSequence = reader.readSequence();
                if (trainingSequence == null) {
                    break;
                }
                if (trainingSequence.getAlphabet() != alphabet) {
                    throw new FileFormatException("All sequences must be of the same type");
                }
            }

            System.out.println();

            final BatchTrainer trainer = new BatchTrainer(model);
            Model oldModel = model;

            int iteration = 0;

            while (iteration < maxIterations) {
                for (Sequence s : trainingSequences) {
                    trainer.learn(s, maxDepth);
                }
                System.out.print("\rIteration "+ iteration);
                final Model newModel = trainer.finishBatch();

                final double parameterDifference = oldModel.parameterDifference(newModel);

                if (parameterDifference < parameterEpsilon) {
                    break;
                }
                iteration++;

                oldModel = newModel;
            }

            System.out.println();

            final ObjectOutputStream output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(hmmFileName)));
            output.writeObject(model);
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param r
     * @throws IOException
     */
    private SequenceReader reader(Reader r1) throws IOException {
        final CloneableReader r = new LinkedDataBlockReader(r1);
        if (format == null) {
            format = guessFormat(r);
        }
        switch(format) {
        case Fasta:
            return new FastaReader(r);
        case Embl:
            return new EmblReader(r);
        default:
            throw new FileFormatException("Unknown file format");
        }
    }

    /**
     * @param r
     * @throws IOException
     */
    private static Format guessFormat(final CloneableReader r) throws IOException {
        String firstLine = new BufferedReader(r.clone()).readLine();
        if (firstLine == null) {
            return null;
        } else if (firstLine.startsWith(">")) {
            return Format.Fasta;
        } else if (firstLine.startsWith("ID ")) {
            return Format.Embl;
        } else {
            return null;
        }
    }

}
