/*
 * $Id: DefaultCreator.java,v 1.6 2003/02/20 13:40:40 gregor Exp $
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
package org.wyona.cms.authoring;

import org.dom4j.Node;

import java.io.File;


/**
 * DOCUMENT ME!
 *
 * @author <a href="mailto:juergen.bigalke@wyona.org">Juergen Bigalke</a>
 */
public class DefaultCreator extends DefaultParentChildCreator {

    private String prefix = null;
    private String fname = "/index.xml";
    private String fnameMeta = "/index-meta.xml";
    private String docNameSample = "generic.xml";
    private String docNameMeta = "Meta.xml";

    /**
     * DOCUMENT ME!
     *
     * @param creatorNode DOCUMENT ME!
     */
    public void init(Node creatorNode) {
        if (creatorNode == null) {
            return;
        }

        String s = creatorNode.valueOf("prefix");

        if ((s != null) && !s.equals("")) {
            prefix = s;
        }

        s = creatorNode.valueOf("sample");

        if ((s != null) && !s.equals("")) {
            docNameSample = s;
        }

        s = creatorNode.valueOf("sampleMeta");

        if ((s != null) && !s.equals("")) {
            docNameMeta = s;
        }

        s = creatorNode.valueOf("index");

        if (s == null) {
            s = "";
        }

        fname = s + ".xml";
        fnameMeta = s + "-meta.xml";
    }

    /**
     * DOCUMENT ME!
     *
     * @param childType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public short getChildType(short childType) throws Exception {
        if ((prefix == null) || !prefix.startsWith("@")) {
            return AbstractParentChildCreator.BRANCH_NODE;
        }

        return childType;
    }

    /**
     *  evaluate prefix:<br>
     *   
     *   &lt;prefix&gt;-   :  &lt;prefix&gt;childId<br>
     *   -&lt;prefix&gt;   :  childId.&lt;prefix&gt;<br>
     *   @&lt;prefix&gt;   :  any function (returns @&lt;prefix&gt; if function not implemented)<br>
     *   /&lt;prefix&gt;/..:  recursive call of evalPrefix<br>
     */
    private String evalPrefix(String prefix, String childId) {
        if (prefix.startsWith("/")) {
            String s = prefix.substring(1);
            int i = s.indexOf("/");

            if (i < 0) {
                return evalPrefix(s, childId);
            }

            return evalPrefix(s.substring(0, i), childId) + "/" +
            evalPrefix(s.substring(i), childId);

            // without childid
        }

        if (prefix.startsWith("@")) {
            if (prefix.equals("@millis")) {
                return "" + System.currentTimeMillis();
            }

            return prefix; // unknown      
        }

        if (prefix.startsWith("-")) {
            return childId + "." + prefix.substring(1); //        
        }

        if (prefix.endsWith("-")) {
            return prefix.substring(0, prefix.length() - 1) + childId;
        }

        return prefix;
    }

    /**
     * DOCUMENT ME!
     *
     * @param childId DOCUMENT ME!
     * @param childType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public String generateTreeId(String childId, short childType)
        throws Exception {
        if (prefix != null) {
            return evalPrefix(prefix, childId);
        }

        if (childType == AbstractParentChildCreator.BRANCH_NODE) {
            return childId;
        }

        return childId + ".xml";
    }

    /**
     * DOCUMENT ME!
     *
     * @param samplesDir DOCUMENT ME!
     * @param parentDir DOCUMENT ME!
     * @param childId DOCUMENT ME!
     * @param childType DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void create(File samplesDir, File parentDir,
		       String childId, short childType, String childName)
        throws Exception {
        String filename = null;
        String filenameMeta = null;

        if (prefix == null) {
            if (childType == AbstractParentChildCreator.BRANCH_NODE) {
                filename = parentDir + "/" + childId + "/index.xml";
                filenameMeta = parentDir + "/" + childId + "/index-meta.xml";
            } else if (childType == AbstractParentChildCreator.LEAF_NODE) {
                filename = parentDir + "/" + childId + ".xml";
                filenameMeta = parentDir + "/" + childId + "-meta.xml";
            }
        } else {
            filename = parentDir + "/" + generateTreeId(childId, childType) + fname;
            filenameMeta = parentDir + "/" + generateTreeId(childId, childType) + fnameMeta;
        }

        String doctypeSample = samplesDir + "/" + docNameSample; //  "/Group.xml"
        String doctypeMeta = samplesDir + "/" + docNameMeta; //  Meta.xl
        copyFile(new File(doctypeSample), new File(filename));
        copyFile(new File(doctypeMeta), new File(filenameMeta));
    }
}
