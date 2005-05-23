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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;


/**
 * HTML Parser
 */
public class HTMLParser implements HTMLParserConstants {
    /**
     * <code>SUMMARY_LENGTH</code> Contains the length of the summary.
     */
    public static final int SUMMARY_LENGTH = 200;
    /**
     * <code>token_source</code>
     */
    public HTMLParserTokenManager token_source;
    /**
     * <code>token</code>
     */
    public Token token;
    /**
     * <code>jj_nt</code>
     */
    public Token jj_nt;
    /**
     * <code>lookingAhead</code>
     */
    public boolean lookingAhead = false;

    StringBuffer title = new StringBuffer(SUMMARY_LENGTH);
    StringBuffer summary = new StringBuffer(SUMMARY_LENGTH * 2);
    int length = 0;
    boolean titleComplete = false;
    boolean inTitle = false;
    boolean inScript = false;
    boolean afterTag = false;
    boolean afterSpace = false;
    String eol = System.getProperty("line.separator");
    PipedReader pipeIn = null;
    PipedWriter pipeOut;
    int MAX_WAIT = 1000;
    SimpleCharStream jj_input_stream;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    final private int[] jj_la1 = new int[13];
    final private int[] jj_la1_0 = {
        0xb3e, 0xb3e, 0x1000, 0x38000, 0x2000, 0x8000, 0x10000, 0x20000, 0x3b000, 0x3b000, 0x800000,
        0x2000000, 0x18,
    };
    final private JJCalls[] jj_2_rtns = new JJCalls[2];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private java.util.Vector jj_expentries = new java.util.Vector();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    /**
     * Creates a new HTMLParser object from a file
     * @param file The file
     * @throws FileNotFoundException if the file was not found
     */
    public HTMLParser(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    /**
     * Creates a new HTMLParser object from a stream
     * @param stream The stream
     */
    public HTMLParser(java.io.InputStream stream) {
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new HTMLParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;

        for (int i = 0; i < 13; i++)
            this.jj_la1[i] = -1;

        for (int i = 0; i < this.jj_2_rtns.length; i++)
            this.jj_2_rtns[i] = new JJCalls();
    }

    /**
     * Creates a new HTMLParser object from a reader
     * @param stream The reader
     */
    public HTMLParser(java.io.Reader stream) {
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new HTMLParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;

        for (int i = 0; i < 13; i++)
            this.jj_la1[i] = -1;

        for (int i = 0; i < this.jj_2_rtns.length; i++)
            this.jj_2_rtns[i] = new JJCalls();
    }

    /**
     * Creates a new HTMLParser object from a token manager
     * @param tm The token manager
     */
    public HTMLParser(HTMLParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;

        for (int i = 0; i < 13; i++)
            this.jj_la1[i] = -1;

        for (int i = 0; i < this.jj_2_rtns.length; i++)
            this.jj_2_rtns[i] = new JJCalls();
    }

    /**
     * Get the title
     * @return The title
     * @throws IOException if an IO error occurs
     * @throws InterruptedException if an error occurs
     */
    public String getTitle() throws IOException, InterruptedException {
        if (this.pipeIn == null) {
            getReader(); // spawn parsing thread
        }

        int elapsedMillis = 0;

        while (true) {
            synchronized (this) {
                if (this.titleComplete || (this.length > SUMMARY_LENGTH)) {
                    break;
                }

                wait(10);

                elapsedMillis = elapsedMillis + 10;

                if (elapsedMillis > this.MAX_WAIT) {
                    break;
                }
            }
        }

        return this.title.toString().trim();
    }

    /**
     * Get keywords
     * @return keywords
     * @throws IOException if an IO error occurs
     * @throws InterruptedException if an error occurs
     */
    public String getKeywords() throws IOException, InterruptedException {
        return "";
    }

    /**
     * Get the summary
     * @return The summary
     * @throws IOException if an IO error occurs
     * @throws InterruptedException if an error occurs
     */
    public String getSummary() throws IOException, InterruptedException {
        System.out.println("HTMLParser().getSummary()");

        if (this.pipeIn == null) {
            getReader(); // spawn parsing thread
        }

        int elapsedMillis = 0;

        while (true) {
            synchronized (this) {
                if (this.summary.length() >= SUMMARY_LENGTH) {
                    break;
                }

                wait(10);

                elapsedMillis = elapsedMillis + 10;

                if (elapsedMillis > this.MAX_WAIT) {
                    break;
                }
            }
        }

        if (this.summary.length() > SUMMARY_LENGTH) {
            this.summary.setLength(SUMMARY_LENGTH);
        }

        String sum = this.summary.toString().trim();
        String tit = getTitle();

        if (sum.startsWith(tit)) {
            return sum;
        }
        return sum;
    }

    /**
     * Get a reader
     * @return The reader
     * @throws IOException if an IO error occurs
     */
    public Reader getReader() throws IOException {
        if (this.pipeIn == null) {
            this.pipeIn = new PipedReader();
            this.pipeOut = new PipedWriter(this.pipeIn);

            Thread thread = new ParserThread(this);
            thread.start(); // start parsing
        }

        return this.pipeIn;
    }

    void addToSummary(String text) {
        if (this.summary.length() < SUMMARY_LENGTH) {
            this.summary.append(text);

            if (this.summary.length() >= SUMMARY_LENGTH) {
                synchronized (this) {
                    notifyAll();
                }
            }
        }
    }

    void addToTitle(String text) {
        this.title.append(text);
    }

    void addText(String text) throws IOException {
        if (this.inScript) {
            return;
        }

        if (this.inTitle) {
            addToTitle(text);
        } else {
            addToSummary(text);

            if (!this.titleComplete && !this.title.equals("")) { // finished title

                synchronized (this) {
                    this.titleComplete = true; // tell waiting threads
                    notifyAll();
                }
            }
        }

        this.length += text.length();
        this.pipeOut.write(text);

        this.afterSpace = false;
    }

    void addSpace() throws IOException {
        if (this.inScript) {
            return;
        }

        if (!this.afterSpace) {
            if (this.inTitle) {
                addToTitle(" ");
            } else {
                addToSummary(" ");
            }

            String space = this.afterTag ? this.eol : " ";
            this.length += space.length();
            this.pipeOut.write(space);
            this.afterSpace = true;
        }
    }

    /**
     * Implements HTMLDocument
     * @throws ParseException if a parser error occurs
     * @throws IOException if an IO error occurs
     */
    final public void HTMLDocument() throws ParseException, IOException {
        Token t;
label_1: 
        while (true) {
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
            case TagName:
            case DeclName:
            case Comment1:
            case Comment2:
            case Word:
            case Entity:
            case Space:
            case Punct:

                break;

            default:
                this.jj_la1[0] = this.jj_gen;

                break label_1;
            }

            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
            case TagName:
                tag();
                this.afterTag = true;
                break;

            case DeclName:
                t = decl();
                this.afterTag = true;
                break;

            case Comment1:
            case Comment2:
                commentTag();
                this.afterTag = true;
                break;

            case Word:
                t = jj_consume_token(Word);
                addText(t.image);
                this.afterTag = false;
                break;

            case Entity:
                t = jj_consume_token(Entity);
                addText(Entities.decode(t.image));
                this.afterTag = false;
                break;

            case Punct:
                t = jj_consume_token(Punct);
                addText(t.image);
                this.afterTag = false;
                break;

            case Space:
                jj_consume_token(Space);
                addSpace();
                this.afterTag = false;
                break;

            default:
                this.jj_la1[1] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            }
        }

