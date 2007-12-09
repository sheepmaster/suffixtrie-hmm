package de.blacksheepsoftware.t9;

import java.util.HashMap;
import java.util.Map;

import apple.laf.ScaledImageIcon;

public class NumberKey {
	
	private static final char[] abc = new char[]{'a', 'b', 'c'};
	private static final char[] def = new char[]{'d', 'e', 'f'};
	private static final char[] ghi = new char[]{'g', 'h', 'i'};
	private static final char[] jkl = new char[]{'j', 'k', 'l'};
	private static final char[] mno = new char[]{'m', 'n', 'o'};
	private static final char[] pqrs = new char[]{'p', 'q', 'r', 's'};
	private static final char[] tuv = new char[]{'t', 'u', 'v'};
	private static final char[] wxyz = new char[]{'w', 'x', 'y', 'z'};
	protected static final char[][] characters = new char[][]{abc, def, ghi, jkl, mno, pqrs, tuv, wxyz};
	
	protected static final Map<Character, Integer> numbers = new HashMap<Character, Integer>();
	
	static {
		numbers.put(new Character('a'), new Integer(2));
		numbers.put(new Character('b'), new Integer(2));
		numbers.put(new Character('c'), new Integer(2));
		numbers.put(new Character('d'), new Integer(3));
		numbers.put(new Character('e'), new Integer(3));
		numbers.put(new Character('f'), new Integer(3));
		numbers.put(new Character('g'), new Integer(4));
		numbers.put(new Character('h'), new Integer(4));
		numbers.put(new Character('i'), new Integer(4));
		numbers.put(new Character('j'), new Integer(5));
		numbers.put(new Character('k'), new Integer(5));
		numbers.put(new Character('l'), new Integer(5));
		numbers.put(new Character('m'), new Integer(6));
		numbers.put(new Character('n'), new Integer(6));
		numbers.put(new Character('o'), new Integer(6));
		numbers.put(new Character('p'), new Integer(7));
		numbers.put(new Character('q'), new Integer(7));
		numbers.put(new Character('r'), new Integer(7));
		numbers.put(new Character('s'), new Integer(7));
		numbers.put(new Character('t'), new Integer(8));
		numbers.put(new Character('u'), new Integer(8));
		numbers.put(new Character('v'), new Integer(8));
		numbers.put(new Character('w'), new Integer(9));
		numbers.put(new Character('x'), new Integer(9));
		numbers.put(new Character('y'), new Integer(9));
		numbers.put(new Character('z'), new Integer(9));
	}
	
	protected final int number;
	
	public NumberKey(int number) {
		this.number = number;
	}
	
	public NumberKey(Character c) {
		number = numbers.get(c);
	}
	
	public char[] characters() {
		return characters[number-2];
	}
    
    public static int intForChar(char c) {
        return -1; // TODO
    }
    
    public static int[] intArrayForString(String s) {
        String upperCase = s.toUpperCase();
        int length = s.length();
        int[] a = new int[length];
        for (int i=0; i<a.length; i++) {
            a[i] = upperCase.codePointAt(i) - 64;
        }
        return a;
    }
}
