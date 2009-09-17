package de.tum.in.lrr.hmm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class Alphabet implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Alphabet ABC = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    public static final Alphabet DNA = new Alphabet("ACGT");
    public static final Alphabet AMINO_ACIDS = new Alphabet("ACDEFGHIKLPQRSTUVWY");

    protected final Map<Character,Byte> alphabetMap = new HashMap<Character, Byte>();
    protected final Pattern symbolPattern;

    public Alphabet(String alph) {
        String alphabet = alph.toUpperCase();

        if (!alphabet.matches("[A-Z]+")) {
            throw new IllegalArgumentException();
        }
        symbolPattern = Pattern.compile("["+alphabet+"]", Pattern.CASE_INSENSITIVE);

        for (int i=0; i<alphabet.length(); i++) {
            final char c = alphabet.charAt(i);
            if (alphabetMap.containsKey(c)) {
                throw new IllegalArgumentException("Alphabet may not contain duplicate symbols");
            }
            alphabetMap.put(c, (byte)(i+1));
        }
    }

    public int numberOfCharacters() {
        return alphabetMap.size();
    }

    public byte indexOfSymbol(char c) {
        return alphabetMap.get(Character.toUpperCase(c));
    }

    public Pattern getSymbolPattern() {
        return symbolPattern;
    }

}