        jj_consume_token(0);
    }

    /**
     * tag() callback
     * @throws ParseException if a parser error occurs
     * @throws IOException if an IO error occurs
     */
    final public void tag() throws ParseException, IOException {
        Token t1;
        Token t2;
        boolean inImg = false;
        t1 = jj_consume_token(TagName);
        this.inTitle = t1.image.equalsIgnoreCase("<title"); // keep track if in <TITLE>
        inImg = t1.image.equalsIgnoreCase("<img"); // keep track if in <IMG>

        if (this.inScript) { // keep track if in <SCRIPT>
            this.inScript = !t1.image.equalsIgnoreCase("</script");
        } else {
            this.inScript = t1.image.equalsIgnoreCase("<script");
        }

label_2: 
        while (true) {
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
            case ArgName:
                break;

            default:
                this.jj_la1[2] = this.jj_gen;

                break label_2;
            }

            t1 = jj_consume_token(ArgName);

            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
            case ArgEquals:
                jj_consume_token(ArgEquals);

                switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                case ArgValue:
                case ArgQuote1:
                case ArgQuote2:
                    t2 = argValue();

                    if (inImg && t1.image.equalsIgnoreCase("alt") && (t2 != null)) {
                        addText("[" + t2.image + "]");
                    }

                    break;

                default:
                    this.jj_la1[3] = this.jj_gen;
                }

                break;

            default:
                this.jj_la1[4] = this.jj_gen;
            }
        }

        jj_consume_token(TagEnd);
    }

    /**
     * ArgValue() callback
     * @return The token
     * @throws ParseException if a parser error occurs
     * @throws Error if an error occurs
     */
    final public Token argValue() throws ParseException {
        Token t = null;

        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
        case ArgValue:
            t = jj_consume_token(ArgValue);
             {
                if (true) {
                    return t;
                }
            }

            break;

        default:
            this.jj_la1[5] = this.jj_gen;

            if (jj_2_1(2)) {
                jj_consume_token(ArgQuote1);
                jj_consume_token(CloseQuote1);

                if (true) {
                    return t;
                }
            } else {
                switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                case ArgQuote1:
                    jj_consume_token(ArgQuote1);
                    t = jj_consume_token(Quote1Text);
                    jj_consume_token(CloseQuote1);
                     {
                        if (true) {
                            return t;
                        }
                    }

                    break;

                default:
                    this.jj_la1[6] = this.jj_gen;

                    if (jj_2_2(2)) {
                        jj_consume_token(ArgQuote2);
                        jj_consume_token(CloseQuote2);

                        if (true) {
                            return t;
                        }
                    } else {
                        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                        case ArgQuote2:
                            jj_consume_token(ArgQuote2);
                            t = jj_consume_token(Quote2Text);
                            jj_consume_token(CloseQuote2);
                             {
                                if (true) {
                                    return t;
                                }
                            }

                            break;

                        default:
                            this.jj_la1[7] = this.jj_gen;
                            jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                }
            }
        }

        throw new Error("Missing return statement in function");
    }

    /**
     * Decl() callback
     * @return The token
     * @throws ParseException if a parser error occurs
     * @throws Error if an error occurs
     */
    final public Token decl() throws ParseException {
        Token t;
        t = jj_consume_token(DeclName);
label_3: 
        while (true) {
            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
            case ArgName:
            case ArgEquals:
            case ArgValue:
            case ArgQuote1:
            case ArgQuote2:

                break;

            default:
                this.jj_la1[8] = this.jj_gen;

                break label_3;
            }

            switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
            case ArgName:
                jj_consume_token(ArgName);

                break;

            case ArgValue:
            case ArgQuote1:
            case ArgQuote2:
                argValue();

                break;

            case ArgEquals:
                jj_consume_token(ArgEquals);

                break;

            default:
                this.jj_la1[9] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            }
        }

        jj_consume_token(TagEnd);

        if (true) {
            return t;
        }

        throw new Error("Missing return statement in function");
    }

    /**
     * CommentTag() callback
     * @throws ParseException if a parser error occurs
     */
    final public void commentTag() throws ParseException {
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
        case Comment1:
            jj_consume_token(Comment1);
label_4: 
            while (true) {
                switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                case CommentText1:

                    break;

                default:
                    this.jj_la1[10] = this.jj_gen;

                    break label_4;
                }

                jj_consume_token(CommentText1);
            }

            jj_consume_token(CommentEnd1);

            break;

        case Comment2:
            jj_consume_token(Comment2);
