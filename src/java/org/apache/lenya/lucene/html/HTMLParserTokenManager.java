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

public class HTMLParserTokenManager implements HTMLParserConstants {
    static final long[] jjbitVec0 = { 0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL };
    static final int[] jjnextStates = {
        17, 18, 21, 12, 14, 5, 8, 0, 4, 6, 0, 4, 6, 5, 0, 4, 6, 12, 13,
    };
    public static final String[] jjstrLiteralImages = {
        "", null, null, "\74\41\55\55", "\74\41", null, null, null, null, null, null, null, null,
        "\75", null, null, "\47", "\42", null, null, null, null, null, null, "\55\55\76", null,
        "\76",
    };
    public static final String[] lexStateNames = {
        "DEFAULT", "WithinTag", "AfterEquals", "WithinQuote1", "WithinQuote2", "WithinComment1",
        "WithinComment2",
    };
    public static final int[] jjnewLexState = {
        -1, 1, 1, 5, 6, -1, -1, -1, -1, -1, -1, -1, -1, 2, 0, 1, 3, 4, -1, -1, 1, -1, 1, -1, 0, -1,
        0,
    };
    static final long[] jjtoToken = { 0x7fbfb3fL, };
    static final long[] jjtoSkip = { 0x40000L, };
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
     * Creates a new HTMLParserTokenManager object.
     *
     * @param stream DOCUMENT ME!
     */
    public HTMLParserTokenManager(SimpleCharStream stream) {
        if (SimpleCharStream.staticFlag) {
            throw new Error(
                "ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
        }

        input_stream = stream;
    }

    /**
     * Creates a new HTMLParserTokenManager object.
     *
     * @param stream DOCUMENT ME!
     * @param lexState DOCUMENT ME!
     */
    public HTMLParserTokenManager(SimpleCharStream stream, int lexState) {
        this(stream);
        SwitchTo(lexState);
    }

    /**
     * DOCUMENT ME!
     *
     * @param ds DOCUMENT ME!
     */
    public void setDebugStream(java.io.PrintStream ds) {
        debugStream = ds;
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
        jjmatchedKind = kind;
        jjmatchedPos = pos;

        return pos + 1;
    }

    private final int jjMoveStringLiteralDfa0_0() {
        switch (curChar) {
        case 60:
            return jjMoveStringLiteralDfa1_0(0x18L);

        default:
            return jjMoveNfa_0(11, 0);
        }
    }

    private final int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            jjStopStringLiteralDfa_0(0, active0);

            return 1;
        }

