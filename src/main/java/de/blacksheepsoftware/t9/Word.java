package de.blacksheepsoftware.t9;



/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class Word implements Comparable<Word>{
    protected StateDistribution state;
    protected final String string;
    protected final double score;
    
    protected Word(StateDistribution state, String characters, double score) {
        this.state = state;
        this.string = characters;
        this.score = score;
    }

    public Word(StateDistribution s) {
        this(s, "", 0);
    }
    
    public Word push(char c) {
        final StateDistribution newState = state.read(NumberKey.intForChar(c));
        return new Word(newState, string + c, score + newState.normalize());
    }
    
    public String toString() {
        return string;
    }
    
    public double score() {
        return score;
    }
    
    public Word clearState() {
        state = null;
        return this;
    }

    public int compareTo(Word arg0) {
        return Double.compare(score, arg0.score);
    }
}
