package de.tum.in.lrr.hmm.util;

import java.io.IOException;
import java.io.Reader;

/**
 * @author <a href="bauerb@in.tum.de">Bernhard Bauer</a>
 *
 */
public class LinkedDataBlockReader extends CloneableReader {

    static final int DEFAULT_BLOCK_SIZE = 8192;

    LinkedDataBlock block;
    int pos;

    public LinkedDataBlockReader(Reader r) {
        this(r, DEFAULT_BLOCK_SIZE);
    }

    public LinkedDataBlockReader(Reader r, int size) {
        this(new LinkedDataBlock(r, size), 0);
    }

    protected LinkedDataBlockReader(LinkedDataBlock block, int pos) {
        this.block = block;
        this.pos = pos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        final int end = off+len;
        if ((off < 0) || (off > cbuf.length) || (len < 0) || (end > cbuf.length) || (end < 0)) {
            throw new IndexOutOfBoundsException();
        }
        if (block == null) {
            return -1;
        }
        block.fill();
        final char[] source = block.getData();
        final int size = block.size();
        final int charsRead = Math.min(len, size - pos);
        if (charsRead == 0) {
            return -1;
        }
        System.arraycopy(source, pos, cbuf, off, charsRead);
        pos += charsRead;
        if (pos >= size) {
            block = block.next();
            pos = 0;
        }
        return charsRead;
    }

    @Override
    public LinkedDataBlockReader clone() {
        return new LinkedDataBlockReader(block, pos);
    }

    static class LinkedDataBlock {

        final char[] data;

        int size = 0;

        LinkedDataBlock next = null;

        Reader r;

        public LinkedDataBlock(Reader r, int size) {
            this.data = new char[size];
            this.r = r;
        }

        void fill() throws IOException {
            while (size < data.length) {
                final int charsRead = r.read(data, size, data.length - size);
                if (charsRead == -1) {
                    r = null;
                    return;
                }
                size += charsRead;
            }
        }

        public LinkedDataBlock next() {
            if (next == null && r != null) {
                next = new LinkedDataBlock(r, data.length);
                r = null;
            }
            return next;
        }

        protected char[] getData() {
            return data;
        }

        protected int size() {
            return size;
        }
    }
}
