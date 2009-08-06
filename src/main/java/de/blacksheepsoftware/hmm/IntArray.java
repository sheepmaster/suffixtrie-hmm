package de.blacksheepsoftware.hmm;

public class IntArray {
    public static final int DEFAULT_LIST_LENGTH = 16;

    public static int[] forList(Iterable<Integer> list) {
        return forList(list, DEFAULT_LIST_LENGTH);
    }

    public static int[] forList(Iterable<Integer> list, int length) {
        int[] array = new int[length];
        int numItems = 0;
        for (int i : list) {
            if (numItems >= array.length) {
                final int newSize;
                if (array.length >= Integer.MAX_VALUE >> 1) {
                    newSize = Integer.MAX_VALUE;
                } else {
                    newSize = array.length << 1;
                }
                final int[] newArray = new int[newSize];
                System.arraycopy(array, 0, newArray, 0, array.length);
                array = newArray;
            }
            array[numItems] = i;
            numItems++;
        }
        if (array.length != numItems) {
            final int[] newArray = new int[numItems];
            System.arraycopy(array, 0, newArray, 0, numItems);
            array = newArray;
        }

        return array;
    }
}