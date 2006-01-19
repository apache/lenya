/*
 * Copyright  1999-2005 The Apache Software Foundation
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

package org.apache.lenya.cms.authoring;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.lenya.ac.Identity;
import org.apache.lenya.xml.DocumentHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Date;

/**
 * Creator a blog entry
 * @version $Id$
 */
public class NewBlogEntryCreator extends DefaultBranchCreator {

    /**
     * @see org.apache.lenya.cms.authoring.DefaultCreator#transformXML(org.w3c.dom.Document,
     *      org.apache.lenya.cms.publication.Document, java.util.Map)
     */
    public void transformXML(Document doc,
            org.apache.lenya.cms.publication.Document document,
            Map parameters) throws Exception {

        // sanity check: blog entry creation depends on certain parameters
        if (parameters == null)
            throw new IllegalArgumentException("parameters may not be null for blog entry creation");

        Element parent = doc.getDocumentElement();

        if (getLogger().isDebugEnabled())
            getLogger().debug("NewBlogEntryCreator.transformXML(): " + document);

        String[] steps = document.getId().split("/");
        String nodeId = steps[5];

        // Replace id
        Element element = (Element) XPathAPI.selectSingleNode(parent,
                "/*[local-name() = 'entry']/*[local-name() = 'id']");

        String year = steps[2];
        String month = steps[3];
        String day = steps[4];

        DocumentHelper.setSimpleElementText(element, year + "/" + month + "/"
                + day + "/" + nodeId);

        // Replace title
        element = (Element) XPathAPI.selectSingleNode(parent,
                "/*[local-name() = 'entry']/*[local-name() = 'title']");
        DocumentHelper.setSimpleElementText(element, (String) parameters.get("title"));

        element = (Element) XPathAPI.selectSingleNode(parent,
                "/*[local-name() = 'entry']/*[local-name() = 'link']");
        element.setAttribute("rel", "alternate");
        element.setAttribute("href", "http://bob.blog");
        element.setAttribute("type", "text/xml");

        // Replace Summary
        element = (Element) XPathAPI.selectSingleNode(parent,
                "/*[local-name() = 'entry']/*[local-name() = 'summary']");
        DocumentHelper.setSimpleElementText(element, "Summary");

        // Replace author
        Identity identity = (Identity) parameters.get(Identity.class.getName());

        element = (Element) XPathAPI.selectSingleNode(parent,
                "/*[local-name() = 'entry']/*[local-name() = 'author']/*[local-name() = 'name']");

        if (element == null) {
            throw new RuntimeException("Element entry/author/name not found.");
        }

        DocumentHelper.setSimpleElementText(element, identity.getUser().getId());

        // Replace date created, issued and modified
        DateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        DateFormat ofsfmt = new SimpleDateFormat("Z");
        Date date = new Date();

        String dateofs = ofsfmt.format(date);
        String datestr = datefmt.format(date) + dateofs.substring(0, 3) + ":"
                + dateofs.substring(3, 5);

        element = (Element) XPathAPI.selectSingleNode(parent,
                "/*[local-name() = 'entry']/*[local-name() = 'created']");
        DocumentHelper.setSimpleElementText(element, datestr);
        element = (Element) XPathAPI.selectSingleNode(parent,
                "/*[local-name() = 'entry']/*[local-name() = 'issued']");
        DocumentHelper.setSimpleElementText(element, datestr);
        element = (Element) XPathAPI.selectSingleNode(parent,
                "/*[local-name() = 'entry']/*[local-name() = 'modified']");
        DocumentHelper.setSimpleElementText(element, datestr);
    }
}