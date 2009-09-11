package de.blacksheepsoftware.hmm;

import java.util.List;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public interface ISequence extends List<Integer> {
    int length();

    String getIdentifier();

    int getStartIndex();
    int getEndIndex();

    ISequence subSequencePreceding(SubSequence s);
    ISequence subSequenceFollowing(SubSequence s);

    public Alphabet getAlphabet();
}
