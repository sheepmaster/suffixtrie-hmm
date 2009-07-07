package de.blacksheepsoftware.gene;


/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class IntArray {

    public static int[] forList(Iterable<Integer> list) {
        int[] array = new int[16];
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
        final int[] newArray = new int[numItems];
        System.arraycopy(array, 0, newArray, 0, numItems);

        return newArray;
    }

}
