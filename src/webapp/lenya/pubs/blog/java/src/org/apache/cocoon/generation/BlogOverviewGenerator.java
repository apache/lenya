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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.lenya.cms.cocoon.source.SourceUtil;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.SiteManager;
import org.apache.lenya.xml.DocumentHelper;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * BlogOverviewGenerator
 * 
 * Builds an ordered tree from the blog entries in the
 * repository, allows simple queries.
 * 
 */
public class BlogOverviewGenerator extends ServiceableGenerator {

    /** The URI of the namespace of this generator. */
    protected static final String URI = "http://apache.org/cocoon/blog/1.0";

    /** The namespace prefix for this namespace. */
    protected static final String PREFIX = "blog";

    /** Node and attribute names */
    protected static final String BLOG_NODE_NAME = "overview";
    
    protected static final String ENTRY_NODE_NAME = "entry";

    protected static final String DOCID_ATTR_NAME = "docid";

    protected static final String URL_ATTR_NAME = "url";
    
    protected static final String TITLE_ATTR_NAME = "title";
    
    protected static final String LASTMOD_ATTR_NAME = "lastModified";

    protected static final String STRUCT_ATTR_NAME = "structure";
    
    protected static final String YEAR_NODE_NAME = "year";

    protected static final String MONTH_NODE_NAME = "month";

    protected static final String DAY_NODE_NAME = "day";

    protected static final String ID_ATTR_NAME = "id";

    
    /**
     * Convenience object, so we don't need to create an AttributesImpl for
     * every element.
     */
    protected AttributesImpl attributes;

    /**
     * The Lenya-Area where the generator should work on
     */
    protected String area;

    /**
     * Request parameters
     */
    protected int  year;
    protected int  month;
    protected int  day;        
    
    protected String structure;
    
    /**
     * The request
     */
    protected Request request;
    
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

        request = ObjectModelHelper.getRequest(this.objectModel);
                
        String param = request.getParameter("year");
        if (param != null) 
            year = Integer.parseInt(param);
        else
            year = 0;
        param = request.getParameter("month");
        if (param != null) 
            month = Integer.parseInt(param);
        else
            month = 0;
        param = request.getParameter("day");
        if (param != null) 
            day = Integer.parseInt(param);
        else
            day = 0;
    
        structure = request.getParameter("struct");
        if (structure != null) {
            year = month = day = 0;
        }
                
        area = par.getParameter("area", null);
        if (area == null)
            throw new ProcessingException("no area specified");               
        
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

        if (structure != null) {
            attributes.addAttribute("", STRUCT_ATTR_NAME,
                    STRUCT_ATTR_NAME, "CDATA", String.valueOf(structure));
        }
        this.contentHandler.startElement(URI, BLOG_NODE_NAME, PREFIX + ':'
                + BLOG_NODE_NAME, attributes);

