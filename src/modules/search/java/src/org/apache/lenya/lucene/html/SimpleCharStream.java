/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/* $Id$  */

package org.apache.lenya.lucene.html;

import java.io.Reader;
import java.io.IOException;

/**
 * An implementation of interface CharStream, where the stream is assumed to contain only ASCII
 * characters (without unicode processing).
 */
public final class SimpleCharStream {
    /**
     * <code>staticFlag</code> Static flag
     */
    public static final boolean staticFlag = false;
    int bufsize;
    int available;
    int tokenBegin;
    /**
     * <code>bufpos</code> The buffer position
     */
    public int bufpos = -1;
    private int[] bufline;
    private int[] bufcolumn;
    private int column = 0;
    private int line = 1;
    private boolean prevCharIsCR = false;
    private boolean prevCharIsLF = false;
    private java.io.Reader inputStream;
    private char[] buffer;
    private int maxNextCharInd = 0;
    private int inBuf = 0;

    /**
     * Creates a new SimpleCharStream object.
     * @param dstream The reader
     * @param startline The starting line
     * @param startcolumn The starting column
     * @param buffersize The buffer size
     */
    public SimpleCharStream(Reader dstream, int startline, int startcolumn, int buffersize) {
        this.inputStream = dstream;
        this.line = startline;
        this.column = startcolumn - 1;

        this.available = this.bufsize = buffersize;
        this.buffer = new char[buffersize];
        this.bufline = new int[buffersize];
        this.bufcolumn = new int[buffersize];
    }

    /**
     * Creates a new SimpleCharStream object.
     * @param dstream The reader
     * @param startline The starting line
     * @param startcolumn The starting column
     */
    public SimpleCharStream(java.io.Reader dstream, int startline, int startcolumn) {
        this(dstream, startline, startcolumn, 4096);
    }

    /**
     * Creates a new SimpleCharStream object.
     * @param dstream The reader
     */
    public SimpleCharStream(java.io.Reader dstream) {
        this(dstream, 1, 1, 4096);
    }

    /**
     * Creates a new SimpleCharStream object.
     * @param dstream The stream
     * @param startline The starting line
     * @param startcolumn The starting column
     * @param buffersize The buffer size
     */
    public SimpleCharStream(java.io.InputStream dstream, int startline, int startcolumn,
        int buffersize) {
        this(new java.io.InputStreamReader(dstream), startline, startcolumn, 4096);
    }

    /**
     * Creates a new SimpleCharStream object.
     * @param dstream The stream
     * @param startline The starting line
     * @param startcolumn The starting column
     */
    public SimpleCharStream(java.io.InputStream dstream, int startline, int startcolumn) {
        this(dstream, startline, startcolumn, 4096);
    }

    /**
     * Creates a new SimpleCharStream object.
     * @param dstream The stream
     */
    public SimpleCharStream(java.io.InputStream dstream) {
        this(dstream, 1, 1, 4096);
    }

    private final void rxpandBuff(boolean wrapAround) {
        char[] newbuffer = new char[this.bufsize + 2048];
        int[] newbufline = new int[this.bufsize + 2048];
        int[] newbufcolumn = new int[this.bufsize + 2048];

        try {
            if (wrapAround) {
                System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.buffer, 0, newbuffer, this.bufsize - this.tokenBegin, this.bufpos);
                this.buffer = newbuffer;

                System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.bufline, 0, newbufline, this.bufsize - this.tokenBegin, this.bufpos);
                this.bufline = newbufline;

                System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.bufcolumn, 0, newbufcolumn, this.bufsize - this.tokenBegin, this.bufpos);
                this.bufcolumn = newbufcolumn;

