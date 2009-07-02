package de.blacksheepsoftware.t9;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Vector;


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
 
    public static int[] intArrayForString(String s) {
        String lowerCase = s.toLowerCase(Locale.ENGLISH);
        int length = s.length();
        int[] a = new int[length];
        for (int i=0; i<a.length; i++) {
            a[i] = lowerCase.codePointAt(i) - 96;
        }
        return a;
    }

    private static class IntArrayList extends AbstractList<Integer>
    implements RandomAccess, java.io.Serializable {
        private static final long serialVersionUID = 2357281897136237691L;
        private final int[] a;

        IntArrayList(int[] array) {
            if (array==null)
                throw new NullPointerException();
            a = array;
        }

        @Override
        public int size() {
            return a.length;
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        public int[] toIntArray() {
            return a.clone();
        }

        @Override
        public Integer get(int index) {
            return a[index];
        }

        public int set(int index, int element) {
            final int oldValue = a[index];
            a[index] = element;
            return oldValue;
        }

        @Override
        public int indexOf(Object o) {
            if (o!=null) {
                for (int i=0; i<a.length; i++) {
                    if (o.equals(a[i])) {
                        return i;
                    }
                }
            }
            return -1;
        }

        @Override
        public boolean contains(Object o) {
            return indexOf(o) != -1;
        }
    }

    public static Iterable<Integer> intArrayList(int[] array) {
        return new IntArrayList(array);
    }

    public static Iterable<Integer> characterSequenceForString(String s) {
        return intArrayList(intArrayForString(s));
    }
}
