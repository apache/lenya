/*
 * $Id: StringUtil.java,v 1.5 2003/02/17 13:06:57 egli Exp $
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
package org.wyona.util;

/**
 *
 * @author <a href="mailto:tinu@email.ch">Martin Lüthi</a>
 * @version Utilty Class for String handling
 */
public class StringUtil {
    /**
     * DOCUMENT ME!
     *
     * @param s Main string.
     * @param find The substring to find.
     * @param rep Replaces the find substring.
     *
     * @return The new string.  Replaces one substring with another within a main string.
     */
    public static String replace(String s, String find, String rep) {
        return replace(new StringBuffer(s), find, rep).toString();
    }

    /**
     * Replaces one substring with another within a main string.
     *
     * @param sb Main string. (StringBuffer)
     * @param find The substring to find.
     * @param rep Replaces the find substring.
     *
     * @return The new string. (StringBuffer)
     */
    public static StringBuffer replace(StringBuffer sb, String find, String rep) {
        StringBuffer buf = new StringBuffer(sb.toString());
        int startIndex;
        int endIndex;
        boolean done = false;

        startIndex = endIndex = 0;

        // Halts if they is not need to do any replacements
        if (!find.equals(rep)) {
            String s = buf.toString();

            // Continues while more substring(s) (find) exist
            while (!done) {
                // Grab the position of the substring (find)
                if ((startIndex = s.indexOf(find)) >= 0) {
                    // Replace "find" with "rep"
                    endIndex = startIndex + find.length();
                    buf.delete(startIndex, endIndex);
                    buf.insert(startIndex, rep);
                    s = buf.toString();
                } else {
                    done = true;
                }
            }
        }

        return buf;
    }
}
 // StringUtil