        ServiceSelector selector = null;
        SiteManager siteManager = null;
        try {            
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
            ArrayList filteredDocs = new ArrayList(1);          
            for (int i=0; i<docs.length; i++) {
                String docId = docs[i].getId();
                if (docId.startsWith("/entries/")) {
                    int eYear = 0;
                    int eMonth = 0;
                    int eDay = 0;
                    boolean add = false;
                    String[] patterns = docId.split("/");
                    eYear = Integer.parseInt(patterns[2]);
                    eMonth = Integer.parseInt(patterns[3]);
                    eDay = Integer.parseInt(patterns[4]);
                    /* determine matching documents */
                    if (year > 0) {
                        if (year == eYear) {
                            if (month > 0) {
                                if (month == eMonth) {
                                    if (day > 0) {
                                        if (day == eDay) {
                                            /* add */
                                            add = true;
                                        }
                                    } else {
                                        /* add */
                                        add = true;
                                    }
                                }
                            } else {
                                if (day > 0) {
                                    if (day == eDay) {
                                        /* add */
                                        add = true;
                                    }
                                } else {
                                    /* add */
                                    add = true;
                                }
                            }
                        }
                    } else if (month > 0l) {
                        if (month == eMonth) {
                            if (day > 0) {
                                if (day == eDay) {
                                    /* add */
                                    add = true;
                                }
                            } else {
                                /* add */
                                add = true;
                            }
                        }
                    } else {
                        if (day > 0) {
                            if (day == eDay) {
                                /* add */
                                add = true;
                            }
                        } else {
                            /* add */
                            add = true;
                        }
                    }
                    if (add) {                       
                        filteredDocs.add((Object)docs[i]);                        
                    }
                }                                
            }

            /* sort entries by year -> month -> day -> lastModified */
            Object[] sortedList = filteredDocs.toArray();            
            Arrays.sort(sortedList, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Document d1,d2;
                    int year1,month1,day1;
                    int year2,month2,day2;
                    
                    d1 = (Document)o1;
                    d2 = (Document)o2;
                    
                    String[] patterns = d1.getId().split("/");                    
                    year1 = Integer.parseInt(patterns[2]);
                    month1 = Integer.parseInt(patterns[3]);
                    day1 = Integer.parseInt(patterns[4]);
                    
                    patterns = d2.getId().split("/");                    
                    year2 = Integer.parseInt(patterns[2]);
                    month2 = Integer.parseInt(patterns[3]);
                    day2 = Integer.parseInt(patterns[4]);                    
                                        
                    if (year1 > year2) {
                        return 1;
                    } else if (year1 == year2) {
                        if (month1 > month2) {
                            return 1;
                        } else if (month1 == month2) {
                            if (day1 > day2) {
                                return 1;
                            } else if (day1 == day2) {
                                /* newest first */
                                return d2.getLastModified().compareTo(d1.getLastModified());
                            } else {
                                return -1;
                            }
                        } else {
                            return -1;
                        }
                    } else {
                        return -1;
                    }
                }
            });            
            
            /* group entries by year -> month -> day */
            /* works because the list is sorted =) */
            int currentYear = 0;
            int currentMonth = 0;
            int currentDay = 0;
            boolean yearOpen = false;
            boolean monthOpen = false;
            boolean dayOpen = false;            
            for (int i=0; i<sortedList.length; i++) {
                Document doc = ((Document)sortedList[i]);
                String[] patterns = doc.getId().split("/");                   
                int year =  Integer.parseInt(patterns[2]);
                int month = Integer.parseInt(patterns[3]);
                int day = Integer.parseInt(patterns[4]);
                if (year != currentYear) {
                    if (dayOpen) {
                        dayOpen = false;
                        this.contentHandler.endElement(URI, DAY_NODE_NAME, PREFIX + ':'
                                + DAY_NODE_NAME);
                    }
                    if (monthOpen) {
                        monthOpen = false;
                        this.contentHandler.endElement(URI, MONTH_NODE_NAME, PREFIX + ':'
                                + MONTH_NODE_NAME);
                    }
                    if (yearOpen) {
                        this.contentHandler.endElement(URI, YEAR_NODE_NAME, PREFIX + ':'
                                + YEAR_NODE_NAME);
                    }
                    this.attributes.clear();
                    attributes.addAttribute("", ID_ATTR_NAME,
                            ID_ATTR_NAME, "CDATA", String.valueOf(year));
                    this.contentHandler.startElement(URI, YEAR_NODE_NAME, PREFIX + ':'
                            + YEAR_NODE_NAME, attributes);
                    yearOpen = true;
                    currentYear = year;
                    currentMonth = 0;
                    currentDay = 0;                    
                } 
                if (month != currentMonth) {
                    if (dayOpen) {
                        dayOpen = false;
                        this.contentHandler.endElement(URI, DAY_NODE_NAME, PREFIX + ':'
                                + DAY_NODE_NAME);
                    }
                    if (monthOpen) {
                        this.contentHandler.endElement(URI, MONTH_NODE_NAME, PREFIX + ':'
                                + MONTH_NODE_NAME);
                    }
                    this.attributes.clear();
                    attributes.addAttribute("", ID_ATTR_NAME,
                            ID_ATTR_NAME, "CDATA", String.valueOf(month));
                    this.contentHandler.startElement(URI, MONTH_NODE_NAME, PREFIX + ':'
                            + MONTH_NODE_NAME, attributes);
                    monthOpen = true;
                    currentMonth = month;
                    currentDay = 0;
                } 
                if (day != currentDay) {
                    if (dayOpen) {
                        this.contentHandler.endElement(URI, DAY_NODE_NAME, PREFIX + ':'
                                + DAY_NODE_NAME);
                    }
                    this.attributes.clear();
                    attributes.addAttribute("", ID_ATTR_NAME,
                            ID_ATTR_NAME, "CDATA", String.valueOf(day));
                    this.contentHandler.startElement(URI, DAY_NODE_NAME, PREFIX + ':'
                            + DAY_NODE_NAME, attributes);
                    dayOpen = true;
                    currentDay = day;
                }                
                if (structure == null) {
                    attributes.clear();
                    attributes.addAttribute("", DOCID_ATTR_NAME,
                            DOCID_ATTR_NAME, "CDATA", doc.getId());
                    attributes.addAttribute("", URL_ATTR_NAME,
                            URL_ATTR_NAME, "CDATA", doc.getCanonicalWebappURL());
                    org.w3c.dom.Document docDOM = SourceUtil.readDOM(doc.getSourceURI(), this.manager);
                    Element parent = docDOM.getDocumentElement();
                    Element element = (Element) XPathAPI.selectSingleNode(parent,
                        "/*[local-name() = 'entry']/*[local-name() = 'title']");
                    attributes.addAttribute("", TITLE_ATTR_NAME,
                            TITLE_ATTR_NAME, "CDATA",DocumentHelper.getSimpleElementText(element));
                    attributes.addAttribute("", LASTMOD_ATTR_NAME,
                            LASTMOD_ATTR_NAME, "CDATA", String.valueOf(doc.getLastModified().getTime())); 
                    DocumentHelper.getSimpleElementText(element);
                    this.contentHandler.startElement(URI, ENTRY_NODE_NAME,
                            PREFIX + ':' + ENTRY_NODE_NAME, attributes);
                    this.contentHandler.endElement(URI, ENTRY_NODE_NAME, PREFIX
                            + ':' + ENTRY_NODE_NAME);
                }
            }
            
            if (dayOpen) {
                this.contentHandler.endElement(URI, DAY_NODE_NAME, PREFIX + ':'
                        + DAY_NODE_NAME);
            }
            if (monthOpen) {
                this.contentHandler.endElement(URI, MONTH_NODE_NAME, PREFIX + ':'
                        + MONTH_NODE_NAME);
            }
            if (yearOpen) {
                this.contentHandler.endElement(URI, YEAR_NODE_NAME, PREFIX + ':'
                        + YEAR_NODE_NAME);
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
