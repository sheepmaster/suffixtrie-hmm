package de.blacksheepsoftware.gene;

import java.util.ArrayList;
import java.util.List;

import de.blacksheepsoftware.hmm.Alphabet;
import de.blacksheepsoftware.hmm.ISequence;
import de.blacksheepsoftware.hmm.Sequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class AnnotatedSequence extends Sequence {

    public AnnotatedSequence(String identifier, String contents, Alphabet alphabet, int length) {
        super(identifier, contents, alphabet, length);
    }

    protected List<ISequence> subSequences = new ArrayList<ISequence>();

    public List<ISequence> getSubSequences() {
        return subSequences;
    }


}
