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

/* $Id: NewBlogEntryCreator.java,v 1.5 2004/03/20 12:08:56 gregor Exp $  */

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
    protected void transformXML(Document doc, String childId, short childType, String childName, Map parameters) throws Exception {
        log.error(".transformXML(): " + childId);
        DOMUtil du = new DOMUtil();

        // Replace id
        du.setElementValue(doc, "/echo:entry/echo:id", year + "/" + month + "/" + day + "/" + childId);

        // Replace title 
        du.setElementValue(doc, "/echo:entry/echo:title", (String)parameters.get("title"));

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