                this.maxNextCharInd = (this.bufpos += (this.bufsize - this.tokenBegin));
            } else {
                System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
                this.buffer = newbuffer;

                System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
                this.bufline = newbufline;

                System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
                this.bufcolumn = newbufcolumn;

                this.maxNextCharInd = (this.bufpos -= this.tokenBegin);
            }
        } catch (Throwable t) {
            throw new Error(t.getMessage());
        }

        this.bufsize += 2048;
        this.available = this.bufsize;
        this.tokenBegin = 0;
    }

    private final void fillBuff() throws java.io.IOException {
        if (this.maxNextCharInd == this.available) {
            if (this.available == this.bufsize) {
                if (this.tokenBegin > 2048) {
                    this.bufpos = this.maxNextCharInd = 0;
                    this.available = this.tokenBegin;
                } else if (this.tokenBegin < 0) {
                    this.bufpos = this.maxNextCharInd = 0;
                } else {
                    rxpandBuff(false);
                }
            } else if (this.available > this.tokenBegin) {
                this.available = this.bufsize;
            } else if ((this.tokenBegin - this.available) < 2048) {
                rxpandBuff(true);
            } else {
                this.available = this.tokenBegin;
            }
        }

        int i;

        try {
            if ((i = this.inputStream.read(this.buffer, this.maxNextCharInd, this.available - this.maxNextCharInd)) == -1) {
                this.inputStream.close();
                throw new java.io.IOException();
            }
            this.maxNextCharInd += i;

            return;
        } catch (java.io.IOException e) {
            --this.bufpos;
            backup(0);

            if (this.tokenBegin == -1) {
                this.tokenBegin = this.bufpos;
            }

            throw e;
        }
    }

    /**
     * Begin Token callback
     * @return A character
     * @throws IOException if an IO error occurs
     */
    public final char beginToken() throws IOException {
        this.tokenBegin = -1;

        char c = readChar();
        this.tokenBegin = this.bufpos;

        return c;
    }

    private final void updateLineColumn(char c) {
        this.column++;

        if (this.prevCharIsLF) {
            this.prevCharIsLF = false;
            this.line += (this.column = 1);
        } else if (this.prevCharIsCR) {
            this.prevCharIsCR = false;

            if (c == '\n') {
                this.prevCharIsLF = true;
            } else {
                this.line += (this.column = 1);
            }
        }

        switch (c) {
        case '\r':
            this.prevCharIsCR = true;

            break;

        case '\n':
            this.prevCharIsLF = true;

            break;

        case '\t':
            this.column--;
            this.column += (8 - (this.column & 07));

            break;

        default:
            break;
        }

        this.bufline[this.bufpos] = this.line;
        this.bufcolumn[this.bufpos] = this.column;
    }

    /**
     * Read the next character
     * @return The character
     * @throws IOException if an IO error occurs
     */
    public final char readChar() throws IOException {
        if (this.inBuf > 0) {
            --this.inBuf;

            if (++this.bufpos == this.bufsize) {
                this.bufpos = 0;
            }

            return this.buffer[this.bufpos];
        }

        if (++this.bufpos >= this.maxNextCharInd) {
            fillBuff();
        }

        char c = this.buffer[this.bufpos];

        updateLineColumn(c);

        return (c);
    }

    /**
     * Get the column position
     * @return The position
     * @see #getEndColumn
     * @deprecated
     */
    public final int getColumn() {
        return this.bufcolumn[this.bufpos];
    }

    /**
     * Get the line number
     * @return The line number
     * @see #getEndLine
     * @deprecated
     */
    public final int getLine() {
        return this.bufline[this.bufpos];
    }

    /**
     * Get the column position
     * @return The position
     */
    public final int getEndColumn() {
        return this.bufcolumn[this.bufpos];
    }

    /**
     * Get the line number
     * @return The line number
     */
    public final int getEndLine() {
        return this.bufline[this.bufpos];
    }

    /**
     * Get the column begin
     * @return The begin of the column
     */
    public final int getBeginColumn() {
        return this.bufcolumn[this.tokenBegin];
    }

    /**
     * Get the line begin
     * @return The begin of the line
     */
    public final int getBeginLine() {
        return this.bufline[this.tokenBegin];
    }

    /**
     * Go backwards in the buffer
     * @param amount The amount to go backwards
     */
    public final void backup(int amount) {
        this.inBuf += amount;

        if ((this.bufpos -= amount) < 0) {
            this.bufpos += this.bufsize;
        }
    }

    /**
     * Reinitialize the Parser
     * @param dstream The reader
     * @param startline The starting line
     * @param startcolumn The starting column
     * @param buffersize The buffer size
     */
    public void reInit(java.io.Reader dstream, int startline, int startcolumn, int buffersize) {
        this.inputStream = dstream;
        this.line = startline;
        this.column = startcolumn - 1;

        if ((this.buffer == null) || (buffersize != this.buffer.length)) {
            this.available = this.bufsize = buffersize;
            this.buffer = new char[buffersize];
            this.bufline = new int[buffersize];
            this.bufcolumn = new int[buffersize];
        }

        this.prevCharIsLF = this.prevCharIsCR = false;
        this.tokenBegin = this.inBuf = this.maxNextCharInd = 0;
        this.bufpos = -1;
    }

    /**
     * Reinitialize the parser
     * @param dstream The reader
     * @param startline The starting line
     * @param startcolumn The starting column
     */
    public void reInit(java.io.Reader dstream, int startline, int startcolumn) {
        reInit(dstream, startline, startcolumn, 4096);
    }

    /**
     * Reinitialize the parser
     * @param reader The reader
     */
    public void reInit(java.io.Reader reader) {
        reInit(reader, 1, 1, 4096);
    }

    /**
     * Reinitialize the parser
     * @param dstream The stream
     * @param startline The starting line
     * @param startcolumn The starting column
     * @param buffersize The buffer size
     */
    public void reInit(java.io.InputStream dstream, int startline, int startcolumn, int buffersize) {
        reInit(new java.io.InputStreamReader(dstream), startline, startcolumn, 4096);
    }

    /**
     * Reinitialize the parser
     * @param dstream The stream
     */
    public void reInit(java.io.InputStream dstream) {
        reInit(dstream, 1, 1, 4096);
    }

    /**
     * Reinitialize the parser
     * @param dstream The stream
     * @param startline The starting line
     * @param startcolumn The starting column
     */
    public void reInit(java.io.InputStream dstream, int startline, int startcolumn) {
        reInit(dstream, startline, startcolumn, 4096);
    }

    /**
     * Get the image
     * @return The image
     */
    public final String getImage() {
        if (this.bufpos >= this.tokenBegin) {
            return new String(this.buffer, this.tokenBegin, this.bufpos - this.tokenBegin + 1);
        }
        return new String(this.buffer, this.tokenBegin, this.bufsize - this.tokenBegin) +
        new String(this.buffer, 0, this.bufpos + 1);
    }

    /**
     * Get a suffix
     * @param len The length of the suffix
     * @return The suffix
     */
    public final char[] getSuffix(int len) {
        char[] ret = new char[len];

        if ((this.bufpos + 1) >= len) {
            System.arraycopy(this.buffer, this.bufpos - len + 1, ret, 0, len);
        } else {
            System.arraycopy(this.buffer, this.bufsize - (len - this.bufpos - 1), ret, 0, len - this.bufpos - 1);
            System.arraycopy(this.buffer, 0, ret, len - this.bufpos - 1, this.bufpos + 1);
        }

        return ret;
    }

    /**
     * Empty all buffers
     */
    public void done() {
        this.buffer = null;
        this.bufline = null;
        this.bufcolumn = null;
    }

    /**
     * Method to adjust line and column numbers for the start of a token.<BR>
     * @param newLine The new line
     * @param newCol The new column
     */
    public void adjustBeginLineColumn(int newLine, int newCol) {
        int start = this.tokenBegin;
        int len;

        if (this.bufpos >= this.tokenBegin) {
            len = this.bufpos - this.tokenBegin + this.inBuf + 1;
        } else {
            len = this.bufsize - this.tokenBegin + this.bufpos + 1 + this.inBuf;
        }

        int i = 0;
        int j = 0;
        int k = 0;
        int nextColDiff = 0;
        int columnDiff = 0;

        while ((i < len) && (this.bufline[j = start % this.bufsize] == this.bufline[k = ++start % this.bufsize])) {
            this.bufline[j] = newLine;
            nextColDiff = (columnDiff + this.bufcolumn[k]) - this.bufcolumn[j];
            this.bufcolumn[j] = newCol + columnDiff;
            columnDiff = nextColDiff;
            i++;
        }

        if (i < len) {
            this.bufline[j] = newLine++;
            this.bufcolumn[j] = newCol + columnDiff;

            while (i++ < len) {
                if (this.bufline[j = start % this.bufsize] != this.bufline[++start % this.bufsize]) {
                    this.bufline[j] = newLine++;
                } else {
                    this.bufline[j] = newLine;
                }
            }
        }

        this.line = this.bufline[j];
        this.column = this.bufcolumn[j];
    }
}
