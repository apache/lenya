/*
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
 *    by lenya (http://cocoon.apache.org/lenya/)"
 *
 * 4. The name "lenya" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact board@apache.org
 *
 * 5. Products derived from this software may not be called "lenya" nor may "lenya"
 *    appear in their names without prior written permission of lenya.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by lenya (http://cocoon.apache.org/lenya/)"
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
package org.apache.lenya.cms.authoring;

import org.apache.log4j.Category;

import org.apache.avalon.framework.configuration.Configuration;

import org.w3c.dom.Document;

import org.apache.lenya.cms.ac2.Identity;
import org.apache.lenya.cms.authoring.DefaultBranchCreator;
import org.apache.lenya.xml.DOMUtil;
import org.apache.lenya.util.DateUtil;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;


/**
 * @author Michael Wechner
 * @version $Id: NewBlogEntryCreator.java,v 1.2 2003/08/11 22:19:25 michi Exp $
 */
public class NewBlogEntryCreator extends DefaultBranchCreator {
    private static Category log = Category.getInstance(NewBlogEntryCreator.class);

    private String year;
    private String month;
    private String day;

    /**
     *
     */
    public void init(Configuration conf) {
        super.init(conf);

        Calendar cal = new GregorianCalendar();
        year = Integer.toString(cal.get(Calendar.YEAR));
        month = DateUtil.oneToTwoDigits(Integer.toString(cal.get(Calendar.MONTH) + 1));
        day = DateUtil.oneToTwoDigits(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));

        log.error(".init(): Initialize Creator: " + year + "/" + month + "/" + day);
    }

    /**
     *
     */
    protected String getChildFileName(File parentDir, String childId, String language) {
        String newFilename = parentDir + File.separator + "entries" + File.separator + year + File.separator + month + "/" + day + "/" + childId + "/index.xml";
        log.error(".getChildFileName(): " + newFilename);
        return newFilename;
    }

    /**
     *
     */
    protected void transformXML (Document doc, String childId, short childType, String childName, Map parameters) throws Exception {
        log.error(".transformXML(): " + childId);
        DOMUtil du = new DOMUtil();

        // Replace id
        du.setElementValue(doc, "/echo:entry/echo:id", year + "/" + month + "/" + day + "/" + childId);


        // Replace title 
        du.setElementValue(doc, "/echo:entry/echo:title", (String)parameters.get("title"));

        // Replace author
        Identity identity = (Identity)parameters.get("org.apache.lenya.cms.ac2.Identity");
        du.setElementValue(doc, "/echo:entry/echo:author/echo:name", identity.getUser().getId());

        // Replace date created (and issued and modified, FIXME: issued should be set during first time publishing, modified should be set during re-publishing)
        String date = org.apache.lenya.util.DateUtil.getCurrentDate();
        du.setElementValue(doc, "/echo:entry/echo:created", date);
        du.setElementValue(doc, "/echo:entry/echo:issued", date);
        du.setElementValue(doc, "/echo:entry/echo:modified", date);
    }
}