label_5: 
            while (true) {
                switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
                case CommentText2:

                    break;

                default:
                    this.jj_la1[11] = this.jj_gen;

                    break label_5;
                }

                jj_consume_token(CommentText2);
            }

            jj_consume_token(CommentEnd2);

            break;

        default:
            this.jj_la1[12] = this.jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
        }
    }

    final private boolean jj_2_1(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;

        boolean retval = !jj_3_1();
        jj_save(0, xla);

        return retval;
    }

    final private boolean jj_2_2(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;

        boolean retval = !jj_3_2();
        jj_save(1, xla);

        return retval;
    }

    final private boolean jj_3_1() {
        if (jj_scan_token(ArgQuote1)) {
            return true;
        }

        if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos)) {
            return false;
        }

        if (jj_scan_token(CloseQuote1)) {
            return true;
        }

        if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos)) {
            return false;
        }

        return false;
    }

    final private boolean jj_3_2() {
        if (jj_scan_token(ArgQuote2)) {
            return true;
        }

        if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos)) {
            return false;
        }

        if (jj_scan_token(CloseQuote2)) {
            return true;
        }

        if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos)) {
            return false;
        }

        return false;
    }

    /**
     * Reinitialize the Parser
     * @param stream The stream
     */
    public void reInit(java.io.InputStream stream) {
        this.jj_input_stream.reInit(stream, 1, 1);
        this.token_source.reInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;

        for (int i = 0; i < 13; i++)
            this.jj_la1[i] = -1;

        for (int i = 0; i < this.jj_2_rtns.length; i++)
            this.jj_2_rtns[i] = new JJCalls();
    }

    /**
     * Reinitialize the Parser
     * @param stream The reader
     */
    public void reInit(java.io.Reader stream) {
        this.jj_input_stream.reInit(stream, 1, 1);
        this.token_source.reInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;

        for (int i = 0; i < 13; i++)
            this.jj_la1[i] = -1;

        for (int i = 0; i < this.jj_2_rtns.length; i++)
            this.jj_2_rtns[i] = new JJCalls();
    }

    /**
     * Reinitialize the Parser
     * @param tm The token manager
     */
    public void reInit(HTMLParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;

        for (int i = 0; i < 13; i++)
            this.jj_la1[i] = -1;

        for (int i = 0; i < this.jj_2_rtns.length; i++)
            this.jj_2_rtns[i] = new JJCalls();
    }

    final private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken;

        if ((oldToken = this.token).next != null) {
            this.token = this.token.next;
        } else {
            this.token = this.token.next = this.token_source.getNextToken();
        }

        this.jj_ntk = -1;

        if (this.token.kind == kind) {
            this.jj_gen++;

            if (++this.jj_gc > 100) {
                this.jj_gc = 0;

                for (int i = 0; i < this.jj_2_rtns.length; i++) {
                    JJCalls c = this.jj_2_rtns[i];

                    while (c != null) {
                        if (c.gen < this.jj_gen) {
                            c.first = null;
                        }

                        c = c.next;
                    }
                }
            }

            return this.token;
        }

        this.token = oldToken;
        this.jj_kind = kind;
        throw generateParseException();
    }

    final private boolean jj_scan_token(int kind) {
        if (this.jj_scanpos == this.jj_lastpos) {
            this.jj_la--;

            if (this.jj_scanpos.next == null) {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
            } else {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
            }
        } else {
            this.jj_scanpos = this.jj_scanpos.next;
        }

        if (this.jj_rescan) {
            int i = 0;
            Token tok = this.token;

            while ((tok != null) && (tok != this.jj_scanpos)) {
                i++;
                tok = tok.next;
            }

            if (tok != null) {
                jj_add_error_token(kind, i);
            }
        }

        return (this.jj_scanpos.kind != kind);
    }

    /**
     * Get the next token
     * @return The token
     */
    final public Token getNextToken() {
        if (this.token.next != null) {
            this.token = this.token.next;
        } else {
            this.token = this.token.next = this.token_source.getNextToken();
        }

        this.jj_ntk = -1;
        this.jj_gen++;

        return this.token;
    }

    /**
     * Get token at position
     * @param index The position
     * @return The token
     */
    final public Token getToken(int index) {
        Token t = this.lookingAhead ? this.jj_scanpos : this.token;

        for (int i = 0; i < index; i++) {
            if (t.next != null) {
                t = t.next;
            } else {
                t = t.next = this.token_source.getNextToken();
            }
        }

        return t;
    }

    final private int jj_ntk() {
        if ((this.jj_nt = this.token.next) == null) {
            return (this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind);
        }
        return (this.jj_ntk = this.jj_nt.kind);
    }

    private void jj_add_error_token(int kind, int pos) {
        if (pos >= 100) {
            return;
        }

        if (pos == (this.jj_endpos + 1)) {
            this.jj_lasttokens[this.jj_endpos++] = kind;
        } else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];

            for (int i = 0; i < this.jj_endpos; i++) {
                this.jj_expentry[i] = this.jj_lasttokens[i];
            }

            boolean exists = false;

            for (java.util.Enumeration tEnum = this.jj_expentries.elements(); tEnum.hasMoreElements();) {
                int[] oldentry = (int[]) (tEnum.nextElement());

                if (oldentry.length == this.jj_expentry.length) {
                    exists = true;

                    for (int i = 0; i < this.jj_expentry.length; i++) {
                        if (oldentry[i] != this.jj_expentry[i]) {
                            exists = false;

                            break;
                        }
                    }

                    if (exists) {
                        break;
                    }
                }
            }

            if (!exists) {
                this.jj_expentries.addElement(this.jj_expentry);
            }

            if (pos != 0) {
                this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
            }
        }
    }

    /**
     * Generate a parse exception
     * @return The exception
     */
    final public ParseException generateParseException() {
        this.jj_expentries.removeAllElements();

        boolean[] la1tokens = new boolean[27];

        for (int i = 0; i < 27; i++) {
            la1tokens[i] = false;
        }

        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }

        for (int i = 0; i < 13; i++) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; j++) {
                    if ((this.jj_la1_0[i] & (1 << j)) != 0) {
                        la1tokens[j] = true;
                    }
                }
            }
        }

        for (int i = 0; i < 27; i++) {
            if (la1tokens[i]) {
                this.jj_expentry = new int[1];
                this.jj_expentry[0] = i;
                this.jj_expentries.addElement(this.jj_expentry);
            }
        }

        this.jj_endpos = 0;
        jj_rescan_token();
        jj_add_error_token(0, 0);

        int[][] exptokseq = new int[this.jj_expentries.size()][];

        for (int i = 0; i < this.jj_expentries.size(); i++) {
            exptokseq[i] = (int[]) this.jj_expentries.elementAt(i);
        }

        return new ParseException(this.token, exptokseq, tokenImage);
    }

    /**
     * Enable tracing
     */
    final public void enable_tracing() {
        // do nothing
    }

    /**
     * Disable tracing
     */
    final public void disable_tracing() {
        // do nothing
    }

    final private void jj_rescan_token() {
        this.jj_rescan = true;

        for (int i = 0; i < 2; i++) {
            JJCalls p = this.jj_2_rtns[i];

            do {
                if (p.gen > this.jj_gen) {
                    this.jj_la = p.arg;
                    this.jj_lastpos = this.jj_scanpos = p.first;

                    switch (i) {
                    case 0:
                        jj_3_1();

                        break;

                    case 1:
                        jj_3_2();

                        break;
                    }
                }

                p = p.next;
            } while (p != null);
        }

        this.jj_rescan = false;
    }

    final private void jj_save(int index, int xla) {
        JJCalls p = this.jj_2_rtns[index];

        while (p.gen > this.jj_gen) {
            if (p.next == null) {
                p = p.next = new JJCalls();

                break;
            }

            p = p.next;
        }

        p.gen = (this.jj_gen + xla) - this.jj_la;
        p.first = this.token;
        p.arg = xla;
    }

    static final class JJCalls {
        int gen;
        Token first;
        int arg;
        JJCalls next;
    }
}
