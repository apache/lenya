/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.generation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.SiteManager;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Blog entry generator
 */
public class BlogGenerator extends ServiceableGenerator {

    /** The URI of the namespace of this generator. */
    protected static final String URI = "http://apache.org/cocoon/blog/1.0";

    /** The namespace prefix for this namespace. */
    protected static final String PREFIX = "blog";

    /** Node and attribute names */
    protected static final String BLOG_NODE_NAME = "blog";

    protected static final String ENTRY_NODE_NAME = "entry";

    protected static final String DOCID_ATTR_NAME = "docid";

    protected static final String LASTMOD_ATTR_NAME = "lastModified";

    /**
     * Convenience object, so we don't need to create an AttributesImpl for
     * every element.
     */
    protected AttributesImpl attributes;

    /**
     * The Lenya-Area where the generator should work on
     */
    protected String area;

    protected boolean recent;
    
    /**
     * Only generate the #numrecent entries
     */
    protected int numrecent; 
    
    /**
     * Set the request parameters. Must be called before the generate method.
     * 
     * @param resolver
     *            the SourceResolver object
     * @param objectModel
     *            a <code>Map</code> containing model object
     * @param src
     *            the source URI (ignored)
     * @param par
     *            configuration parameters
     */
    public void setup(SourceResolver resolver, Map objectModel, String src,
            Parameters par) throws ProcessingException, SAXException,
            IOException {
        super.setup(resolver, objectModel, src, par);

        area = par.getParameter("area", null);
        if (area == null)
            throw new ProcessingException("no area specified");               
        if (area.equals(Publication.LIVE_AREA)) 
            numrecent = 16;
        else 
            numrecent = 0;
        
        this.attributes = new AttributesImpl();
    }

    /**
     * Generate XML data.
     * 
     * @throws SAXException
     *             if an error occurs while outputting the document
     */
    public void generate() throws SAXException, ProcessingException {

        this.contentHandler.startDocument();
        this.contentHandler.startPrefixMapping(PREFIX, URI);
        attributes.clear();

        this.contentHandler.startElement(URI, BLOG_NODE_NAME, PREFIX + ':'
                + BLOG_NODE_NAME, attributes);

        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {
            Request request = ObjectModelHelper.getRequest(this.objectModel);
            Session session = RepositoryUtil.getSession(request, this
                    .getLogger());
            DocumentIdentityMap map = new DocumentIdentityMap(session,
                    this.manager, this.getLogger());
            Publication publication = PublicationUtil.getPublication(
                    this.manager, request);
                       
            selector = (ServiceSelector) this.manager.lookup(SiteManager.ROLE
                    + "Selector");
            siteManager = (SiteManager) selector.select(publication
                    .getSiteManagerHint());

            Document[] docs = siteManager.getDocuments(map, publication, area);
            Arrays.sort((Object[]) docs, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Document) o2).getLastModified().compareTo(
                            ((Document) o1).getLastModified());
                }
            });

            for (int i = 0; i < docs.length; i++) {                
                if (numrecent > 0 && numrecent <= i)                     
                    break;                
                if (docs[i].getId().startsWith("/entries/")) {
                    attributes.clear();
                    attributes.addAttribute("", DOCID_ATTR_NAME,
                            DOCID_ATTR_NAME, "CDATA", docs[i].getId());
                    attributes.addAttribute("", LASTMOD_ATTR_NAME,
                            LASTMOD_ATTR_NAME, "CDATA", String.valueOf(docs[i].getLastModified().getTime()));
                    
                    this.contentHandler.startElement(URI, ENTRY_NODE_NAME,
                            PREFIX + ':' + ENTRY_NODE_NAME, attributes);
                    this.contentHandler.endElement(URI, ENTRY_NODE_NAME, PREFIX
                            + ':' + ENTRY_NODE_NAME);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (selector != null) {
                if (siteManager != null) {
                    selector.release(siteManager);
                }
                this.manager.release(selector);
            }
        }

        this.contentHandler.endElement(URI, BLOG_NODE_NAME, PREFIX + ':'
                + BLOG_NODE_NAME);

        this.contentHandler.endPrefixMapping(PREFIX);
        this.contentHandler.endDocument();
    }
}
