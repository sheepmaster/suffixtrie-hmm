package de.blacksheepsoftware.t9;

import java.util.HashMap;
import java.util.Map;

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
		numbers.put('a', 2);
		numbers.put('b', 2);
		numbers.put('c', 2);
		numbers.put('d', 3);
		numbers.put('e', 3);
		numbers.put('f', 3);
		numbers.put('g', 4);
		numbers.put('h', 4);
		numbers.put('i', 4);
		numbers.put('j', 5);
		numbers.put('k', 5);
		numbers.put('l', 5);
		numbers.put('m', 6);
		numbers.put('n', 6);
		numbers.put('o', 6);
		numbers.put('p', 7);
		numbers.put('q', 7);
		numbers.put('r', 7);
		numbers.put('s', 7);
		numbers.put('t', 8);
		numbers.put('u', 8);
		numbers.put('v', 8);
		numbers.put('w', 9);
		numbers.put('x', 9);
		numbers.put('y', 9);
		numbers.put('z', 9);
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
