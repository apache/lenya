/*
 * $Id: ParseException.java,v 1.5 2003/03/06 20:45:52 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 lenya. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by lenya (http://www.lenya.org)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@lenya.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://www.lenya.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY lenya "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. lenya WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL lenya BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF lenya HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. lenya WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Lenya includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.lucene.html;

/**
 * This exception is thrown when parse errors are encountered. You can explicitly create objects of
 * this exception type by calling the method generateParseException in the generated parser. You
 * can modify this class to customize your error reporting mechanisms so long as you retain the
 * public fields.
 */
public class ParseException extends Exception {
    /**
     * This variable determines which constructor was used to create this object and thereby
     * affects the semantics of the "getMessage" method (see below).
     */
    protected boolean specialConstructor;

    /**
     * This is the last token that has been consumed successfully.  If this object has been created
     * due to a parse error, the token followng this token will (therefore) be the first error
     * token.
     */
    public Token currentToken;

    /**
     * Each entry in this array is an array of integers.  Each array of integers represents a
     * sequence of tokens (by their ordinal values) that is expected at this point of the parse.
     */
    public int[][] expectedTokenSequences;

    /**
     * This is a reference to the "tokenImage" array of the generated parser within which the parse
     * error occurred.  This array is defined in the generated ...Constants interface.
     */
    public String[] tokenImage;

    /** The end of line string for this machine. */
    protected String eol = System.getProperty("line.separator", "\n");

    /**
     * This constructor is used by the method "generateParseException" in the generated parser.
     * Calling this constructor generates a new object of this type with the fields
     * "currentToken", "expectedTokenSequences", and "tokenImage" set.  The boolean flag
     * "specialConstructor" is also set to true to indicate that this constructor was used to
     * create this object. This constructor calls its super class with the empty string to force
     * the "toString" method of parent class "Throwable" to print the error message in the form:
     * ParseException: &lt;result of getMessage&gt;
     *
     * @param currentTokenVal DOCUMENT ME!
     * @param expectedTokenSequencesVal DOCUMENT ME!
     * @param tokenImageVal DOCUMENT ME!
     */
    public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal,
        String[] tokenImageVal) {
        super("");
        specialConstructor = true;
        currentToken = currentTokenVal;
        expectedTokenSequences = expectedTokenSequencesVal;
        tokenImage = tokenImageVal;
    }

    /**
     * The following constructors are for use by you for whatever purpose you can think of.
     * Constructing the exception in this manner makes the exception behave in the normal way -
     * i.e., as documented in the class "Throwable".  The fields "errorToken",
     * "expectedTokenSequences", and "tokenImage" do not contain relevant information.  The JavaCC
     * generated code does not use these constructors.
     */
    public ParseException() {
        super();
        specialConstructor = false;
    }

    /**
     * Creates a new ParseException object.
     *
     * @param message DOCUMENT ME!
     */
    public ParseException(String message) {
        super(message);
        specialConstructor = false;
    }

    /**
     * This method has the standard behavior when this object has been created using the standard
     * constructors.  Otherwise, it uses "currentToken" and "expectedTokenSequences" to generate a
     * parse error message and returns it.  If this object has been created due to a parse error,
     * and you do not catch it (it gets thrown from the parser), then this method is called during
     * the printing of the final stack trace, and hence the correct error message gets displayed.
     *
     * @return DOCUMENT ME!
     */
    public String getMessage() {
        if (!specialConstructor) {
            return super.getMessage();
        }

        String expected = "";
        int maxSize = 0;

        for (int i = 0; i < expectedTokenSequences.length; i++) {
            if (maxSize < expectedTokenSequences[i].length) {
                maxSize = expectedTokenSequences[i].length;
            }

            for (int j = 0; j < expectedTokenSequences[i].length; j++) {
                expected += (tokenImage[expectedTokenSequences[i][j]] + " ");
            }

            if (expectedTokenSequences[i][expectedTokenSequences[i].length - 1] != 0) {
                expected += "...";
            }

            expected += (eol + "    ");
        }

        String retval = "Encountered \"";
        Token tok = currentToken.next;

        for (int i = 0; i < maxSize; i++) {
            if (i != 0) {
                retval += " ";
            }

            if (tok.kind == 0) {
                retval += tokenImage[0];

                break;
            }

            retval += add_escapes(tok.image);
            tok = tok.next;
        }

        retval += ("\" at line " + currentToken.next.beginLine + ", column " +
        currentToken.next.beginColumn);
        retval += ("." + eol);

        if (expectedTokenSequences.length == 1) {
            retval += ("Was expecting:" + eol + "    ");
        } else {
            retval += ("Was expecting one of:" + eol + "    ");
        }

        retval += expected;

        return retval;
    }

    /**
     * Used to convert raw characters to their escaped version when these raw version cannot be
     * used as part of an ASCII string literal.
     *
     * @param str DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected String add_escapes(String str) {
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
}
