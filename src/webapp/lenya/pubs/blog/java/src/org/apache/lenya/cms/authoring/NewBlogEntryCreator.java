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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;

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

    private String year;
    private String month;
    private String day;
    private Date date;

    /**
     * @see org.apache.lenya.cms.authoring.DefaultBranchCreator#init(Configuration, ServiceManager, Logger)
     */
    public void init(Configuration conf, ServiceManager _manager, Logger _logger) {
        super.init(conf, _manager, _logger);

        DateFormat fmtyyyy = new SimpleDateFormat("yyyy");
        DateFormat fmtMM = new SimpleDateFormat("MM");
        DateFormat fmtdd = new SimpleDateFormat("dd");
        date = new Date();

        year = fmtyyyy.format(date);
        month = fmtMM.format(date);
        day = fmtdd.format(date);

        if (getLogger().isDebugEnabled())
            getLogger().debug("NewBlogEntryCreator.init(): " + year + "/" + month + "/" + day);
    }

    /**
     * The blog publication has a specific site structuring: 
     * it uses the filesystem hierarchy to group nodes by date.
     * In particular, the new URI of a blog entry is not dependent upon a "parent", so parameter <code>parentId</code> is unused.
     *
     * <p>Example structuring of blog entries:</p>
     * <ul>
     *  <li>2004</li>
     *  <li>2005</li>
     *    <ul>
     *      <li>01</li>
     *      <li>02</li>
     *      <ul>
     *        <li>23</li>
     *        <li>24</li>
     *          <ul>
     *            <li>article-one</li>
     *            <li>article-two</li>
     *          </ul>
     *      </ul>
     *    </ul>
     * </ul>
     * 
     * @see org.apache.lenya.cms.authoring.NodeCreatorInterface#getNewDocumentURI(String, String, String, String)
     */
    public String getNewDocumentURI(
        String contentBaseURI,
        String parentId,
        String newId,
        String language) {
        return
           contentBaseURI
           + "/" 
           + "entries" 
           + "/" 
           + year 
           + "/" 
           + month 
           + "/" 
           + day 
           + "/" 
           + newId 
           + "/" 
           + "index.xml";
    }

    /**
     *
     */
    protected void transformXML(Document doc, String childId, short childType, String childName, Map parameters) throws Exception {

       // sanity check: blog entry creation depends on certain parameters
       if (parameters == null)
           throw new IllegalArgumentException("parameters may not be null for blog entry creation");

       Element parent = doc.getDocumentElement();

       if (getLogger().isDebugEnabled())
           getLogger().debug("NewBlogEntryCreator.transformXML(): " + childId);

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
