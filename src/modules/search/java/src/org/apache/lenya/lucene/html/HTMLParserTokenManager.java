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

/**
 * Auto-generated (by JavaCC) class for HTML Parser tokens
 */
public class HTMLParserTokenManager implements HTMLParserConstants {
    static final long[] jjbitVec0 = { 0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL };
    static final int[] jjnextStates = {
        17, 18, 21, 12, 14, 5, 8, 0, 4, 6, 0, 4, 6, 5, 0, 4, 6, 12, 13,
    };
    /**
     * <code>jjstrLiteralImages</code>
     */
    public static final String[] jjstrLiteralImages = {
        "", null, null, "\74\41\55\55", "\74\41", null, null, null, null, null, null, null, null,
        "\75", null, null, "\47", "\42", null, null, null, null, null, null, "\55\55\76", null,
        "\76",
    };
    /**
     * <code>lexStateNames</code> (represents parser states)
     */
    public static final String[] lexStateNames = {
        "DEFAULT", "WithinTag", "AfterEquals", "WithinQuote1", "WithinQuote2", "WithinComment1",
        "WithinComment2",
    };
    /**
     * <code>jjnewLexState</code> (further parser states)
     */
    public static final int[] jjnewLexState = {
        -1, 1, 1, 5, 6, -1, -1, -1, -1, -1, -1, -1, -1, 2, 0, 1, 3, 4, -1, -1, 1, -1, 1, -1, 0, -1,
        0,
    };
    static final long[] jjtoToken = { 0x7fbfb3fL, };
    static final long[] jjtoSkip = { 0x40000L, };
    /**
     * <code>debugStream</code> The debug stream for this parser
     */
    public java.io.PrintStream debugStream = System.out;
    private SimpleCharStream input_stream;
    private final int[] jjrounds = new int[25];
    private final int[] jjstateSet = new int[50];
    protected char curChar;
    int curLexState = 0;
    int defaultLexState = 0;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;

