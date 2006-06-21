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
 * This exception is thrown when parse errors are encountered. You can explicitly create objects of
 * this exception type by calling the method generateParseException in the generated parser. You
 * can modify this class to customize your error reporting mechanisms so long as you retain the
 * public fields.
 */
public class ParseException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
     * @param currentTokenVal Value of the current token
     * @param expectedTokenSequencesVal Value of the expected token sequences
     * @param tokenImageVal Value of the image token
     */
    public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal,
        String[] tokenImageVal) {
        super("");
        this.specialConstructor = true;
        this.currentToken = currentTokenVal;
        this.expectedTokenSequences = expectedTokenSequencesVal;
        this.tokenImage = tokenImageVal;
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
        this.specialConstructor = false;
    }

    /**
     * Creates a new ParseException object.
     * @param message The message
     */
    public ParseException(String message) {
        super(message);
        this.specialConstructor = false;
    }

    /**
     * This method has the standard behavior when this object has been created using the standard
     * constructors.  Otherwise, it uses "currentToken" and "expectedTokenSequences" to generate a
     * parse error message and returns it.  If this object has been created due to a parse error,
     * and you do not catch it (it gets thrown from the parser), then this method is called during
     * the printing of the final stack trace, and hence the correct error message gets displayed.
     *
     * @return The exception message
     */
    public String getMessage() {
        if (!this.specialConstructor) {
            return super.getMessage();
        }

        String expected = "";
        StringBuffer buf = new StringBuffer();
        int maxSize = 0;

        for (int i = 0; i < this.expectedTokenSequences.length; i++) {
            if (maxSize < this.expectedTokenSequences[i].length) {
                maxSize = this.expectedTokenSequences[i].length;
            }

            for (int j = 0; j < this.expectedTokenSequences[i].length; j++) {
                buf.append((this.tokenImage[this.expectedTokenSequences[i][j]] + " "));
            }

            if (this.expectedTokenSequences[i][this.expectedTokenSequences[i].length - 1] != 0) {
               buf.append("...");
            }

            buf.append((this.eol + "    "));
        }

        expected = buf.toString();

        String retval = null;
        StringBuffer buf2 = new StringBuffer();
        buf2.append("Encountered \"");
        Token tok = this.currentToken.next;

        for (int i = 0; i < maxSize; i++) {
            if (i != 0) {
                buf2.append(" ");
            }

            if (tok.kind == 0) {
                buf2.append(this.tokenImage[0]);

                break;
            }

            buf2.append(add_escapes(tok.image));
            tok = tok.next;
        }

        buf2.append("\" at line " + this.currentToken.next.beginLine + ", column " +
        this.currentToken.next.beginColumn);
        buf2.append("." + this.eol);

        if (this.expectedTokenSequences.length == 1) {
            buf2.append("Was expecting:" + this.eol + "    ");
        } else {
            buf2.append("Was expecting one of:" + this.eol + "    ");
        }

        buf2.append(expected);
        retval = buf2.toString();

        return retval;
    }

    /**
     * Used to convert raw characters to their escaped version when these raw version cannot be
     * used as part of an ASCII string literal.
     * @param str The string to be escaped
     * @return The escaped string
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