package de.tum.in.lrr.hmm;

import java.util.regex.Matcher;

import de.tum.in.lrr.hmm.util.ByteBuffer;


/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class Sequence extends AbstractSequence {

    protected final String identifier;
    protected final Alphabet alphabet;
    protected byte[] charSequence = null;

    public Sequence(String identifier, final String contents, final Alphabet alphabet, int length) {
        this(identifier, alphabet, parseSequence(contents, alphabet, new ByteBuffer(length)));
    }

    public Sequence(String identifier, final String contents, final Alphabet alphabet) {
        this(identifier, alphabet, parseSequence(contents, alphabet));
    }

    public Sequence(String identifier, Alphabet alphabet, byte[] sequence) {
        this.charSequence = sequence;
        this.alphabet = alphabet;
        this.identifier = identifier;
    }

    public static byte[] parseSequence(String sequence, Alphabet alphabet) {
        return parseSequence(sequence, alphabet, new ByteBuffer());
    }

    public static byte[] parseSequence(String sequence, Alphabet alphabet, ByteBuffer buf) {
        final Matcher matcher = alphabet.getSymbolPattern().matcher(sequence);
        while (matcher.find()) {
            buf.append(alphabet.indexOfSymbol(matcher.group().charAt(0)));
        }
        return buf.toByteArray();
    }

    public Alphabet getAlphabet() {
        return alphabet;
    }

    /**
     * @return
     */
    public String getIdentifier() {
        return identifier;
    }

    public int length() {
        return charSequence.length;
    }

    public byte[] charSequence() {
        return charSequence;
    }

    @Override
    public Byte get(int i) {
        return charSequence[i];
    }

}
