/*
 * $Id: Token.java,v 1.3 2003/03/04 17:46:47 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
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
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
package org.lenya.lucene.html;

/**
 * Describes the input token stream.
 */
public class Token {
    /**
     * An integer that describes the kind of this token.  This numbering system is determined by
     * JavaCCParser, and a table of these numbers is stored in the file ...Constants.java.
     */
    public int kind;

    /**
     * beginLine and beginColumn describe the position of the first character of this token;
     * endLine and endColumn describe the position of the last character of this token.
     */
    public int beginLine;

    /**
     * beginLine and beginColumn describe the position of the first character of this token;
     * endLine and endColumn describe the position of the last character of this token.
     */
    public int beginColumn;

    /**
     * beginLine and beginColumn describe the position of the first character of this token;
     * endLine and endColumn describe the position of the last character of this token.
     */
    public int endLine;

    /**
     * beginLine and beginColumn describe the position of the first character of this token;
     * endLine and endColumn describe the position of the last character of this token.
     */
    public int endColumn;

    /** The string image of the token. */
    public String image;

    /**
     * A reference to the next regular (non-special) token from the input stream.  If this is the
     * last token from the input stream, or if the token manager has not read tokens beyond this
     * one, this field is set to null.  This is true only if this token is also a regular token.
     * Otherwise, see below for a description of the contents of this field.
     */
    public Token next;

    /**
     * This field is used to access special tokens that occur prior to this token, but after the
     * immediately preceding regular (non-special) token. If there are no such special tokens,
     * this field is set to null. When there are more than one such special token, this field
     * refers to the last of these special tokens, which in turn refers to the next previous
     * special token through its specialToken field, and so on until the first special token
     * (whose specialToken field is null). The next fields of special tokens refer to other
     * special tokens that immediately follow it (without an intervening regular token).  If there
     * is no such token, this field is null.
     */
    public Token specialToken;

    /**
     * Returns the image.
     *
     * @return DOCUMENT ME!
     */
    public final String toString() {
        return image;
    }

    /**
     * Returns a new Token object, by default. However, if you want, you can create and return
     * subclass objects based on the value of ofKind. Simply add the cases to the switch for all
     * those special cases. For example, if you have a subclass of Token called IDToken that you
     * want to create if ofKind is ID, simlpy add something like : case MyParserConstants.ID :
     * return new IDToken(); to the following switch statement. Then you can cast matchedToken
     * variable to the appropriate type and use it in your lexical actions.
     *
     * @param ofKind DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static final Token newToken(int ofKind) {
        switch (ofKind) {
        default:
            return new Token();
        }
    }
}
