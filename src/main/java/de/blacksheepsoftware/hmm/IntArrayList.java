package de.blacksheepsoftware.hmm;

import java.util.AbstractList;
import java.util.RandomAccess;

public class IntArrayList extends AbstractList<Integer>
implements RandomAccess, java.io.Serializable {
    private static final long serialVersionUID = 2357281897136237691L;
    private final int[] a;
    public static final int DEFAULT_LIST_LENGTH = 16;

    public IntArrayList(Iterable<Integer> list) {
        this(forList(list));
    }

    public IntArrayList(Iterable<Integer> list, int length) {
        this(forList(list, length));
    }

    public IntArrayList(int[] array) {
        if (array==null) {
            throw new NullPointerException();
        }
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