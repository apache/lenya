/*
 * $Id: EditorProjectCreator.java,v 1.4 2003/02/17 12:55:18 egli Exp $
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
package org.wyona.cms.pubs.oscom;

import org.apache.log4j.Category;

import org.dom4j.Document;
import org.dom4j.Element;

import org.dom4j.io.SAXReader;

import org.wyona.cms.authoring.AbstractParentChildCreator;

import org.wyona.util.DateUtil;

import java.io.File;
import java.io.FileWriter;

import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * DOCUMENT ME!
 *
 * @author Michael Wechner
 * @version 2002.12.29
 */
public class EditorProjectCreator extends AbstractParentChildCreator {
    static Category log = Category.getInstance(EditorProjectCreator.class);

    /**
     * Why do we have to overwrite this method?
     *
     * @param childType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public short getChildType(short childType) throws Exception {
        return AbstractParentChildCreator.LEAF_NODE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param childname DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public String getChildName(String childname) throws Exception {
        return childname;
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
        return childId;
    }

    /**
     * Create the new editor project
     *
     * @param samplesDir DOCUMENT ME!
     * @param parentDir DOCUMENT ME!
     * @param childId DOCUMENT ME!
     * @param childType DOCUMENT ME!
     * @param childName DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void create(File samplesDir, File parentDir, String childId, short childType,
        String childName) throws Exception {
        // Set filenames
        String id = generateTreeId(childId, childType);
        String filename = parentDir + "/" + id + ".xml";
        String doctypeSample = samplesDir + "/EditorProject.xml";

        // Read sample file
        log.debug(".create(): Try to read file: " + doctypeSample);

        Document doc = new SAXReader().read("file:" + doctypeSample);

        // Replace id
        Element eid = (Element) doc.selectSingleNode("/system/id");

        if (eid != null) {
            log.debug(eid.getPath() + " " + eid.getText());
            eid.addText(id);
            log.debug(eid.getPath() + " " + eid.getText());
        }

        // Replace name
        Element ename = (Element) doc.selectSingleNode("/system/system_name");

        if (ename != null) {
            log.debug(ename.getPath() + " " + ename.getText());
            ename.addText(childName);
            log.debug(ename.getPath() + " " + ename.getText());
        }

        // Write file
        File parent = new File(new File(filename).getParent());

        if (!parent.exists()) {
            parent.mkdirs();
        }

        FileWriter fileWriter = new FileWriter(filename);
        doc.write(fileWriter);
        fileWriter.close();
    }
}
