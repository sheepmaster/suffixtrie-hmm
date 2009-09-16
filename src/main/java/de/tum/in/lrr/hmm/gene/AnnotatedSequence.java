package de.tum.in.lrr.hmm.gene;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.lrr.hmm.Alphabet;
import de.tum.in.lrr.hmm.Sequence;
import de.tum.in.lrr.hmm.SubSequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class AnnotatedSequence extends Sequence {

    public AnnotatedSequence(String identifier, String contents, Alphabet alphabet, int length) {
        super(identifier, contents, alphabet, length);
    }

    protected List<SubSequence> subSequences = new ArrayList<SubSequence>();

    public List<SubSequence> getSubSequences() {
        return subSequences;
    }


}
