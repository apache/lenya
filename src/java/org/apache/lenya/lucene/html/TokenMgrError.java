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
 * The Token Manager Error class
 */
public class TokenMgrError extends Error {
    /*
     * Ordinals for various reasons why an Error of this type can be thrown.
     */

    /** Lexical error occured. */
    static final int LEXICAL_ERROR = 0;

    /** An attempt wass made to create a second instance of a static token manager. */
    static final int STATIC_LEXER_ERROR = 1;

    /** Tried to change to an invalid lexical state. */
    static final int INVALID_LEXICAL_STATE = 2;

    /** Detected (and bailed out of) an infinite loop in the token manager. */
    static final int LOOP_DETECTED = 3;

    /** Indicates the reason why the exception is thrown. It will have one of the above 4 values. */
    int errorCode;

    /**
     * Constructors of various flavors follow.
     */
    public TokenMgrError() {
        // do nothing
    }

    /**
     * Creates a new TokenMgrError object.
     * @param message The message
     * @param reason The error code
     */
    public TokenMgrError(String message, int reason) {
        super(message);
        this.errorCode = reason;
    }

    /**
     * Creates a new TokenMgrError object.
     * @param EOFSeen indicates if EOF caused the lexical error
     * @param lexState lexical state in which this error occured
     * @param errorLine line number when the error occured
     * @param errorColumn column number when the error occured
     * @param errorAfter prefix that was seen before this error occured
     * @param curChar the offending character
     * @param reason The error code
     */
    public TokenMgrError(boolean EOFSeen, int lexState, int errorLine, int errorColumn,
        String errorAfter, char curChar, int reason) {
        this(lexicalError(EOFSeen, lexState, errorLine, errorColumn, errorAfter, curChar), reason);
    }

    /**
     * Replaces unprintable characters by their espaced (or unicode escaped) equivalents in the
     * given string
     * @param str The string
     * @return The escaped string
     */
    protected static final String addEscapes(String str) {
        StringBuffer retval = new StringBuffer();
        char ch;

        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
            case 0:
                continue;

            case '\b':
                retval.append("\\b");
                continue;

            case '\t':
                retval.append("\\t");
                continue;

            case '\n':
                retval.append("\\n");
                continue;

            case '\f':
                retval.append("\\f");
                continue;

            case '\r':
                retval.append("\\r");
                continue;

            case '\"':
                retval.append("\\\"");
                continue;

            case '\'':
                retval.append("\\\'");
                continue;

            case '\\':
                retval.append("\\\\");
                continue;

            default:

                if (((ch = str.charAt(i)) < 0x20) || (ch > 0x7e)) {
                    String s = "0000" + Integer.toString(ch, 16);
                    retval.append("\\u" + s.substring(s.length() - 4, s.length()));
                } else {
                    retval.append(ch);
                }

                continue;
            }
        }

        return retval.toString();
    }

    /**
     * Returns a detailed message for the Error when it is thrown by the token manager to indicate
     * a lexical error. Note: You can customize the lexical error message by modifying this method.
     * @param EOFSeen indicates if EOF caused the lexical error
     * @param lexState lexical state in which this error occured
     * @param errorLine line number when the error occured
     * @param errorColumn column number when the error occured
     * @param errorAfter prefix that was seen before this error occured
     * @param curChar the offending character
     * @return The error message
     */
    private static final String lexicalError(boolean EOFSeen, int lexState, int errorLine,
        int errorColumn, String errorAfter, char curChar) {
        return ("Lexical error at line " + errorLine + ", column " + errorColumn +
        ".  Encountered: " +
        (EOFSeen ? "<EOF> "
                 : (("\"" + addEscapes(String.valueOf(curChar)) + "\"") + " (" + (int) curChar +
        "), ")) + "after : \"" + addEscapes(errorAfter) + "\"");
    }

    /**
     * You can also modify the body of this method to customize your error messages. For example,
     * cases like LOOP_DETECTED and INVALID_LEXICAL_STATE are not of end-users concern, so you can
     * return something like :  "Internal Error : Please file a bug report .... " from this method
     * for such cases in the release version of your parser.
     * @return The error message
     */
    public String getMessage() {
        return super.getMessage();
    }
}
