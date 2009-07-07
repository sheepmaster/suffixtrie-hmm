package de.blacksheepsoftware.gene;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class Alphabet {

    protected static final Alphabet DNA = new Alphabet("ACGT");
    protected static final Alphabet AMINO_ACIDS = new Alphabet("ACDEFGHIKLPQRSTUVWY");

    protected final Map<Character,Integer> alphabetMap = new HashMap<Character, Integer>();
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
            alphabetMap.put(c, i+1);
        }
    }

    public int numberOfCharacters() {
        return alphabetMap.size();
    }

    public int indexOfSymbol(char c) {
        return alphabetMap.get(Character.toUpperCase(c));
    }

    public Pattern getSymbolPattern() {
        return symbolPattern;
    }

}