    /**
     * Creates a new HTMLParserTokenManager object using a Stream
     * @param stream The stream
     */
    public HTMLParserTokenManager(SimpleCharStream stream) {
        if (SimpleCharStream.staticFlag) {
            throw new Error(
                "ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
        }

        this.input_stream = stream;
    }

    /**
     * Creates a new HTMLParserTokenManager object.
     * @param stream The stream
     * @param lexState The parser state
     */
    public HTMLParserTokenManager(SimpleCharStream stream, int lexState) {
        this(stream);
        switchTo(lexState);
    }

    /**
     * Set the debug stream
     * @param ds The debug stream
     */
    public void setDebugStream(java.io.PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_0(int pos, long active0) {
        switch (pos) {
        case 0:

            if ((active0 & 0x18L) != 0L) {
                return 17;
            }

            return -1;

        case 1:

            if ((active0 & 0x18L) != 0L) {
                return 22;
            }

            return -1;

        default:
            return -1;
        }
    }

    private final int jjStartNfa_0(int pos, long active0) {
        return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
    }

    private final int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;

        return pos + 1;
    }

    private final int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
        case 60:
            return jjMoveStringLiteralDfa1_0(0x18L);

        default:
            return jjMoveNfa_0(11, 0);
        }
    }

    private final int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        } catch (java.io.IOException e) {
            jjStopStringLiteralDfa_0(0, active0);

            return 1;
        }

        switch (this.curChar) {
        case 33:

            if ((active0 & 0x10L) != 0L) {
                this.jjmatchedKind = 4;
                this.jjmatchedPos = 1;
            }

            return jjMoveStringLiteralDfa2_0(active0, 0x8L);

        default:
            break;
        }

        return jjStartNfa_0(0, active0);
    }

    private final int jjMoveStringLiteralDfa2_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return jjStartNfa_0(0, old0);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (java.io.IOException e) {
            jjStopStringLiteralDfa_0(1, active0);

            return 2;
        }

        switch (this.curChar) {
        case 45:
            return jjMoveStringLiteralDfa3_0(active0, 0x8L);

        default:
            break;
        }

        return jjStartNfa_0(1, active0);
    }

    private final int jjMoveStringLiteralDfa3_0(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return jjStartNfa_0(1, old0);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (java.io.IOException e) {
            jjStopStringLiteralDfa_0(2, active0);

            return 3;
        }

        switch (this.curChar) {
        case 45:

            if ((active0 & 0x8L) != 0L) {
                return jjStopAtPos(3, 3);
            }

            break;

        default:
            break;
        }

        return jjStartNfa_0(2, active0);
    }

    private final void jjCheckNAdd(int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }

    private final void jjAddStates(int start, int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
        } while (start++ != end);
    }

    private final void jjCheckNAddTwoStates(int state1, int state2) {
        jjCheckNAdd(state1);
        jjCheckNAdd(state2);
    }

    private final void jjCheckNAddStates(int start, int end) {
        do {
            jjCheckNAdd(jjnextStates[start]);
        } while (start++ != end);
    }

    private final int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 25;

        int i = 1;
        this.jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++this.jjround == 0x7fffffff) {
                reInitRounds();
            }

            if (this.curChar < 64) {
                long l = 1L << this.curChar;
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 11:

                        if ((0x3ff000000000000L & l) != 0L) {
                            jjCheckNAddTwoStates(7, 2);
                        } else if ((0x100002600L & l) != 0L) {
                            if (kind > 9) {
                                kind = 9;
                            }

                            jjCheckNAdd(10);
                        } else if (this.curChar == 60) {
                            jjCheckNAddStates(0, 2);
                        } else if (this.curChar == 38) {
                            jjAddStates(3, 4);
                        } else if (this.curChar == 36) {
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                        }

                        if ((0x3ff000000000000L & l) != 0L) {
                            if (kind > 5) {
                                kind = 5;
                            }

                            jjCheckNAddStates(5, 9);
                        }

                        break;

                    case 17:

                        if (this.curChar == 33) {
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                        } else if (this.curChar == 47) {
                            jjCheckNAdd(18);
                        }

                        break;

                    case 0:

                        if (this.curChar == 36) {
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                        }

                        break;

                    case 1:

                        if ((0x3ff000000000000L & l) != 0L) {
                            jjCheckNAdd(2);
                        }

                        break;

                    case 2:

                        if ((0x500000000000L & l) != 0L) {
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                        }

                        break;

                    case 3:
                    case 9:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 5) {
                            kind = 5;
                        }

                        jjCheckNAddStates(10, 12);

                        break;

                    case 4:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 5) {
                            kind = 5;
                        }

                        jjCheckNAddStates(5, 9);

                        break;

                    case 5:

                        if ((0x880000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 5) {
                            kind = 5;
                        }

                        jjCheckNAddStates(13, 16);

                        break;

                    case 6:

                        if ((0x3ff000000000000L & l) != 0L) {
                            jjCheckNAddTwoStates(7, 2);
                        }

                        break;

                    case 7:

                        if (this.curChar != 34) {
                            break;
                        }

                        if (kind > 5) {
                            kind = 5;
                        }

                        jjCheckNAddStates(10, 12);

                        break;

                    case 8:

                        if ((0x208000000000L & l) != 0L) {
                            this.jjstateSet[this.jjnewStateCnt++] = 9;
                        }

                        break;

                    case 10:

                        if ((0x100002600L & l) == 0L) {
                            break;
                        }

                        kind = 9;
                        jjCheckNAdd(10);

                        break;

                    case 13:

                        if ((this.curChar == 59) && (kind > 8)) {
                            kind = 8;
                        }

                        break;

                    case 14:

                        if (this.curChar == 35) {
                            jjCheckNAdd(15);
                        }

                        break;

                    case 15:

                        if ((0x3ff000000000000L & l) == 0L) {
                            break;
                        }

                        if (kind > 8) {
                            kind = 8;
                        }

                        jjCheckNAddTwoStates(15, 13);

                        break;

                    case 16:

                        if (this.curChar == 60) {
                            jjCheckNAddStates(0, 2);
                        }

                        break;

                    case 19:

                        if ((0x9fffff7affffd9ffL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAdd(20);

                        break;

                    case 20:

                        if ((0x9ffffffeffffd9ffL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAdd(20);

                        break;

                    case 21:

                        if (this.curChar == 33) {
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
                        }

                        break;

                    case 23:

                        if ((0x9fffff7affffd9ffL & l) == 0L) {
                            break;
                        }

                        if (kind > 2) {
                            kind = 2;
                        }

                        jjCheckNAdd(24);

                        break;

                    case 24:

                        if ((0x9ffffffeffffd9ffL & l) == 0L) {
                            break;
                        }

                        if (kind > 2) {
                            kind = 2;
                        }

                        jjCheckNAdd(24);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l = 1L << (this.curChar & 077);
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 11:
                    case 4:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 5) {
                            kind = 5;
                        }

                        jjCheckNAddStates(5, 9);

                        break;

                    case 17:
                    case 18:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        this.jjstateSet[this.jjnewStateCnt++] = 19;

                        break;

                    case 9:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 5) {
                            kind = 5;
                        }

                        jjCheckNAddStates(10, 12);

                        break;

                    case 12:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 8) {
                            kind = 8;
                        }

                        jjAddStates(17, 18);

                        break;

                    case 19:
                    case 20:

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAdd(20);

                        break;

                    case 22:

                        if ((0x7fffffe07fffffeL & l) == 0L) {
                            break;
                        }

                        if (kind > 2) {
                            kind = 2;
                        }

                        this.jjstateSet[this.jjnewStateCnt++] = 23;

                        break;

                    case 23:
                    case 24:

                        if (kind > 2) {
                            kind = 2;
                        }

                        jjCheckNAdd(24);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else {
                int i2 = (this.curChar & 0xff) >> 6;
                long l2 = 1L << (this.curChar & 077);
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 19:
                    case 20:

                        if ((jjbitVec0[i2] & l2) == 0L) {
                            break;
                        }

                        if (kind > 1) {
                            kind = 1;
                        }

                        jjCheckNAdd(20);

                        break;

                    case 23:
                    case 24:

                        if ((jjbitVec0[i2] & l2) == 0L) {
                            break;
                        }

                        if (kind > 2) {
                            kind = 2;
                        }

                        jjCheckNAdd(24);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            }

            if (kind != 0x7fffffff) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = this.jjnewStateCnt) == (startsAt = 25 - (this.jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                this.curChar = this.input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    private final int jjMoveStringLiteralDfa0_4() {
        return jjMoveNfa_4(1, 0);
    }

    private final int jjMoveNfa_4(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 2;

        int i = 1;
        this.jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++this.jjround == 0x7fffffff) {
                reInitRounds();
            }

            if (this.curChar < 64) {
                long l = 1L << this.curChar;
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 1:

                        if ((0xfffffffbffffffffL & l) != 0L) {
                            if (kind > 21) {
                                kind = 21;
                            }

                            jjCheckNAdd(0);
                        } else if (this.curChar == 34) {
                            if (kind > 22) {
                                kind = 22;
                            }
                        }

                        break;

                    case 0:

                        if ((0xfffffffbffffffffL & l) == 0L) {
                            break;
                        }

                        kind = 21;
                        jjCheckNAdd(0);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l = 1L << (this.curChar & 077);
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 1:
                    case 0:
                        kind = 21;
                        jjCheckNAdd(0);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else {
                int i2 = (this.curChar & 0xff) >> 6;
                long l2 = 1L << (this.curChar & 077);
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 1:
                    case 0:

                        if ((jjbitVec0[i2] & l2) == 0L) {
                            break;
                        }

                        if (kind > 21) {
                            kind = 21;
                        }

                        jjCheckNAdd(0);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            }

            if (kind != 0x7fffffff) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = this.jjnewStateCnt) == (startsAt = 2 - (this.jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                this.curChar = this.input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    private final int jjMoveStringLiteralDfa0_6() {
        switch (this.curChar) {
        case 62:
            return jjStopAtPos(0, 26);

        default:
            return jjMoveNfa_6(0, 0);
        }
    }

    private final int jjMoveNfa_6(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 1;

        int i = 1;
        this.jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++this.jjround == 0x7fffffff) {
                reInitRounds();
            }

            if (this.curChar < 64) {
                long l = 1L << this.curChar;
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 0:

                        if ((0xbfffffffffffffffL & l) == 0L) {
                            break;
                        }

                        kind = 25;
                        this.jjstateSet[this.jjnewStateCnt++] = 0;

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l = 1L << (this.curChar & 077);
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 0:
                        kind = 25;
                        this.jjstateSet[this.jjnewStateCnt++] = 0;

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else {
                int i2 = (this.curChar & 0xff) >> 6;
                long l2 = 1L << (this.curChar & 077);
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 0:

                        if ((jjbitVec0[i2] & l2) == 0L) {
                            break;
                        }

                        if (kind > 25) {
                            kind = 25;
                        }

                        this.jjstateSet[this.jjnewStateCnt++] = 0;

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            }

            if (kind != 0x7fffffff) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = this.jjnewStateCnt) == (startsAt = 1 - (this.jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                this.curChar = this.input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    private final int jjMoveStringLiteralDfa0_3() {
        return jjMoveNfa_3(1, 0);
    }

    private final int jjMoveNfa_3(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 2;

        int i = 1;
        this.jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++this.jjround == 0x7fffffff) {
                reInitRounds();
            }

            if (this.curChar < 64) {
                long l = 1L << this.curChar;
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 1:

                        if ((0xffffff7fffffffffL & l) != 0L) {
                            if (kind > 19) {
                                kind = 19;
                            }

                            jjCheckNAdd(0);
                        } else if (this.curChar == 39) {
                            if (kind > 20) {
                                kind = 20;
                            }
                        }

                        break;

                    case 0:

                        if ((0xffffff7fffffffffL & l) == 0L) {
                            break;
                        }

                        kind = 19;
                        jjCheckNAdd(0);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l = 1L << (this.curChar & 077);
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 1:
                    case 0:
                        kind = 19;
                        jjCheckNAdd(0);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else {
                int i2 = (this.curChar & 0xff) >> 6;
                long l2 = 1L << (this.curChar & 077);
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 1:
                    case 0:

                        if ((jjbitVec0[i2] & l2) == 0L) {
                            break;
                        }

                        if (kind > 19) {
                            kind = 19;
                        }

                        jjCheckNAdd(0);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            }

            if (kind != 0x7fffffff) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = this.jjnewStateCnt) == (startsAt = 2 - (this.jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                this.curChar = this.input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    private final int jjMoveStringLiteralDfa0_2() {
        switch (this.curChar) {
        case 34:
            return jjStopAtPos(0, 17);

        case 39:
            return jjStopAtPos(0, 16);

        default:
            return jjMoveNfa_2(0, 0);
        }
    }

    private final int jjMoveNfa_2(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 3;

        int i = 1;
        this.jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++this.jjround == 0x7fffffff) {
                reInitRounds();
            }

            if (this.curChar < 64) {
                long l = 1L << this.curChar;
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 0:

                        if ((0x9fffff7affffd9ffL & l) != 0L) {
                            if (kind > 15) {
                                kind = 15;
                            }

                            jjCheckNAdd(1);
                        } else if ((0x100002600L & l) != 0L) {
                            if (kind > 18) {
                                kind = 18;
                            }

                            jjCheckNAdd(2);
                        }

                        break;

                    case 1:

                        if ((0xbffffffeffffd9ffL & l) == 0L) {
                            break;
                        }

                        if (kind > 15) {
                            kind = 15;
                        }

                        jjCheckNAdd(1);

                        break;

                    case 2:

                        if ((0x100002600L & l) == 0L) {
                            break;
                        }

                        kind = 18;
                        jjCheckNAdd(2);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l = 1L << (this.curChar & 077);
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 0:
                    case 1:

                        if (kind > 15) {
                            kind = 15;
                        }

                        jjCheckNAdd(1);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else {
                int i2 = (this.curChar & 0xff) >> 6;
                long l2 = 1L << (this.curChar & 077);
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 0:
                    case 1:

                        if ((jjbitVec0[i2] & l2) == 0L) {
                            break;
                        }

                        if (kind > 15) {
                            kind = 15;
                        }

                        jjCheckNAdd(1);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            }

            if (kind != 0x7fffffff) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = this.jjnewStateCnt) == (startsAt = 3 - (this.jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                this.curChar = this.input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    private final int jjStopStringLiteralDfa_5(int pos, long active0) {
        switch (pos) {
        case 0:

            if ((active0 & 0x1000000L) != 0L) {
                this.jjmatchedKind = 23;

                return -1;
            }

            return -1;

        case 1:

            if ((active0 & 0x1000000L) != 0L) {
                if (this.jjmatchedPos == 0) {
                    this.jjmatchedKind = 23;
                    this.jjmatchedPos = 0;
                }

                return -1;
            }

            return -1;

        default:
            return -1;
        }
    }

    private final int jjStartNfa_5(int pos, long active0) {
        return jjMoveNfa_5(jjStopStringLiteralDfa_5(pos, active0), pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_5() {
        switch (this.curChar) {
        case 45:
            return jjMoveStringLiteralDfa1_5(0x1000000L);

        default:
            return jjMoveNfa_5(1, 0);
        }
    }

    private final int jjMoveStringLiteralDfa1_5(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        } catch (java.io.IOException e) {
            jjStopStringLiteralDfa_5(0, active0);

            return 1;
        }

        switch (this.curChar) {
        case 45:
            return jjMoveStringLiteralDfa2_5(active0, 0x1000000L);

        default:
            break;
        }

        return jjStartNfa_5(0, active0);
    }

    private final int jjMoveStringLiteralDfa2_5(long old0, long active0) {
        if ((active0 &= old0) == 0L) {
            return jjStartNfa_5(0, old0);
        }

        try {
            this.curChar = this.input_stream.readChar();
        } catch (java.io.IOException e) {
            jjStopStringLiteralDfa_5(1, active0);

            return 2;
        }

        switch (this.curChar) {
        case 62:

            if ((active0 & 0x1000000L) != 0L) {
                return jjStopAtPos(2, 24);
            }

            break;

        default:
            break;
        }

        return jjStartNfa_5(1, active0);
    }

    private final int jjMoveNfa_5(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 2;

        int i = 1;
        this.jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++this.jjround == 0x7fffffff) {
                reInitRounds();
            }

            if (this.curChar < 64) {
                long l = 1L << this.curChar;
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 1:

                        if ((0xffffdfffffffffffL & l) != 0L) {
                            if (kind > 23) {
                                kind = 23;
                            }

                            jjCheckNAdd(0);
                        } else if (this.curChar == 45) {
                            if (kind > 23) {
                                kind = 23;
                            }
                        }

                        break;

                    case 0:

                        if ((0xffffdfffffffffffL & l) == 0L) {
                            break;
                        }

                        kind = 23;
                        jjCheckNAdd(0);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l = 1L << (this.curChar & 077);
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 1:
                    case 0:
                        kind = 23;
                        jjCheckNAdd(0);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else {
                int i2 = (this.curChar & 0xff) >> 6;
                long l2 = 1L << (this.curChar & 077);
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 1:
                    case 0:

                        if ((jjbitVec0[i2] & l2) == 0L) {
                            break;
                        }

                        if (kind > 23) {
                            kind = 23;
                        }

                        jjCheckNAdd(0);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            }

            if (kind != 0x7fffffff) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = this.jjnewStateCnt) == (startsAt = 2 - (this.jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                this.curChar = this.input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    private final int jjStartNfaWithStates_1(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;

        try {
            this.curChar = this.input_stream.readChar();
        } catch (java.io.IOException e) {
            return pos + 1;
        }

        return jjMoveNfa_1(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
        case 34:
            return jjStopAtPos(0, 17);

        case 39:
            return jjStopAtPos(0, 16);

        case 61:
            return jjStartNfaWithStates_1(0, 13, 3);

        default:
            return jjMoveNfa_1(0, 0);
        }
    }

    private final int jjMoveNfa_1(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 6;

        int i = 1;
        this.jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++this.jjround == 0x7fffffff) {
                reInitRounds();
            }

            if (this.curChar < 64) {
                long l = 1L << this.curChar;
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 0:

                        if ((0x9fffff7affffd9ffL & l) != 0L) {
                            if (kind > 12) {
                                kind = 12;
                            }

                            jjCheckNAdd(1);
                        } else if ((0x100002600L & l) != 0L) {
                            if (kind > 18) {
                                kind = 18;
                            }

                            jjCheckNAdd(5);
                        } else if (this.curChar == 61) {
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                        } else if (this.curChar == 62) {
                            if (kind > 14) {
                                kind = 14;
                            }
                        }

                        break;

                    case 1:

                        if ((0x9ffffffeffffd9ffL & l) == 0L) {
                            break;
                        }

                        if (kind > 12) {
                            kind = 12;
                        }

                        jjCheckNAdd(1);

                        break;

                    case 2:
                    case 3:

                        if ((this.curChar == 62) && (kind > 14)) {
                            kind = 14;
                        }

                        break;

                    case 4:

                        if (this.curChar == 61) {
                            this.jjstateSet[this.jjnewStateCnt++] = 3;
                        }

                        break;

                    case 5:

                        if ((0x100002600L & l) == 0L) {
                            break;
                        }

                        kind = 18;
                        jjCheckNAdd(5);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l = 1L << (this.curChar & 077);
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 0:
                    case 1:

                        if (kind > 12) {
                            kind = 12;
                        }

                        jjCheckNAdd(1);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else {
                int i2 = (this.curChar & 0xff) >> 6;
                long l2 = 1L << (this.curChar & 077);
MatchLoop: 
                do {
                    switch (this.jjstateSet[--i]) {
                    case 0:
                    case 1:

                        if ((jjbitVec0[i2] & l2) == 0L) {
                            break;
                        }

                        if (kind > 12) {
                            kind = 12;
                        }

                        jjCheckNAdd(1);

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            }

            if (kind != 0x7fffffff) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = this.jjnewStateCnt) == (startsAt = 6 - (this.jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                this.curChar = this.input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    /**
     * Reinitialize the parser, passing the stream
     * @param stream The stream to parse
     */
    public void reInit(SimpleCharStream stream) {
        this.jjmatchedPos = this.jjnewStateCnt = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        reInitRounds();
    }

    private final void reInitRounds() {
        int i;
        this.jjround = 0x80000001;

        for (i = 25; i-- > 0;)
            this.jjrounds[i] = 0x80000000;
    }

    /**
     * Reinitialize the parser with a certain parser state
     * @param stream The stream to parse
     * @param lexState The parser state
     */
    public void reInit(SimpleCharStream stream, int lexState) {
        reInit(stream);
        switchTo(lexState);
    }

    /**
     * Switch to a new parser state
     * @param lexState The state
     */
    public void switchTo(int lexState) {
        if ((lexState >= 7) || (lexState < 0)) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState +
                ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
        }
        this.curLexState = lexState;
    }

    private final Token jjFillToken() {
        Token t = Token.newToken(this.jjmatchedKind);
        t.kind = this.jjmatchedKind;

        String im = jjstrLiteralImages[this.jjmatchedKind];
        t.image = (im == null) ? this.input_stream.getImage() : im;
        t.beginLine = this.input_stream.getBeginLine();
        t.beginColumn = this.input_stream.getBeginColumn();
        t.endLine = this.input_stream.getEndLine();
        t.endColumn = this.input_stream.getEndColumn();

        return t;
    }

    /**
     * Get the next token
     * @return The token
     */
    public final Token getNextToken() {
        Token matchedToken;
        int curPos = 0;

EOFLoop: 
        for (;;) {
            try {
                this.curChar = this.input_stream.beginToken();
            } catch (java.io.IOException e) {
                this.jjmatchedKind = 0;
                matchedToken = jjFillToken();

                return matchedToken;
            }

            switch (this.curLexState) {
            case 0:
                this.jjmatchedKind = 0x7fffffff;
                this.jjmatchedPos = 0;
                curPos = jjMoveStringLiteralDfa0_0();

                if ((this.jjmatchedPos == 0) && (this.jjmatchedKind > 11)) {
                    this.jjmatchedKind = 11;
                }

                break;

            case 1:
                this.jjmatchedKind = 0x7fffffff;
                this.jjmatchedPos = 0;
                curPos = jjMoveStringLiteralDfa0_1();

                break;

            case 2:
                this.jjmatchedKind = 0x7fffffff;
                this.jjmatchedPos = 0;
                curPos = jjMoveStringLiteralDfa0_2();

                break;

            case 3:
                this.jjmatchedKind = 0x7fffffff;
                this.jjmatchedPos = 0;
                curPos = jjMoveStringLiteralDfa0_3();

                break;

            case 4:
                this.jjmatchedKind = 0x7fffffff;
                this.jjmatchedPos = 0;
                curPos = jjMoveStringLiteralDfa0_4();

                break;

            case 5:
                this.jjmatchedKind = 0x7fffffff;
                this.jjmatchedPos = 0;
                curPos = jjMoveStringLiteralDfa0_5();

                break;

            case 6:
                this.jjmatchedKind = 0x7fffffff;
                this.jjmatchedPos = 0;
                curPos = jjMoveStringLiteralDfa0_6();

                break;
            }

            if (this.jjmatchedKind != 0x7fffffff) {
                if ((this.jjmatchedPos + 1) < curPos) {
                    this.input_stream.backup(curPos - this.jjmatchedPos - 1);
                }

                if ((jjtoToken[this.jjmatchedKind >> 6] & (1L << (this.jjmatchedKind & 077))) != 0L) {
                    matchedToken = jjFillToken();

                    if (jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = jjnewLexState[this.jjmatchedKind];
                    }

                    return matchedToken;
                }
                if (jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = jjnewLexState[this.jjmatchedKind];
                }

                continue EOFLoop;
            }

            int error_line = this.input_stream.getEndLine();
            int error_column = this.input_stream.getEndColumn();
            String error_after = null;
            boolean EOFSeen = false;

            try {
                this.input_stream.readChar();
                this.input_stream.backup(1);
            } catch (java.io.IOException e1) {
                EOFSeen = true;
                error_after = (curPos <= 1) ? "" : this.input_stream.getImage();

                if ((this.curChar == '\n') || (this.curChar == '\r')) {
                    error_line++;
                    error_column = 0;
                } else {
                    error_column++;
                }
            }

            if (!EOFSeen) {
                this.input_stream.backup(1);
                error_after = (curPos <= 1) ? "" : this.input_stream.getImage();
            }

            throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after,
                this.curChar, TokenMgrError.LEXICAL_ERROR);
        }
    }
}
