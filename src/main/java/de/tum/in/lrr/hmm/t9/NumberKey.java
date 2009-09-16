package de.tum.in.lrr.hmm.t9;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import de.tum.in.lrr.hmm.Alphabet;
import de.tum.in.lrr.hmm.Sequence;


public class NumberKey implements CharacterTemplate {

    private static final char[] abc = new char[]{'a', 'b', 'c'};
    private static final char[] def = new char[]{'d', 'e', 'f'};
    private static final char[] ghi = new char[]{'g', 'h', 'i'};
    private static final char[] jkl = new char[]{'j', 'k', 'l'};
    private static final char[] mno = new char[]{'m', 'n', 'o'};
    private static final char[] pqrs = new char[]{'p', 'q', 'r', 's'};
    private static final char[] tuv = new char[]{'t', 'u', 'v'};
    private static final char[] wxyz = new char[]{'w', 'x', 'y', 'z'};
    private static final char[][] characters = new char[][]{abc, def, ghi, jkl, mno, pqrs, tuv, wxyz};

    protected static final Map<Character, Integer> numbers = new HashMap<Character, Integer>();

    static {
        for (int i=0; i<characters.length; i++) {
            for (char c : characters[i]) {
                numbers.put(c, i+2);
            }
        }
    }

    protected final int number;

    public NumberKey(int number) {
        this.number = number;
    }

    public NumberKey(char c) {
        number = numbers.get(c);
    }

    public char[] characters() {
        return characters[number-2];
    }

    public static Sequence sequenceForWord(String word) {
        return new Sequence(null, word, Alphabet.ABC, word.length());
    }

    protected static List<NumberKey> numberKeysForString(String s) {
        final String lc = s.toLowerCase(Locale.ENGLISH);

        Vector<NumberKey> numberKeys = new Vector<NumberKey>();

        for (int i = 0; i < lc.length(); i++) {
            numberKeys.add(new NumberKey(lc.charAt(i)));
        }
        return numberKeys;
    }

    public static int intForChar(char c) {
        final String s = String.valueOf(c);
        return s.codePointAt(0) - 96;
    }

}