        switch (curChar) {
        case 33:

            if ((active0 & 0x10L) != 0L) {
                jjmatchedKind = 4;
                jjmatchedPos = 1;
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
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            jjStopStringLiteralDfa_0(1, active0);

            return 2;
        }

        switch (curChar) {
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
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            jjStopStringLiteralDfa_0(2, active0);

            return 3;
        }

        switch (curChar) {
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
        if (jjrounds[state] != jjround) {
            jjstateSet[jjnewStateCnt++] = state;
            jjrounds[state] = jjround;
        }
    }

    private final void jjAddStates(int start, int end) {
        do {
            jjstateSet[jjnewStateCnt++] = jjnextStates[start];
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
        jjnewStateCnt = 25;

        int i = 1;
        jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++jjround == 0x7fffffff) {
                ReInitRounds();
            }

            if (curChar < 64) {
                long l = 1L << curChar;
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
                    case 11:

                        if ((0x3ff000000000000L & l) != 0L) {
                            jjCheckNAddTwoStates(7, 2);
                        } else if ((0x100002600L & l) != 0L) {
                            if (kind > 9) {
                                kind = 9;
                            }

                            jjCheckNAdd(10);
                        } else if (curChar == 60) {
                            jjCheckNAddStates(0, 2);
                        } else if (curChar == 38) {
                            jjAddStates(3, 4);
                        } else if (curChar == 36) {
                            jjstateSet[jjnewStateCnt++] = 1;
                        }

                        if ((0x3ff000000000000L & l) != 0L) {
                            if (kind > 5) {
                                kind = 5;
                            }

                            jjCheckNAddStates(5, 9);
                        }

                        break;

                    case 17:

                        if (curChar == 33) {
                            jjstateSet[jjnewStateCnt++] = 22;
                        } else if (curChar == 47) {
                            jjCheckNAdd(18);
                        }

                        break;

                    case 0:

                        if (curChar == 36) {
                            jjstateSet[jjnewStateCnt++] = 1;
                        }

                        break;

                    case 1:

                        if ((0x3ff000000000000L & l) != 0L) {
                            jjCheckNAdd(2);
                        }

                        break;

                    case 2:

                        if ((0x500000000000L & l) != 0L) {
                            jjstateSet[jjnewStateCnt++] = 3;
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

                        if (curChar != 34) {
                            break;
                        }

                        if (kind > 5) {
                            kind = 5;
                        }

                        jjCheckNAddStates(10, 12);

                        break;

                    case 8:

                        if ((0x208000000000L & l) != 0L) {
                            jjstateSet[jjnewStateCnt++] = 9;
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

                        if ((curChar == 59) && (kind > 8)) {
                            kind = 8;
                        }

                        break;

                    case 14:

                        if (curChar == 35) {
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

                        if (curChar == 60) {
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

                        if (curChar == 33) {
                            jjstateSet[jjnewStateCnt++] = 22;
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
            } else if (curChar < 128) {
                long l = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
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

                        jjstateSet[jjnewStateCnt++] = 19;

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

                        jjstateSet[jjnewStateCnt++] = 23;

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
                int i2 = (curChar & 0xff) >> 6;
                long l2 = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
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
                jjmatchedKind = kind;
                jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = jjnewStateCnt) == (startsAt = 25 - (jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                curChar = input_stream.readChar();
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
        jjnewStateCnt = 2;

        int i = 1;
        jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++jjround == 0x7fffffff) {
                ReInitRounds();
            }

            if (curChar < 64) {
                long l = 1L << curChar;
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
                    case 1:

                        if ((0xfffffffbffffffffL & l) != 0L) {
                            if (kind > 21) {
                                kind = 21;
                            }

                            jjCheckNAdd(0);
                        } else if (curChar == 34) {
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
            } else if (curChar < 128) {
                long l = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
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
                int i2 = (curChar & 0xff) >> 6;
                long l2 = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
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
                jjmatchedKind = kind;
                jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = jjnewStateCnt) == (startsAt = 2 - (jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                curChar = input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    private final int jjMoveStringLiteralDfa0_6() {
        switch (curChar) {
        case 62:
            return jjStopAtPos(0, 26);

        default:
            return jjMoveNfa_6(0, 0);
        }
    }

    private final int jjMoveNfa_6(int startState, int curPos) {
        int startsAt = 0;
        jjnewStateCnt = 1;

        int i = 1;
        jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++jjround == 0x7fffffff) {
                ReInitRounds();
            }

            if (curChar < 64) {
                long l = 1L << curChar;
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
                    case 0:

                        if ((0xbfffffffffffffffL & l) == 0L) {
                            break;
                        }

                        kind = 25;
                        jjstateSet[jjnewStateCnt++] = 0;

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else if (curChar < 128) {
                long l = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
                    case 0:
                        kind = 25;
                        jjstateSet[jjnewStateCnt++] = 0;

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            } else {
                int i2 = (curChar & 0xff) >> 6;
                long l2 = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
                    case 0:

                        if ((jjbitVec0[i2] & l2) == 0L) {
                            break;
                        }

                        if (kind > 25) {
                            kind = 25;
                        }

                        jjstateSet[jjnewStateCnt++] = 0;

                        break;

                    default:
                        break;
                    }
                } while (i != startsAt);
            }

            if (kind != 0x7fffffff) {
                jjmatchedKind = kind;
                jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = jjnewStateCnt) == (startsAt = 1 - (jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                curChar = input_stream.readChar();
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
        jjnewStateCnt = 2;

        int i = 1;
        jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++jjround == 0x7fffffff) {
                ReInitRounds();
            }

            if (curChar < 64) {
                long l = 1L << curChar;
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
                    case 1:

                        if ((0xffffff7fffffffffL & l) != 0L) {
                            if (kind > 19) {
                                kind = 19;
                            }

                            jjCheckNAdd(0);
                        } else if (curChar == 39) {
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
            } else if (curChar < 128) {
                long l = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
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
                int i2 = (curChar & 0xff) >> 6;
                long l2 = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
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
                jjmatchedKind = kind;
                jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = jjnewStateCnt) == (startsAt = 2 - (jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                curChar = input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    private final int jjMoveStringLiteralDfa0_2() {
        switch (curChar) {
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
        jjnewStateCnt = 3;

        int i = 1;
        jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++jjround == 0x7fffffff) {
                ReInitRounds();
            }

            if (curChar < 64) {
                long l = 1L << curChar;
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
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
            } else if (curChar < 128) {
                long l = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
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
                int i2 = (curChar & 0xff) >> 6;
                long l2 = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
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
                jjmatchedKind = kind;
                jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = jjnewStateCnt) == (startsAt = 3 - (jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                curChar = input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    private final int jjStopStringLiteralDfa_5(int pos, long active0) {
        switch (pos) {
        case 0:

            if ((active0 & 0x1000000L) != 0L) {
                jjmatchedKind = 23;

                return -1;
            }

            return -1;

        case 1:

            if ((active0 & 0x1000000L) != 0L) {
                if (jjmatchedPos == 0) {
                    jjmatchedKind = 23;
                    jjmatchedPos = 0;
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
        switch (curChar) {
        case 45:
            return jjMoveStringLiteralDfa1_5(0x1000000L);

        default:
            return jjMoveNfa_5(1, 0);
        }
    }

    private final int jjMoveStringLiteralDfa1_5(long active0) {
        try {
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            jjStopStringLiteralDfa_5(0, active0);

            return 1;
        }

        switch (curChar) {
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
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            jjStopStringLiteralDfa_5(1, active0);

            return 2;
        }

        switch (curChar) {
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
        jjnewStateCnt = 2;

        int i = 1;
        jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++jjround == 0x7fffffff) {
                ReInitRounds();
            }

            if (curChar < 64) {
                long l = 1L << curChar;
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
                    case 1:

                        if ((0xffffdfffffffffffL & l) != 0L) {
                            if (kind > 23) {
                                kind = 23;
                            }

                            jjCheckNAdd(0);
                        } else if (curChar == 45) {
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
            } else if (curChar < 128) {
                long l = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
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
                int i2 = (curChar & 0xff) >> 6;
                long l2 = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
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
                jjmatchedKind = kind;
                jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = jjnewStateCnt) == (startsAt = 2 - (jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                curChar = input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    private final int jjStartNfaWithStates_1(int pos, int kind, int state) {
        jjmatchedKind = kind;
        jjmatchedPos = pos;

        try {
            curChar = input_stream.readChar();
        } catch (java.io.IOException e) {
            return pos + 1;
        }

        return jjMoveNfa_1(state, pos + 1);
    }

    private final int jjMoveStringLiteralDfa0_1() {
        switch (curChar) {
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
        jjnewStateCnt = 6;

        int i = 1;
        jjstateSet[0] = startState;

        int kind = 0x7fffffff;

        for (;;) {
            if (++jjround == 0x7fffffff) {
                ReInitRounds();
            }

            if (curChar < 64) {
                long l = 1L << curChar;
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
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
                        } else if (curChar == 61) {
                            jjstateSet[jjnewStateCnt++] = 3;
                        } else if (curChar == 62) {
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

                        if ((curChar == 62) && (kind > 14)) {
                            kind = 14;
                        }

                        break;

                    case 4:

                        if (curChar == 61) {
                            jjstateSet[jjnewStateCnt++] = 3;
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
            } else if (curChar < 128) {
                long l = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
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
                int i2 = (curChar & 0xff) >> 6;
                long l2 = 1L << (curChar & 077);
MatchLoop: 
                do {
                    switch (jjstateSet[--i]) {
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
                jjmatchedKind = kind;
                jjmatchedPos = curPos;
                kind = 0x7fffffff;
            }

            ++curPos;

            if ((i = jjnewStateCnt) == (startsAt = 6 - (jjnewStateCnt = startsAt))) {
                return curPos;
            }

            try {
                curChar = input_stream.readChar();
            } catch (java.io.IOException e) {
                return curPos;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param stream DOCUMENT ME!
     */
    public void ReInit(SimpleCharStream stream) {
        jjmatchedPos = jjnewStateCnt = 0;
        curLexState = defaultLexState;
        input_stream = stream;
        ReInitRounds();
    }

    private final void ReInitRounds() {
        int i;
        jjround = 0x80000001;

        for (i = 25; i-- > 0;)
            jjrounds[i] = 0x80000000;
    }

    /**
     * DOCUMENT ME!
     *
     * @param stream DOCUMENT ME!
     * @param lexState DOCUMENT ME!
     */
    public void ReInit(SimpleCharStream stream, int lexState) {
        ReInit(stream);
        SwitchTo(lexState);
    }

    /**
     * DOCUMENT ME!
     *
     * @param lexState DOCUMENT ME!
     */
    public void SwitchTo(int lexState) {
        if ((lexState >= 7) || (lexState < 0)) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState +
                ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
        } else {
            curLexState = lexState;
        }
    }

    private final Token jjFillToken() {
        Token t = Token.newToken(jjmatchedKind);
        t.kind = jjmatchedKind;

        String im = jjstrLiteralImages[jjmatchedKind];
        t.image = (im == null) ? input_stream.GetImage() : im;
        t.beginLine = input_stream.getBeginLine();
        t.beginColumn = input_stream.getBeginColumn();
        t.endLine = input_stream.getEndLine();
        t.endColumn = input_stream.getEndColumn();

        return t;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public final Token getNextToken() {
        Token matchedToken;
        int curPos = 0;

EOFLoop: 
        for (;;) {
            try {
                curChar = input_stream.BeginToken();
            } catch (java.io.IOException e) {
                jjmatchedKind = 0;
                matchedToken = jjFillToken();

                return matchedToken;
            }

            switch (curLexState) {
            case 0:
                jjmatchedKind = 0x7fffffff;
                jjmatchedPos = 0;
                curPos = jjMoveStringLiteralDfa0_0();

                if ((jjmatchedPos == 0) && (jjmatchedKind > 11)) {
                    jjmatchedKind = 11;
                }

                break;

            case 1:
                jjmatchedKind = 0x7fffffff;
                jjmatchedPos = 0;
                curPos = jjMoveStringLiteralDfa0_1();

                break;

            case 2:
                jjmatchedKind = 0x7fffffff;
                jjmatchedPos = 0;
                curPos = jjMoveStringLiteralDfa0_2();

                break;

            case 3:
                jjmatchedKind = 0x7fffffff;
                jjmatchedPos = 0;
                curPos = jjMoveStringLiteralDfa0_3();

                break;

            case 4:
                jjmatchedKind = 0x7fffffff;
                jjmatchedPos = 0;
                curPos = jjMoveStringLiteralDfa0_4();

                break;

            case 5:
                jjmatchedKind = 0x7fffffff;
                jjmatchedPos = 0;
                curPos = jjMoveStringLiteralDfa0_5();

                break;

            case 6:
                jjmatchedKind = 0x7fffffff;
                jjmatchedPos = 0;
                curPos = jjMoveStringLiteralDfa0_6();

                break;
            }

            if (jjmatchedKind != 0x7fffffff) {
                if ((jjmatchedPos + 1) < curPos) {
                    input_stream.backup(curPos - jjmatchedPos - 1);
                }

                if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L) {
                    matchedToken = jjFillToken();

                    if (jjnewLexState[jjmatchedKind] != -1) {
                        curLexState = jjnewLexState[jjmatchedKind];
                    }

                    return matchedToken;
                } else {
                    if (jjnewLexState[jjmatchedKind] != -1) {
                        curLexState = jjnewLexState[jjmatchedKind];
                    }

                    continue EOFLoop;
                }
            }

            int error_line = input_stream.getEndLine();
            int error_column = input_stream.getEndColumn();
            String error_after = null;
            boolean EOFSeen = false;

            try {
                input_stream.readChar();
                input_stream.backup(1);
            } catch (java.io.IOException e1) {
                EOFSeen = true;
                error_after = (curPos <= 1) ? "" : input_stream.GetImage();

                if ((curChar == '\n') || (curChar == '\r')) {
                    error_line++;
                    error_column = 0;
                } else {
                    error_column++;
                }
            }

            if (!EOFSeen) {
                input_stream.backup(1);
                error_after = (curPos <= 1) ? "" : input_stream.GetImage();
            }

            throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after,
                curChar, TokenMgrError.LEXICAL_ERROR);
        }
    }
}
