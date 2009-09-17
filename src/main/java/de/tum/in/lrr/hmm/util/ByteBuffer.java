package de.tum.in.lrr.hmm.util;



public class ByteBuffer {

    private static final int DEFAULT_SIZE = 16;

    protected byte[] buffer;
    protected int size = 0;

    public ByteBuffer() {
        this(DEFAULT_SIZE);
    }

    public ByteBuffer(int size) {
        buffer = new byte[size];
    }

    public void append(byte i) {
        if (size >= buffer.length) {
            final int newSize;
            if (buffer.length >= Integer.MAX_VALUE >> 1) {
                newSize = Integer.MAX_VALUE;
            } else {
                newSize = buffer.length << 1;
            }
            final byte[] newArray = new byte[newSize];
            System.arraycopy(buffer, 0, newArray, 0, buffer.length);
            buffer = newArray;
        }
        buffer[size] = i;
        size++;
    }

    public byte[] toByteArray() {
        if (buffer.length != size) {
            final byte[] newArray = new byte[size];
            System.arraycopy(buffer, 0, newArray, 0, size);
            buffer = newArray;
        }

        return buffer;
    }

}