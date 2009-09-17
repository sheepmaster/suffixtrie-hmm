package de.tum.in.lrr.hmm.util;

import java.util.Iterator;


public class ByteArray {
    public static final int DEFAULT_LIST_LENGTH = 16;

    public static byte[] forList(Iterator<Byte> it) {
        return forList(it, DEFAULT_LIST_LENGTH);
    }

    public static byte[] forList(Iterator<Byte> it, int length) {
        byte[] array = new byte[length];
        int numItems = 0;
        while (it.hasNext()) {
            final byte i = it.next();
            if (numItems >= array.length) {
                final int newSize;
                if (array.length >= Integer.MAX_VALUE >> 1) {
                    newSize = Integer.MAX_VALUE;
                } else {
                    newSize = array.length << 1;
                }
                final byte[] newArray = new byte[newSize];
                System.arraycopy(array, 0, newArray, 0, array.length);
                array = newArray;
            }
            array[numItems] = i;
            numItems++;
        }
        if (array.length != numItems) {
            final byte[] newArray = new byte[numItems];
            System.arraycopy(array, 0, newArray, 0, numItems);
            array = newArray;
        }

        return array;
    }
}