/*
$Id: WildcardFilter.java,v 1.10 2003/07/23 13:21:14 gregor Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.util;

import java.util.StringTokenizer;
import java.util.Vector;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner (http://cocoon.apache.org/lenya)
 * @version 0.12.17 WARNING: This class does not work properly!!! NOTE: matchNew() should be working properly!!!
 */
public class WildcardFilter {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        Vector wildcards = new Vector();

        if (new WildcardFilter().matchNew("/nzz/online/daily/2000/12/08/al/page-article32.html",
                    "*nzz/online/daily/*/1*/*/*/page-article*.htm*", wildcards)) {
            for (int i = 0; i < wildcards.size(); i++) {
                System.out.println((String) wildcards.elementAt(i));
            }

            System.out.println("String matched");
        } else {
            System.out.println("String did not match");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param stringToMatch DOCUMENT ME!
     * @param stringWithWildcards DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean match(String stringToMatch, String stringWithWildcards) {
        StringTokenizer st = new StringTokenizer(stringWithWildcards, "*");
        int length = st.countTokens();
        String firstToken = st.nextToken();
        String lastToken = "";

        for (int i = 1; i < length; i++) {
            lastToken = st.nextToken();
        }

        if ((stringToMatch.indexOf(firstToken) == 0) &&
                (stringToMatch.indexOf(lastToken) == (stringToMatch.length() - lastToken.length()))) {
            return matchInBetween(stringToMatch, stringWithWildcards);
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param stringToMatch DOCUMENT ME!
     * @param stringWithWildcards DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean matchInBetween(String stringToMatch, String stringWithWildcards) {
        StringTokenizer st = new StringTokenizer(stringWithWildcards, "*");
        int length = st.countTokens();

        if (length >= 2) {
            String beforeFirstWildcard = st.nextToken();
            String afterFirstWildcard = st.nextToken();

            for (int i = 2; i < length; i++) {
                afterFirstWildcard = afterFirstWildcard + "*" + st.nextToken();
            }

            int index = stringToMatch.indexOf(beforeFirstWildcard);

            if (index >= 0) {
                if (matchInBetween(stringToMatch.substring(index + beforeFirstWildcard.length()),
                            afterFirstWildcard)) {
                    return true;
                }
            } else {
                return false;
            }
        } else // The end
         {
            if (stringToMatch.indexOf(stringWithWildcards) >= 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param stringToMatch DOCUMENT ME!
     * @param stringWithWildcards DOCUMENT ME!
     * @param wildcards DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean matchNew(String stringToMatch, String stringWithWildcards, Vector wildcards) {
        int index = stringWithWildcards.indexOf("*");

        if (index < 0) {
            if (stringToMatch.equals(stringWithWildcards)) {
                return true;
            } else {
                return false;
            }
        } else if (index == 0) {
            String stringWithWildcardAtBeginningRemoved = stringWithWildcards.substring(1);
            int secondIndex = stringWithWildcardAtBeginningRemoved.indexOf("*");

            if (secondIndex < 0) {
                int indexAfter = stringToMatch.indexOf(stringWithWildcardAtBeginningRemoved);

                if ((indexAfter >= 0) &&
                        (stringWithWildcardAtBeginningRemoved.equals(stringToMatch.substring(
                                indexAfter)))) {
                    //System.out.println(stringToMatch.substring(0,indexAfter));
                    wildcards.addElement(stringToMatch.substring(0, indexAfter));

                    return true;
                } else if (stringWithWildcards.length() == 1) {
                    //System.out.println(stringToMatch);
                    wildcards.addElement(stringToMatch);

                    return true;
                } else {
                    return false;
                }
            } else {
                String before = stringWithWildcardAtBeginningRemoved.substring(0, secondIndex);
                String after = stringWithWildcardAtBeginningRemoved.substring(secondIndex);
                int indexBefore = stringToMatch.indexOf(before);

                if (indexBefore >= 0) {
                    wildcards.addElement(stringToMatch.substring(0, indexBefore));

                    return matchNew(stringToMatch.substring(indexBefore + before.length()), after,
                        wildcards);
                } else {
                    return false;
                }
            }
        } else {
            String before = stringWithWildcards.substring(0, index);
            String after = stringWithWildcards.substring(index);

            if (stringToMatch.indexOf(before) == 0) {
                return matchNew(stringToMatch.substring(before.length()), after, wildcards);
            }
        }

        return false;
    }
}
