/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
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

/* $Id: NewBlogEntryCreator.java 473841 2006-11-12 00:46:38Z gregor $  */

package org.apache.lenya.cms.authoring;

import org.apache.log4j.Category;

import org.apache.avalon.framework.configuration.Configuration;

import org.w3c.dom.Document;

import org.apache.lenya.ac.Identity;
import org.apache.lenya.xml.DOMUtil;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Date;

import org.apache.lenya.cms.publication.Publication;

/**
 * Create a blog entry
 */
public class NewBlogEntryCreator extends DefaultBranchCreator {
    private static Category log = Category.getInstance(NewBlogEntryCreator.class);

    private String year;
    private String month;
    private String day;
    private Date date;

    /**
     *
     */
    public void init(Configuration conf) {
        super.init(conf);

        DateFormat fmtyyyy = new SimpleDateFormat("yyyy");
        DateFormat fmtMM = new SimpleDateFormat("MM");
        DateFormat fmtdd = new SimpleDateFormat("dd");
        date = new Date();

        year = fmtyyyy.format(date);
        month = fmtMM.format(date);
        day = fmtdd.format(date);

        log.debug(".init(): Initialize Creator: " + year + "/" + month + "/" + day);
    }

    /**
     *
     */
    protected String getChildFileName(Publication publication, String area, String parentId, String childId, String language) {
        // TODO: Somehow the first character is being cut off!
        String newFilename = publication.getPathMapper().getFile(publication, area, "eentries" + File.separator + year + File.separator + month + "/" + day + "/" + childId + "/index", null).getAbsolutePath();
        log.debug(newFilename);
        return newFilename;
    }

    /**
     *
     */
    protected void transformXML(Document doc, String childId, short childType, String childName, Map parameters) throws Exception {
        log.debug(".transformXML(): " + childId);
        DOMUtil du = new DOMUtil();

        // Replace id
        du.setElementValue(doc, "/echo:entry/echo:id", "tag:bob.blog," + year + ":" + month + ":" + day + ":" + childId);

        // Replace title 
        du.setElementValue(doc, "/echo:entry/echo:title", (String)parameters.get("title"));

        // Replace Summary
        du.setElementValue(doc, "/echo:entry/echo:summary", "Summary");

	// Replace link:
        du.setAttributeValue(doc, "/echo:entry/echo:link/@rel",  "alternate");
        du.setAttributeValue(doc, "/echo:entry/echo:link/@href", "http://bob.blog/");
        du.setAttributeValue(doc, "/echo:entry/echo:link/@type", "text/xml");

        // Replace author
        Identity identity = (Identity)parameters.get("org.apache.lenya.ac.Identity");
        du.setElementValue(doc, "/echo:entry/echo:author/echo:name", identity.getUser().getId());

        // Replace date created (and issued and modified, FIXME: issued should be set during first time publishing, modified should be set during re-publishing)
        DateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        DateFormat ofsfmt = new SimpleDateFormat("Z");

        String dateofs = ofsfmt.format(date);
        String datestr = datefmt.format(date) + dateofs.substring(0, 3) + ":" + dateofs.substring(3, 5);

        du.setElementValue(doc, "/echo:entry/echo:created", datestr);
        du.setElementValue(doc, "/echo:entry/echo:issued", datestr);
        du.setElementValue(doc, "/echo:entry/echo:modified", datestr);
    }
}
