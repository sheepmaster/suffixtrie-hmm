package de.tum.in.lrr.hmm;

import java.util.List;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public interface ISequence extends List<Byte> {
    int length();

    String getIdentifier();

    int getStartIndex();
    int getEndIndex();

    ISequence subSequencePreceding(SubSequence s);
    ISequence subSequenceFollowing(SubSequence s);

    public Alphabet getAlphabet();
}
