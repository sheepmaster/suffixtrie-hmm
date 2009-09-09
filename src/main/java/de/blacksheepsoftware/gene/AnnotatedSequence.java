package de.blacksheepsoftware.gene;

import java.util.ArrayList;
import java.util.List;

import de.blacksheepsoftware.hmm.Alphabet;
import de.blacksheepsoftware.hmm.Sequence;
import de.blacksheepsoftware.hmm.SubSequence;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class AnnotatedSequence extends Sequence {

    public AnnotatedSequence(String identifier, String contents, Alphabet alphabet, int length) {
        super(identifier, contents, alphabet, length);
        // TODO Auto-generated constructor stub
    }

    protected List<SubSequence> subSequences = new ArrayList<SubSequence>();

    public List<SubSequence> subSequences() {
        return subSequences;
    }


}
