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

package org.apache.lenya.cms.authoring;

import org.apache.log4j.Category;

import org.apache.avalon.framework.configuration.Configuration;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.lenya.ac.Identity;
import org.apache.lenya.xml.DocumentHelper;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

        log.debug(".init(): Initialize Creator: " + year + "/" + month + "/" + day);
    }

    /**
     *
     */
    protected String getChildFileName(File parentDir, String childId, String language) {
        String newFilename = parentDir + File.separator + "entries" + File.separator + year + File.separator + month + File.separator + day + File.separator + childId + File.separator + "index.xml";
        log.debug(".getChildFileName(): " + newFilename);
        return newFilename;
    }

    /**
     *
     */
    protected void transformXML(Document doc, String childId, short childType, String childName, Map parameters) throws Exception {
       Element parent = doc.getDocumentElement();
       log.debug(".transformXML(): " + childId);

       // Replace id
        Element element = (Element) XPathAPI.selectSingleNode(parent, "/*[local-name() = 'entry']/*[local-name() = 'id']");
        DocumentHelper.setSimpleElementText(element, "tag:bob.blog," + year + ":" + month + ":" + day + ":" + childId);
        
        // Replace title 
        element = (Element) XPathAPI.selectSingleNode(parent, "/*[local-name() = 'entry']/*[local-name() = 'title']");
        DocumentHelper.setSimpleElementText(element, (String)parameters.get("title"));

        element = (Element) XPathAPI.selectSingleNode(parent, "/*[local-name() = 'entry']/*[local-name() = 'link']");
        element.setAttribute("rel","alternate");
        element.setAttribute("href","http://bob.blog");
        element.setAttribute("type","text/xml");

        // Replace Summary
        element = (Element) XPathAPI.selectSingleNode(parent, "/*[local-name() = 'entry']/*[local-name() = 'summary']");
        DocumentHelper.setSimpleElementText(element, "Summary");


        // Replace author
        Identity identity = (Identity) parameters.get(Identity.class.getName());
        
        element = (Element) XPathAPI.selectSingleNode(parent, "/*[local-name() = 'entry']/*[local-name() = 'author']/*[local-name() = 'name']");
        
        if (element == null) {
            throw new RuntimeException("Element entry/author/name not found.");
        }
        
        DocumentHelper.setSimpleElementText(element, identity.getUser().getId());

        // Replace date created (and issued and modified, FIXME: issued should be set during first time publishing, modified should be set during re-publishing)
        DateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        DateFormat ofsfmt = new SimpleDateFormat("Z");

        String dateofs = ofsfmt.format(date);
        String datestr = datefmt.format(date) + dateofs.substring(0, 3) + ":" + dateofs.substring(3, 5);

        element = (Element) XPathAPI.selectSingleNode(parent, "/*[local-name() = 'entry']/*[local-name() = 'created']");
        DocumentHelper.setSimpleElementText(element, datestr);
        element = (Element) XPathAPI.selectSingleNode(parent, "/*[local-name() = 'entry']/*[local-name() = 'issued']");
        DocumentHelper.setSimpleElementText(element, datestr);
        element = (Element) XPathAPI.selectSingleNode(parent, "/*[local-name() = 'entry']/*[local-name() = 'modified']");
        DocumentHelper.setSimpleElementText(element, datestr);
    }
}
